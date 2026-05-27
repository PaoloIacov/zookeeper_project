package org.apache.zookeeper.server.quorum.flexible;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per la classe QuorumHierarchical di Apache ZooKeeper.
 * Verifica la logica dei quorum gerarchici basati su gruppi e pesi.
 */
public class ContainsQuorum_QuorumHierarchicalLLMZeroShotInstructionTest {

    private Properties properties;
    private QuorumHierarchical quorumHierarchical;

    @BeforeEach
    public void setUp() throws Exception {
        properties = new Properties();

        // REGOLA DI DOMINIO: Uso obbligatorio dei due punti (:) come delimitatore, NON
        // la virgola.
        // Setup di una topologia di base: 3 gruppi da 3 server ciascuno (ID da 1 a 9)
        properties.setProperty("group.1", "1:2:3");
        properties.setProperty("group.2", "4:5:6");
        properties.setProperty("group.3", "7:8:9");

        // Assegnazione del peso 1 per ogni server
        for (int i = 1; i <= 9; i++) {
            properties.setProperty("weight." + i, "1");
        }

        // Inizializzazione della classe da testare
        quorumHierarchical = new QuorumHierarchical(properties);
    }

    @Test
    @DisplayName("Verifica l'inizializzazione corretta della topologia gerarchica e dei pesi")
    public void testValidInitialization() {
        // Verifica che il parser abbia letto e impostato correttamente i pesi
        for (long i = 1; i <= 9; i++) {
            assertEquals(1L, quorumHierarchical.getWeight(i),
                    "Il peso del server " + i + " dovrebbe essere stato inizializzato a 1.");
        }
    }

    @Test
    @DisplayName("Il quorum è valido con la maggioranza dei server in una maggioranza dei gruppi")
    public void testContainsQuorum_ValidQuorum() {
        Set<Long> ackSet = new HashSet<>();

        // Per 3 gruppi, la maggioranza è 2 gruppi.
        // Per gruppi di 3 server, la maggioranza è 2 server.

        // Maggioranza per il Gruppo 1
        ackSet.add(1L);
        ackSet.add(2L);

        // Maggioranza per il Gruppo 2
        ackSet.add(4L);
        ackSet.add(5L);

        assertTrue(quorumHierarchical.containsQuorum(ackSet),
                "Il quorum DOVREBBE essere raggiunto (maggioranza di server in 2 gruppi su 3).");
    }

    @Test
    @DisplayName("Il quorum fallisce se solo un gruppo raggiunge la maggioranza interna")
    public void testContainsQuorum_NoQuorum_NotEnoughGroups() {
        Set<Long> ackSet = new HashSet<>();

        // Tutti i server del Gruppo 1 confermano
        ackSet.add(1L);
        ackSet.add(2L);
        ackSet.add(3L);

        // Nessun server degli altri gruppi conferma

        assertFalse(quorumHierarchical.containsQuorum(ackSet),
                "Il quorum NON DOVREBBE essere raggiunto (solo 1 gruppo su 3 ha la maggioranza, ne servono 2).");
    }

    @Test
    @DisplayName("Il quorum fallisce se ci sono server in più gruppi, ma nessuna maggioranza interna ai gruppi")
    public void testContainsQuorum_NoQuorum_NotEnoughServersInGroup() {
        Set<Long> ackSet = new HashSet<>();

        // Solo un server per ogni gruppo conferma (1 su 3 non è maggioranza per un
        // gruppo)
        ackSet.add(1L); // Dal Gruppo 1
        ackSet.add(4L); // Dal Gruppo 2
        ackSet.add(7L); // Dal Gruppo 3

        assertFalse(quorumHierarchical.containsQuorum(ackSet),
                "Il quorum NON DOVREBBE essere raggiunto (nessun gruppo raggiunge la sua maggioranza locale).");
    }

    @Test
    @DisplayName("Costruttore lancia eccezione se la properties di gruppo usa la virgola invece dei due punti")
    public void testMalformedGroupString_ThrowsException() {
        Properties invalidProps = new Properties();

        // INIEZIONE DI ERRORE: uso della virgola invece dei due punti
        invalidProps.setProperty("group.1", "1,2,3");
        invalidProps.setProperty("weight.1", "1");
        invalidProps.setProperty("weight.2", "1");
        invalidProps.setProperty("weight.3", "1");

        // Ci aspettiamo che il parser generi un'eccezione a causa della formattazione
        // non valida
        assertThrows(Exception.class, () -> {
            new QuorumHierarchical(invalidProps);
        }, "Il costruttore dovrebbe lanciare una ConfigException a causa dell'uso del delimitatore errato (virgola).");
    }
}