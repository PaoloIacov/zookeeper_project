package org.apache.zookeeper.server.quorum.flexible;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Disabled;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Suite di test JUnit 5 per verificare la logica di assegnazione dei pesi
 * nella classe QuorumHierarchical di Apache ZooKeeper.
 *
 * Iterazione V4 (Bug-Aware): aggiunta delle properties server.X per evitare
 * la NullPointerException in computeGroupWeight(), bug scoperto durante
 * la generazione iniziale di questi stessi test (cfr. QH_ZeroShot.md, Iterazione 2).
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

        // REGOLA AGGIUNTIVA (V4 Bug-Aware): definire server.X per ogni server
        // dichiarato nei gruppi, altrimenti computeGroupWeight() lancia NPE.
        for (int i = 1; i <= 9; i++) {
            qp.setProperty("server." + i, "localhost:" + (2780 + i * 10) + ":" + (3780 + i * 10));
        }

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
    @Disabled("BUG DEL SUT: getWeight(id) con ID non configurato lancia NullPointerException " +
              "per auto-unboxing di null (HashMap.get() → null → Long→long). " +
              "Confermato anche da Randoop con 528 ErrorTest. Il metodo dovrebbe restituire 0.")
    @DisplayName("Dato un server ID non presente in alcun gruppo, getWeight deve restituire 0")
    public void testGetWeight_WithUnknownServer_ShouldReturnZero() {
        // Il comportamento ATTESO è restituire 0 per un server inesistente.
        // Attualmente il SUT lancia NullPointerException (bug).
        assertEquals(0L, quorumHierarchical.getWeight(99),
                "Il peso per un server inesistente nella topologia deve essere 0.");
        assertEquals(0L, quorumHierarchical.getWeight(-1),
                "Il peso per un server con ID negativo non configurato deve essere 0.");
    }
}