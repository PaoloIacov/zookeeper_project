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
 * Integration test per QuorumHierarchical.
 * Verifica l'interazione tra SyncedLearnerTracker e QuorumHierarchical.
 */
public class QuorumHierarchicalIT {

    // --- Helper ---

    // Cluster a 3 nodi in 1 gruppo. Quorum: 2 su 3.
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

    // Cluster a 5 nodi in 2 gruppi. Quorum: maggioranza in entrambi i gruppi.
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
    // Integration Test: SyncedLearnerTracker ↔ QuorumHierarchical
    // ========================================================================

    @Test
    @Timeout(5)
    public void MajorityAcks_HasAllQuorums_ReturnsTrue() throws Exception {
        QuorumHierarchical qh = buildThreeNodeCluster();
        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        tracker.addAck(1L);
        tracker.addAck(2L);

        assertTrue(tracker.hasAllQuorums(),
                "Con 2 ACK su 3 nodi il quorum gerarchico deve essere raggiunto");
    }

    @Test
    @Timeout(5)
    public void MinorityAcks_HasAllQuorums_ReturnsFalse() throws Exception {
        QuorumHierarchical qh = buildThreeNodeCluster();
        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        tracker.addAck(1L);

        assertFalse(tracker.hasAllQuorums(),
                "Con 1 ACK su 3 nodi il quorum NON deve essere raggiunto");
    }

    @Test
    @Timeout(5)
    public void TwoGroups_PartialQuorum_ReturnsFalse() throws Exception {
        QuorumHierarchical qh = buildTwoGroupCluster();
        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        // Maggioranza nel gruppo 1, ma nessun ACK nel gruppo 2
        tracker.addAck(1L);
        tracker.addAck(2L);

        assertFalse(tracker.hasAllQuorums(),
                "Con ACK solo nel gruppo 1, il quorum gerarchico multi-gruppo NON è raggiunto");
    }

    @Test
    @Timeout(5)
    public void TwoGroups_FullQuorum_ReturnsTrue() throws Exception {
        QuorumHierarchical qh = buildTwoGroupCluster();
        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        tracker.addAck(1L);
        tracker.addAck(2L);
        tracker.addAck(4L);
        tracker.addAck(5L);

        assertTrue(tracker.hasAllQuorums(),
                "Con la maggioranza in entrambi i gruppi, il quorum deve essere raggiunto");
    }

    @Test
    @Timeout(5)
    public void UnknownServerAck_IsIgnored() throws Exception {
        QuorumHierarchical qh = buildThreeNodeCluster();
        SyncedLearnerTracker tracker = new SyncedLearnerTracker();
        tracker.addQuorumVerifier(qh);

        boolean changed = tracker.addAck(99L);

        assertFalse(changed,
                "Un ACK da un server sconosciuto non deve modificare lo stato del tracker");
        assertFalse(tracker.hasAllQuorums(),
                "Un ACK fantasma non deve contribuire al raggiungimento del quorum");
    }

}
