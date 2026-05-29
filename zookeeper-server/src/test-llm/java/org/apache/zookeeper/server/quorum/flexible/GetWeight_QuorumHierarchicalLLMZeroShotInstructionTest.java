package org.apache.zookeeper.server.quorum.flexible;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Suite di test JUnit 5 per verificare la logica di assegnazione dei pesi
 * nella classe QuorumHierarchical di Apache ZooKeeper.
 */
public class GetWeight_QuorumHierarchicalLLMZeroShotInstructionTest {

    private QuorumHierarchical quorumHierarchical;

    @BeforeEach
    public void setUp() throws Exception {
        Properties qp = new Properties();

        // REGOLA DI DOMINIO: Utilizzo dei due punti (:) come delimitatore per gli ID
        // dei server
        // Configurazione di una topologia con 3 gruppi, composti da 3 server ciascuno.
        qp.setProperty("group.1", "1:2:3");
        qp.setProperty("group.2", "4:5:6");
        qp.setProperty("group.3", "7:8:9");

        // Assegnazione specifica dei pesi per i server del gruppo 1.
        qp.setProperty("weight.1", "3");
        qp.setProperty("weight.2", "2");
        qp.setProperty("weight.3", "0");

        // Inizializzazione della classe sottoposta a test.
        // Eventuali errori di sintassi nelle properties lanceranno una ConfigException.
        quorumHierarchical = new QuorumHierarchical(qp);
    }

    @Test
    @DisplayName("Dato un server con peso esplicito, getWeight deve restituire il valore esatto configurato")
    public void testGetWeight_WithExplicitlyConfiguredWeights() {
        assertEquals(3L, quorumHierarchical.getWeight(1), "Il peso del server 1 deve essere esattamente 3.");
        assertEquals(2L, quorumHierarchical.getWeight(2), "Il peso del server 2 deve essere esattamente 2.");
        assertEquals(0L, quorumHierarchical.getWeight(3), "Il peso del server 3 deve essere esattamente 0.");
    }

    @Test
    @DisplayName("Dato un server presente in un gruppo ma senza peso esplicito, getWeight deve restituire 1 di default")
    public void testGetWeight_WithUnconfiguredServerInGroup_ShouldReturnDefault() {
        // I server dei gruppi 2 e 3 non hanno la propertiy weight.x configurata
        assertEquals(1L, quorumHierarchical.getWeight(4),
                "Il peso di default per il server 4 (gruppo 2) deve essere 1.");
        assertEquals(1L, quorumHierarchical.getWeight(5),
                "Il peso di default per il server 5 (gruppo 2) deve essere 1.");
        assertEquals(1L, quorumHierarchical.getWeight(7),
                "Il peso di default per il server 7 (gruppo 3) deve essere 1.");
    }

    @Test
    @DisplayName("Dato un server ID non presente in alcun gruppo, getWeight deve restituire 0")
    public void testGetWeight_WithUnknownServer_ShouldReturnZero() {
        // Test per un server ID totalmente estraneo alla topologia configurata
        assertEquals(0L, quorumHierarchical.getWeight(99),
                "Il peso per un server inesistente nella topologia deve essere 0.");
        assertEquals(0L, quorumHierarchical.getWeight(-1),
                "Il peso per un server con ID negativo non configurato deve essere 0.");
    }
}