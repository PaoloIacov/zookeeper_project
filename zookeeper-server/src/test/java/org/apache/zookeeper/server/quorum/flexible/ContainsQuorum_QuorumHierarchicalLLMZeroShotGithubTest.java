package org.apache.zookeeper.server.quorum.flexible;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Suite di test per il metodo containsQuorum della classe QuorumHierarchical
 * in Apache ZooKeeper.
 */
public class ContainsQuorum_QuorumHierarchicalLLMZeroShotGithubTest {

    private QuorumHierarchical quorumHierarchical;

    @BeforeEach
    public void setUp() throws Exception {
        Properties qp = new Properties();
        // Configurazione di default per i test: 3 gruppi da 3 server ciascuno.
        qp.setProperty("group.1", "1,2,3");
        qp.setProperty("group.2", "4,5,6");
        qp.setProperty("group.3", "7,8,9");

        // Assegnazione del peso 1 a ciascun server
        for (int i = 1; i <= 9; i++) {
            qp.setProperty("weight." + i, "1");
        }

        // Inizializzazione della classe sottoposta a test.
        // Il costruttore effettua il parsing delle Properties.
        quorumHierarchical = new QuorumHierarchical(qp);
    }

    @Test
    @DisplayName("Dovrebbe restituire true quando c'è una maggioranza dei pesi nella maggioranza dei gruppi")
    public void testContainsQuorum_MajorityInMajorityOfGroups() {
        // Maggioranza nel Gruppo 1 (server 1, 2) e nel Gruppo 2 (server 4, 5).
        // 2 gruppi su 3 hanno la maggioranza interna.
        Set<Long> ackSet = new HashSet<>(Arrays.asList(1L, 2L, 4L, 5L));
        assertTrue(quorumHierarchical.containsQuorum(ackSet),
                "Il quorum deve essere raggiunto con la maggioranza in 2 gruppi su 3");
    }

    @Test
    @DisplayName("Dovrebbe restituire false quando c'è maggioranza solo in una minoranza dei gruppi")
    public void testContainsQuorum_MajorityInMinorityOfGroups() {
        // Maggioranza assoluta nel Gruppo 1 (tutti i server: 1, 2, 3), ma zero voti
        // negli altri.
        // Solo 1 gruppo su 3 ha il quorum interno.
        Set<Long> ackSet = new HashSet<>(Arrays.asList(1L, 2L, 3L));
        assertFalse(quorumHierarchical.containsQuorum(ackSet),
                "Il quorum non deve essere raggiunto perché solo 1 gruppo su 3 ha la maggioranza");
    }

    @Test
    @DisplayName("Dovrebbe restituire false quando nessun gruppo raggiunge la propria maggioranza interna")
    public void testContainsQuorum_NoMajorityInAnyGroup() {
        // Un solo server per gruppo. Il peso totale di ogni gruppo è 3,
        // quindi un peso di 1 non è sufficiente (serve > 3/2, ovvero 2).
        Set<Long> ackSet = new HashSet<>(Arrays.asList(1L, 4L, 7L));
        assertFalse(quorumHierarchical.containsQuorum(ackSet),
                "Il quorum non deve essere raggiunto se nessun gruppo raggiunge il proprio quorum locale");
    }

    @Test
    @DisplayName("Dovrebbe restituire false con un insieme vuoto di acknowledgement")
    public void testContainsQuorum_EmptySet() {
        Set<Long> ackSet = new HashSet<>();
        assertFalse(quorumHierarchical.containsQuorum(ackSet),
                "Un insieme vuoto non può mai generare un quorum");
    }

    @Test
    @DisplayName("Dovrebbe sollevare NullPointerException se l'insieme passato è null")
    public void testContainsQuorum_NullSet() {
        assertThrows(NullPointerException.class, () -> quorumHierarchical.containsQuorum(null),
                "Il metodo senza un controllo esplicito sui null dovrebbe lanciare NullPointerException");
    }

    @Test
    @DisplayName("I server non registrati nella configurazione dovrebbero essere ignorati in sicurezza")
    public void testContainsQuorum_UnmappedServersIgnored() {
        // Maggioranza in G1 e G2 raggiunta. Il server 99 non appartiene a nessun
        // gruppo.
        Set<Long> ackSet = new HashSet<>(Arrays.asList(1L, 2L, 4L, 5L, 99L));
        assertTrue(quorumHierarchical.containsQuorum(ackSet),
                "I server non registrati (99L) devono essere ignorati senza impedire il quorum raggiunto dai server validi");
    }

    @Test
    @DisplayName("Dovrebbe restituire false se l'ack set contiene esclusivamente server sconosciuti")
    public void testContainsQuorum_OnlyUnmappedServers() {
        Set<Long> ackSet = new HashSet<>(Arrays.asList(98L, 99L, 100L));
        assertFalse(quorumHierarchical.containsQuorum(ackSet),
                "Dovrebbe fallire se gli unici voti provengono da nodi non censiti nei gruppi");
    }

    @Test
    @DisplayName("Deve calcolare accuratamente il quorum utilizzando pesi asimmetrici")
    public void testContainsQuorum_CustomAsymmetricWeights() throws Exception {
        Properties qp = new Properties();
        // Solo 2 gruppi
        qp.setProperty("group.1", "1,2,3");
        qp.setProperty("group.2", "4,5");

        // Pesi sbilanciati per il gruppo 1 (Totale peso = 6, quorum richiesto > 3)
        qp.setProperty("weight.1", "3");
        qp.setProperty("weight.2", "2");
        qp.setProperty("weight.3", "1");

        // Pesi omogenei per il gruppo 2 (Totale peso = 2, quorum richiesto > 1)
        qp.setProperty("weight.4", "1");
        qp.setProperty("weight.5", "1");

        QuorumHierarchical qhCustom = new QuorumHierarchical(qp);

        // Caso Positivo: G1 (Server 1 e 3: peso cumulato = 4) supera il quorum di G1.
        // G2 (Server 4 e 5: peso cumulato = 2) supera il quorum di G2.
        Set<Long> ackSetValid = new HashSet<>(Arrays.asList(1L, 3L, 4L, 5L));
        assertTrue(qhCustom.containsQuorum(ackSetValid),
                "Dovrebbe confermare il quorum, poiché i pesi validano le maggioranze su 2 gruppi su 2");

        // Caso Negativo: G1 (Server 2 e 3: peso cumulato = 3) NON supera il quorum (>
        // 3) di G1.
        // G2 ok, ma 1 solo gruppo su 2 con maggioranza non è sufficiente (richiesto >
        // 1).
        Set<Long> ackSetInvalid = new HashSet<>(Arrays.asList(2L, 3L, 4L, 5L));
        assertFalse(qhCustom.containsQuorum(ackSetInvalid),
                "Dovrebbe negare il quorum, poiché il gruppo 1 non ha raggiunto la maggioranza per via dei pesi asimmetrici");
    }
}