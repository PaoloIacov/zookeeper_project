package org.apache.zookeeper.server.quorum.flexible;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.SyncedLearnerTracker;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

/**
 * Test di integrazione per QuorumHierarchical.
 *
 * Verifichiamo che SyncedLearnerTracker (il chiamante) reagisca correttamente 
 * ai verdetti di quorum di QuorumHierarchical. Sfruttiamo il polimorfismo 
 * dell'interfaccia QuorumVerifier per iniettare l'implementazione concreta.
 */
public class QuorumHierarchicalIT {

    // ========================================================================
    // Helper: costruzione di un QuorumHierarchical reale da Properties
    // ========================================================================

    /**
     * Crea un cluster gerarchico a 3 nodi in 1 gruppo.
     * Quorum richiesto: maggioranza del gruppo (2 su 3).
     *
     *   group.1=1:2:3
     *   weight.1=1, weight.2=1, weight.3=1
     */
    private QuorumHierarchical buildThreeNodeCluster() throws Exception {
        Properties props = new Properties();
        props.setProperty("group.1", "1:2:3");
        props.setProperty("weight.1", "1");
        props.setProperty("weight.2", "1");
        props.setProperty("weight.3", "1");
        props.setProperty("server.1", "localhost:2881:3881");
        props.setProperty("server.2", "localhost:2882:3882");
        props.setProperty("server.3", "localhost:2883:3883");
        return new QuorumHierarchical(props);
    }

    /**
     * Crea un cluster gerarchico a 5 nodi in 2 gruppi.
     * Gruppo 1: server 1,2,3
     * Gruppo 2: server 4,5
     * Quorum richiesto: maggioranza dei gruppi (entrambi, essendo 2)
     */
    private QuorumHierarchical buildTwoGroupCluster() throws Exception {
        Properties props = new Properties();
        props.setProperty("group.1", "1:2:3");
        props.setProperty("group.2", "4:5");
        props.setProperty("weight.1", "1");
        props.setProperty("weight.2", "1");
        props.setProperty("weight.3", "1");
        props.setProperty("weight.4", "1");
        props.setProperty("weight.5", "1");
        props.setProperty("server.1", "localhost:2881:3881");
        props.setProperty("server.2", "localhost:2882:3882");
        props.setProperty("server.3", "localhost:2883:3883");
        props.setProperty("server.4", "localhost:2884:3884");
        props.setProperty("server.5", "localhost:2885:3885");
        return new QuorumHierarchical(props);
    }

    // ========================================================================
    // TEST SUITE IT — Top-Down: SyncedLearnerTracker ↔ QuorumHierarchical
    // ========================================================================

    /**
     * Il tracker riceve gli ACK dei server e delega a QuorumHierarchical.containsQuorum().
     * Con un cluster a 3 nodi, 2 ACK bastano per il quorum.
     */
    @Test
    @Timeout(5)
    public void MajorityAcks_HasAllQuorums_ReturnsTrue() throws Exception {
        QuorumHierarchical qh = buildThreeNodeCluster();

        // Top module: SyncedLearnerTracker (mai testato in unit)
        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        // Simula la ricezione degli ACK dal server 1 e 2 (2 su 3 = maggioranza)
        tracker.addAck(1L);
        tracker.addAck(2L);

        // Il Top delega al Down: qh.containsQuorum({1,2}) → true
        assertTrue(tracker.hasAllQuorums(),
                "Con 2 ACK su 3 nodi il quorum gerarchico deve essere raggiunto");
    }

    /**
     * Con un cluster a 3 nodi, 1 solo ACK non forma maggioranza nel gruppo, 
     * quindi il quorum non deve essere raggiunto.
     */
    @Test
    @Timeout(5)
    public void MinorityAcks_HasAllQuorums_ReturnsFalse() throws Exception {
        QuorumHierarchical qh = buildThreeNodeCluster();

        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        // Solo server 1 ha confermato
        tracker.addAck(1L);

        // Il Top delega al Down: qh.containsQuorum({1}) → false
        assertFalse(tracker.hasAllQuorums(),
                "Con 1 ACK su 3 nodi il quorum NON deve essere raggiunto");
    }

    /**
     * Con 2 gruppi (1,2,3) e (4,5), servono almeno 2 ACK nel gruppo 1
     * e almeno 1 ACK nel gruppo 2 per raggiungere il quorum gerarchico.
     * Testiamo che avere la maggioranza in un solo gruppo non basta.
     */
    @Test
    @Timeout(5)
    public void TwoGroups_PartialQuorum_ReturnsFalse() throws Exception {
        QuorumHierarchical qh = buildTwoGroupCluster();

        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        // Maggioranza nel gruppo 1 (2 su 3), ma nessun ACK nel gruppo 2
        tracker.addAck(1L);
        tracker.addAck(2L);

        // Il Top delega al Down: qh.containsQuorum({1,2}) → false
        // perché manca la maggioranza nel gruppo 2
        assertFalse(tracker.hasAllQuorums(),
                "Con ACK solo nel gruppo 1, il quorum gerarchico multi-gruppo NON è raggiunto");
    }

    /**
     * Il quorum viene raggiunto quando entrambi i gruppi hanno la maggioranza.
     */
    @Test
    @Timeout(5)
    public void TwoGroups_FullQuorum_ReturnsTrue() throws Exception {
        QuorumHierarchical qh = buildTwoGroupCluster();

        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        // Maggioranza in entrambi i gruppi: {1,2} nel gruppo 1, {4,5} nel gruppo 2
        tracker.addAck(1L);
        tracker.addAck(2L);
        tracker.addAck(4L);
        tracker.addAck(5L);

        // Il Top delega al Down: qh.containsQuorum({1,2,4,5}) → true
        assertTrue(tracker.hasAllQuorums(),
                "Con la maggioranza in entrambi i gruppi, il quorum deve essere raggiunto");
    }

    /**
     * Verifica l'interazione tra SyncedLearnerTracker.addAck() e QuorumHierarchical.getVotingMembers().
     * Un ACK di un server inesistente non deve alterare il verdetto.
     */
    @Test
    @Timeout(5)
    public void UnknownServerAck_IsIgnored() throws Exception {
        QuorumHierarchical qh = buildThreeNodeCluster();

        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        // Server 99 non esiste nella configurazione
        boolean changed = tracker.addAck(99L);

        assertFalse(changed,
                "Un ACK da un server sconosciuto non deve modificare lo stato del tracker");
        assertFalse(tracker.hasAllQuorums(),
                "Un ACK fantasma non deve contribuire al raggiungimento del quorum");
    }

}
