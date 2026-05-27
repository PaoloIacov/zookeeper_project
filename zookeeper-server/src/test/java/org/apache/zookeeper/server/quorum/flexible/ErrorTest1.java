package org.apache.zookeeper.server.quorum.flexible;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ErrorTest1 {

    public static boolean debug = false;

    public void assertBooleanArrayEquals(boolean[] expectedArray, boolean[] actualArray) {
        if (expectedArray.length != actualArray.length) {
            throw new AssertionError("Array lengths differ: " + expectedArray.length + " != " + actualArray.length);
        }
        for (int i = 0; i < expectedArray.length; i++) {
            if (expectedArray[i] != actualArray[i]) {
                throw new AssertionError("Arrays differ at index " + i + ": " + expectedArray[i] + " != " + actualArray[i]);
            }
        }
    }

    @Test
    public void test501() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test501");
        java.util.Properties properties0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical1 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap2 = quorumHierarchical1.getVotingMembers();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker3 = null;
        boolean boolean5 = quorumHierarchical1.revalidateVoteset(syncedLearnerTracker3, true);
        org.apache.zookeeper.server.quorum.Leader leader6 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical7 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean8 = quorumHierarchical7.getNeedOracle();
        long long10 = quorumHierarchical7.getWeight(1L);
        boolean boolean11 = quorumHierarchical7.getNeedOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap12 = quorumHierarchical7.getObservingMembers();
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical14 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long15 = quorumHierarchical14.getVersion();
        boolean boolean16 = quorumHierarchical14.getNeedOracle();
        boolean boolean17 = quorumHierarchical14.askOracle();
        boolean boolean18 = quorumHierarchical14.getNeedOracle();
        boolean boolean19 = quorumHierarchical14.askOracle();
        java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList20 = null;
        boolean boolean21 = quorumHierarchical14.updateNeedOracle(learnerHandlerList20);
        org.apache.zookeeper.server.quorum.Leader leader22 = null;
        java.util.Properties properties23 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical24 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties23);
        org.apache.zookeeper.server.quorum.Leader leader25 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray26 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList27 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean28 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList27, proposalArray26);
        boolean boolean30 = quorumHierarchical24.revalidateOutstandingProp(leader25, proposalList27, (long) (-1));
        boolean boolean32 = quorumHierarchical14.revalidateOutstandingProp(leader22, proposalList27, (long) 'a');
        boolean boolean34 = quorumHierarchical7.revalidateOutstandingProp(leader13, proposalList27, 100L);
        boolean boolean36 = quorumHierarchical1.revalidateOutstandingProp(leader6, proposalList27, (long) 0);
        long long37 = quorumHierarchical1.getVersion();
        quorumHierarchical1.setVersion((long) (short) 1);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical7, quorumHierarchical24, and quorumHierarchical1.", !(quorumHierarchical7.equals(quorumHierarchical24) && quorumHierarchical24.equals(quorumHierarchical1)) || quorumHierarchical7.equals(quorumHierarchical1));
    }

    @Test
    public void test502() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test502");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        quorumHierarchical0.setVersion((long) (short) 1);
        java.lang.String str3 = quorumHierarchical0.toString();
        boolean boolean4 = quorumHierarchical0.getNeedOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long6 = quorumHierarchical5.getVersion();
        boolean boolean7 = quorumHierarchical5.getNeedOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap8 = quorumHierarchical5.getObservingMembers();
        quorumHierarchical5.setVersion(0L);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical11 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean12 = quorumHierarchical11.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray14 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList15 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList15, proposalArray14);
        boolean boolean18 = quorumHierarchical11.revalidateOutstandingProp(leader13, proposalList15, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray19 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList20 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean21 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20, learnerHandlerArray19);
        boolean boolean22 = quorumHierarchical11.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        boolean boolean23 = quorumHierarchical5.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        java.util.Properties properties24 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical25 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties24);
        org.apache.zookeeper.server.quorum.Leader leader26 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray27 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList28 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList28, proposalArray27);
        boolean boolean31 = quorumHierarchical25.revalidateOutstandingProp(leader26, proposalList28, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical32 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean33 = quorumHierarchical32.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader34 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray35 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList36 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean37 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList36, proposalArray35);
        boolean boolean39 = quorumHierarchical32.revalidateOutstandingProp(leader34, proposalList36, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray40 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList41 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean42 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList41, learnerHandlerArray40);
        boolean boolean43 = quorumHierarchical32.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList41);
        boolean boolean44 = quorumHierarchical25.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList41);
        boolean boolean45 = quorumHierarchical5.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList41);
        boolean boolean46 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList41);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical0, quorumHierarchical5, and quorumHierarchical11.", !(quorumHierarchical0.equals(quorumHierarchical5) && quorumHierarchical5.equals(quorumHierarchical11)) || quorumHierarchical0.equals(quorumHierarchical11));
    }

    @Test
    public void test503() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test503");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap1 = quorumHierarchical0.getObservingMembers();
        boolean boolean2 = quorumHierarchical0.getNeedOracle();
        java.util.Properties properties3 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical4 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties3);
        org.apache.zookeeper.server.quorum.Leader leader5 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray6 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList7 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList7, proposalArray6);
        boolean boolean10 = quorumHierarchical4.revalidateOutstandingProp(leader5, proposalList7, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical11 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean12 = quorumHierarchical11.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray14 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList15 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList15, proposalArray14);
        boolean boolean18 = quorumHierarchical11.revalidateOutstandingProp(leader13, proposalList15, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray19 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList20 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean21 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20, learnerHandlerArray19);
        boolean boolean22 = quorumHierarchical11.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        boolean boolean23 = quorumHierarchical4.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        boolean boolean24 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical25 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean26 = quorumHierarchical25.getNeedOracle();
        long long28 = quorumHierarchical25.getWeight(1L);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical29 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long30 = quorumHierarchical29.getVersion();
        boolean boolean31 = quorumHierarchical29.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray32 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList33 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean34 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList33, learnerHandlerArray32);
        boolean boolean35 = quorumHierarchical29.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList33);
        boolean boolean36 = quorumHierarchical25.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList33);
        boolean boolean37 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList33);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap38 = quorumHierarchical0.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical39 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str40 = quorumHierarchical39.toString();
        boolean boolean42 = quorumHierarchical39.equals((java.lang.Object) 100.0f);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical43 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean44 = quorumHierarchical43.getNeedOracle();
        long long46 = quorumHierarchical43.getWeight(1L);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical47 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long48 = quorumHierarchical47.getVersion();
        boolean boolean49 = quorumHierarchical47.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray50 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList51 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean52 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList51, learnerHandlerArray50);
        boolean boolean53 = quorumHierarchical47.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList51);
        boolean boolean54 = quorumHierarchical43.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList51);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap55 = quorumHierarchical43.getAllMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap56 = quorumHierarchical43.getObservingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap57 = quorumHierarchical43.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical58 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean59 = quorumHierarchical58.getNeedOracle();
        long long61 = quorumHierarchical58.getWeight(1L);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical62 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long63 = quorumHierarchical62.getVersion();
        boolean boolean64 = quorumHierarchical62.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray65 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList66 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean67 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66, learnerHandlerArray65);
        boolean boolean68 = quorumHierarchical62.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66);
        boolean boolean69 = quorumHierarchical58.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66);
        boolean boolean70 = quorumHierarchical43.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66);
        boolean boolean71 = quorumHierarchical39.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66);
        java.lang.String str72 = quorumHierarchical39.getOraclePath();
        java.lang.String str73 = quorumHierarchical39.toString();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap74 = quorumHierarchical39.getObservingMembers();
        boolean boolean75 = quorumHierarchical0.equals((java.lang.Object) longMap74);
        quorumHierarchical0.setVersion((long) (byte) 10);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical0, quorumHierarchical4, and quorumHierarchical11.", !(quorumHierarchical0.equals(quorumHierarchical4) && quorumHierarchical4.equals(quorumHierarchical11)) || quorumHierarchical0.equals(quorumHierarchical11));
    }

    @Test
    public void test504() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test504");
        java.util.Properties properties0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical1 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap2 = quorumHierarchical1.getVotingMembers();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker3 = null;
        boolean boolean5 = quorumHierarchical1.revalidateVoteset(syncedLearnerTracker3, true);
        long long6 = quorumHierarchical1.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap7 = quorumHierarchical1.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical8 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str9 = quorumHierarchical8.toString();
        boolean boolean11 = quorumHierarchical8.equals((java.lang.Object) 100.0f);
        boolean boolean12 = quorumHierarchical8.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray14 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList15 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList15, proposalArray14);
        boolean boolean18 = quorumHierarchical8.revalidateOutstandingProp(leader13, proposalList15, (long) (short) -1);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap19 = quorumHierarchical8.getObservingMembers();
        boolean boolean20 = quorumHierarchical1.equals((java.lang.Object) quorumHierarchical8);
        java.lang.String str21 = quorumHierarchical1.toString();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical22 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean23 = quorumHierarchical22.getNeedOracle();
        long long25 = quorumHierarchical22.getWeight(1L);
        boolean boolean26 = quorumHierarchical22.getNeedOracle();
        java.lang.String str27 = quorumHierarchical22.toString();
        java.lang.String str28 = quorumHierarchical22.getOraclePath();
        boolean boolean29 = quorumHierarchical22.askOracle();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker30 = null;
        boolean boolean32 = quorumHierarchical22.revalidateVoteset(syncedLearnerTracker30, true);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap33 = quorumHierarchical22.getVotingMembers();
        boolean boolean34 = quorumHierarchical22.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical35 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str36 = quorumHierarchical35.toString();
        quorumHierarchical35.setVersion((long) 100);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap39 = quorumHierarchical35.getVotingMembers();
        boolean boolean40 = quorumHierarchical35.askOracle();
        java.lang.String str41 = quorumHierarchical35.getOraclePath();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical42 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long43 = quorumHierarchical42.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap44 = quorumHierarchical42.getObservingMembers();
        java.util.Properties properties45 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical46 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties45);
        org.apache.zookeeper.server.quorum.Leader leader47 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray48 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList49 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean50 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList49, proposalArray48);
        boolean boolean52 = quorumHierarchical46.revalidateOutstandingProp(leader47, proposalList49, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical53 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean54 = quorumHierarchical53.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader55 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray56 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList57 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean58 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList57, proposalArray56);
        boolean boolean60 = quorumHierarchical53.revalidateOutstandingProp(leader55, proposalList57, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray61 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList62 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean63 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList62, learnerHandlerArray61);
        boolean boolean64 = quorumHierarchical53.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList62);
        boolean boolean65 = quorumHierarchical46.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList62);
        java.lang.Long[] longArray70 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet71 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean72 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet71, longArray70);
        boolean boolean73 = quorumHierarchical46.containsQuorum((java.util.Set<java.lang.Long>) longSet71);
        boolean boolean74 = quorumHierarchical42.containsQuorum((java.util.Set<java.lang.Long>) longSet71);
        boolean boolean75 = quorumHierarchical35.containsQuorum((java.util.Set<java.lang.Long>) longSet71);
        boolean boolean76 = quorumHierarchical22.containsQuorum((java.util.Set<java.lang.Long>) longSet71);
        boolean boolean77 = quorumHierarchical1.containsQuorum((java.util.Set<java.lang.Long>) longSet71);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical1, quorumHierarchical8, and quorumHierarchical35.", !(quorumHierarchical1.equals(quorumHierarchical8) && quorumHierarchical8.equals(quorumHierarchical35)) || quorumHierarchical1.equals(quorumHierarchical35));
    }

    @Test
    public void test505() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test505");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        java.lang.Object obj1 = null;
        boolean boolean2 = quorumHierarchical0.equals(obj1);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical3 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str4 = quorumHierarchical3.toString();
        quorumHierarchical3.setVersion((long) 100);
        java.util.Properties properties7 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical8 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties7);
        org.apache.zookeeper.server.quorum.Leader leader9 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray10 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList11 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList11, proposalArray10);
        boolean boolean14 = quorumHierarchical8.revalidateOutstandingProp(leader9, proposalList11, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical15 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean16 = quorumHierarchical15.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader17 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray18 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList19 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean20 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList19, proposalArray18);
        boolean boolean22 = quorumHierarchical15.revalidateOutstandingProp(leader17, proposalList19, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray23 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList24 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean25 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24, learnerHandlerArray23);
        boolean boolean26 = quorumHierarchical15.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24);
        boolean boolean27 = quorumHierarchical8.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24);
        java.lang.Long[] longArray32 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet33 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean34 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet33, longArray32);
        boolean boolean35 = quorumHierarchical8.containsQuorum((java.util.Set<java.lang.Long>) longSet33);
        boolean boolean36 = quorumHierarchical3.containsQuorum((java.util.Set<java.lang.Long>) longSet33);
        boolean boolean37 = quorumHierarchical3.askOracle();
        java.lang.String str38 = quorumHierarchical3.toString();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical39 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long40 = quorumHierarchical39.getVersion();
        quorumHierarchical39.setVersion((-1L));
        java.lang.String str43 = quorumHierarchical39.getOraclePath();
        java.util.Properties properties44 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical45 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties44);
        org.apache.zookeeper.server.quorum.Leader leader46 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray47 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList48 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean49 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList48, proposalArray47);
        boolean boolean51 = quorumHierarchical45.revalidateOutstandingProp(leader46, proposalList48, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical52 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean53 = quorumHierarchical52.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader54 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray55 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList56 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean57 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList56, proposalArray55);
        boolean boolean59 = quorumHierarchical52.revalidateOutstandingProp(leader54, proposalList56, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray60 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList61 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean62 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList61, learnerHandlerArray60);
        boolean boolean63 = quorumHierarchical52.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList61);
        boolean boolean64 = quorumHierarchical45.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList61);
        java.lang.Long[] longArray69 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet70 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean71 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet70, longArray69);
        boolean boolean72 = quorumHierarchical45.containsQuorum((java.util.Set<java.lang.Long>) longSet70);
        boolean boolean73 = quorumHierarchical39.containsQuorum((java.util.Set<java.lang.Long>) longSet70);
        boolean boolean74 = quorumHierarchical3.containsQuorum((java.util.Set<java.lang.Long>) longSet70);
        boolean boolean75 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet70);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical8, quorumHierarchical0, and quorumHierarchical39.", !(quorumHierarchical8.equals(quorumHierarchical0) && quorumHierarchical0.equals(quorumHierarchical39)) || quorumHierarchical8.equals(quorumHierarchical39));
    }

    @Test
    public void test506() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test506");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker1 = null;
        boolean boolean3 = quorumHierarchical0.revalidateVoteset(syncedLearnerTracker1, false);
        java.lang.String str4 = quorumHierarchical0.toString();
        boolean boolean5 = quorumHierarchical0.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap6 = quorumHierarchical0.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical7 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str8 = quorumHierarchical7.toString();
        boolean boolean9 = quorumHierarchical7.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap10 = quorumHierarchical7.getAllMembers();
        java.util.Properties properties11 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical12 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties11);
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray14 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList15 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList15, proposalArray14);
        boolean boolean18 = quorumHierarchical12.revalidateOutstandingProp(leader13, proposalList15, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical19 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean20 = quorumHierarchical19.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader21 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray22 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList23 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList23, proposalArray22);
        boolean boolean26 = quorumHierarchical19.revalidateOutstandingProp(leader21, proposalList23, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray27 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList28 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList28, learnerHandlerArray27);
        boolean boolean30 = quorumHierarchical19.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList28);
        boolean boolean31 = quorumHierarchical12.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList28);
        boolean boolean32 = quorumHierarchical7.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList28);
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker33 = null;
        boolean boolean35 = quorumHierarchical7.revalidateVoteset(syncedLearnerTracker33, true);
        quorumHierarchical7.setVersion((long) (byte) 100);
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker38 = null;
        boolean boolean40 = quorumHierarchical7.revalidateVoteset(syncedLearnerTracker38, true);
        boolean boolean41 = quorumHierarchical7.getNeedOracle();
        quorumHierarchical7.setVersion((long) (byte) 1);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical44 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long45 = quorumHierarchical44.getVersion();
        quorumHierarchical44.setVersion((-1L));
        java.lang.String str48 = quorumHierarchical44.getOraclePath();
        java.util.Properties properties49 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical50 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties49);
        org.apache.zookeeper.server.quorum.Leader leader51 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray52 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList53 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean54 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList53, proposalArray52);
        boolean boolean56 = quorumHierarchical50.revalidateOutstandingProp(leader51, proposalList53, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical57 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean58 = quorumHierarchical57.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader59 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray60 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList61 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean62 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList61, proposalArray60);
        boolean boolean64 = quorumHierarchical57.revalidateOutstandingProp(leader59, proposalList61, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray65 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList66 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean67 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66, learnerHandlerArray65);
        boolean boolean68 = quorumHierarchical57.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66);
        boolean boolean69 = quorumHierarchical50.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList66);
        java.lang.Long[] longArray74 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet75 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean76 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet75, longArray74);
        boolean boolean77 = quorumHierarchical50.containsQuorum((java.util.Set<java.lang.Long>) longSet75);
        boolean boolean78 = quorumHierarchical44.containsQuorum((java.util.Set<java.lang.Long>) longSet75);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical79 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long80 = quorumHierarchical79.getVersion();
        quorumHierarchical79.setVersion((-1L));
        java.lang.Long[] longArray87 = new java.lang.Long[] { 10L, 10L, (-1L), 10L };
        java.util.LinkedHashSet<java.lang.Long> longSet88 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean89 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet88, longArray87);
        boolean boolean90 = quorumHierarchical79.containsQuorum((java.util.Set<java.lang.Long>) longSet88);
        boolean boolean91 = quorumHierarchical44.containsQuorum((java.util.Set<java.lang.Long>) longSet88);
        boolean boolean92 = quorumHierarchical7.containsQuorum((java.util.Set<java.lang.Long>) longSet88);
        boolean boolean93 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet88);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical7, quorumHierarchical0, and quorumHierarchical12.", !(quorumHierarchical7.equals(quorumHierarchical0) && quorumHierarchical0.equals(quorumHierarchical12)) || quorumHierarchical7.equals(quorumHierarchical12));
    }

    @Test
    public void test507() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test507");
        java.util.Properties properties0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical1 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.Leader leader2 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray3 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList4 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList4, proposalArray3);
        boolean boolean7 = quorumHierarchical1.revalidateOutstandingProp(leader2, proposalList4, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical8 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean9 = quorumHierarchical8.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader10 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray11 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList12 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList12, proposalArray11);
        boolean boolean15 = quorumHierarchical8.revalidateOutstandingProp(leader10, proposalList12, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray16 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList17 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean18 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList17, learnerHandlerArray16);
        boolean boolean19 = quorumHierarchical8.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList17);
        boolean boolean20 = quorumHierarchical1.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList17);
        quorumHierarchical1.setVersion((long) (short) 0);
        boolean boolean23 = quorumHierarchical1.askOracle();
        boolean boolean24 = quorumHierarchical1.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader25 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical26 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long27 = quorumHierarchical26.getVersion();
        boolean boolean28 = quorumHierarchical26.getNeedOracle();
        boolean boolean29 = quorumHierarchical26.askOracle();
        boolean boolean30 = quorumHierarchical26.getNeedOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap31 = quorumHierarchical26.getVotingMembers();
        org.apache.zookeeper.server.quorum.Leader leader32 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical33 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean34 = quorumHierarchical33.getNeedOracle();
        long long36 = quorumHierarchical33.getWeight(1L);
        boolean boolean37 = quorumHierarchical33.getNeedOracle();
        java.lang.String str38 = quorumHierarchical33.toString();
        java.lang.String str39 = quorumHierarchical33.toString();
        java.lang.String str40 = quorumHierarchical33.getOraclePath();
        boolean boolean41 = quorumHierarchical33.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader42 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical43 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str44 = quorumHierarchical43.toString();
        boolean boolean45 = quorumHierarchical43.askOracle();
        org.apache.zookeeper.server.quorum.Leader leader46 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical47 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long48 = quorumHierarchical47.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap49 = quorumHierarchical47.getObservingMembers();
        java.lang.String str50 = quorumHierarchical47.getOraclePath();
        java.util.Properties properties51 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical52 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties51);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap53 = quorumHierarchical52.getVotingMembers();
        java.lang.String str54 = quorumHierarchical52.toString();
        boolean boolean55 = quorumHierarchical52.askOracle();
        boolean boolean56 = quorumHierarchical47.equals((java.lang.Object) boolean55);
        org.apache.zookeeper.server.quorum.Leader leader57 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical58 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean59 = quorumHierarchical58.getNeedOracle();
        long long61 = quorumHierarchical58.getWeight(1L);
        org.apache.zookeeper.server.quorum.Leader leader62 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical63 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long64 = quorumHierarchical63.getVersion();
        boolean boolean65 = quorumHierarchical63.getNeedOracle();
        boolean boolean66 = quorumHierarchical63.askOracle();
        boolean boolean67 = quorumHierarchical63.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader68 = null;
        java.util.Properties properties69 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical70 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties69);
        org.apache.zookeeper.server.quorum.Leader leader71 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray72 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList73 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean74 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList73, proposalArray72);
        boolean boolean76 = quorumHierarchical70.revalidateOutstandingProp(leader71, proposalList73, (long) (-1));
        boolean boolean78 = quorumHierarchical63.revalidateOutstandingProp(leader68, proposalList73, 10L);
        boolean boolean80 = quorumHierarchical58.revalidateOutstandingProp(leader62, proposalList73, (long) (short) 10);
        boolean boolean82 = quorumHierarchical47.revalidateOutstandingProp(leader57, proposalList73, (long) (-1));
        boolean boolean84 = quorumHierarchical43.revalidateOutstandingProp(leader46, proposalList73, 0L);
        boolean boolean86 = quorumHierarchical33.revalidateOutstandingProp(leader42, proposalList73, 10L);
        boolean boolean88 = quorumHierarchical26.revalidateOutstandingProp(leader32, proposalList73, 52L);
        boolean boolean90 = quorumHierarchical1.revalidateOutstandingProp(leader25, proposalList73, (long) (short) 1);
        quorumHierarchical1.setVersion((long) (byte) -1);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical8, quorumHierarchical52, and quorumHierarchical1.", !(quorumHierarchical8.equals(quorumHierarchical52) && quorumHierarchical52.equals(quorumHierarchical1)) || quorumHierarchical8.equals(quorumHierarchical1));
    }

    @Test
    public void test508() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test508");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long1 = quorumHierarchical0.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap2 = quorumHierarchical0.getObservingMembers();
        java.lang.String str3 = quorumHierarchical0.getOraclePath();
        java.util.Properties properties4 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties4);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap6 = quorumHierarchical5.getVotingMembers();
        java.lang.String str7 = quorumHierarchical5.toString();
        boolean boolean8 = quorumHierarchical5.askOracle();
        boolean boolean9 = quorumHierarchical0.equals((java.lang.Object) boolean8);
        long long10 = quorumHierarchical0.getVersion();
        // during test generation this statement threw an exception of type java.lang.NullPointerException in error
        long long12 = quorumHierarchical0.getWeight((long) 'a');
    }

    @Test
    public void test509() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test509");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long1 = quorumHierarchical0.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap2 = quorumHierarchical0.getObservingMembers();
        java.util.Properties properties3 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical4 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties3);
        org.apache.zookeeper.server.quorum.Leader leader5 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray6 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList7 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList7, proposalArray6);
        boolean boolean10 = quorumHierarchical4.revalidateOutstandingProp(leader5, proposalList7, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical11 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean12 = quorumHierarchical11.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray14 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList15 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList15, proposalArray14);
        boolean boolean18 = quorumHierarchical11.revalidateOutstandingProp(leader13, proposalList15, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray19 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList20 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean21 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20, learnerHandlerArray19);
        boolean boolean22 = quorumHierarchical11.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        boolean boolean23 = quorumHierarchical4.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        java.lang.Long[] longArray28 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet29 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean30 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet29, longArray28);
        boolean boolean31 = quorumHierarchical4.containsQuorum((java.util.Set<java.lang.Long>) longSet29);
        boolean boolean32 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet29);
        boolean boolean34 = quorumHierarchical0.equals((java.lang.Object) "server.1=localhost:2888:3888:participant\nserver.2=localhost:2889:3889:observer\ngroup.1=1\nweight.1=1\nweight.2=0\nversion=0");
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap35 = quorumHierarchical0.getVotingMembers();
        long long36 = quorumHierarchical0.getVersion();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical37 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean38 = quorumHierarchical37.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader39 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray40 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList41 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean42 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList41, proposalArray40);
        boolean boolean44 = quorumHierarchical37.revalidateOutstandingProp(leader39, proposalList41, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray45 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList46 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean47 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList46, learnerHandlerArray45);
        boolean boolean48 = quorumHierarchical37.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList46);
        java.lang.String str49 = quorumHierarchical37.getOraclePath();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap50 = quorumHierarchical37.getAllMembers();
        java.lang.String str51 = quorumHierarchical37.getOraclePath();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap52 = quorumHierarchical37.getObservingMembers();
        java.util.Properties properties53 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical54 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties53);
        boolean boolean55 = quorumHierarchical54.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical56 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical57 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long58 = quorumHierarchical57.getVersion();
        quorumHierarchical57.setVersion((-1L));
        java.lang.Long[] longArray65 = new java.lang.Long[] { 10L, 10L, (-1L), 10L };
        java.util.LinkedHashSet<java.lang.Long> longSet66 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean67 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet66, longArray65);
        boolean boolean68 = quorumHierarchical57.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        boolean boolean69 = quorumHierarchical56.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        boolean boolean70 = quorumHierarchical54.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        boolean boolean71 = quorumHierarchical37.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        boolean boolean72 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical4, quorumHierarchical0, and quorumHierarchical57.", !(quorumHierarchical4.equals(quorumHierarchical0) && quorumHierarchical0.equals(quorumHierarchical57)) || quorumHierarchical4.equals(quorumHierarchical57));
    }

    @Test
    public void test510() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test510");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str1 = quorumHierarchical0.toString();
        boolean boolean2 = quorumHierarchical0.askOracle();
        java.lang.String str3 = quorumHierarchical0.toString();
        org.apache.zookeeper.server.quorum.Leader leader4 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long6 = quorumHierarchical5.getVersion();
        boolean boolean7 = quorumHierarchical5.getNeedOracle();
        boolean boolean8 = quorumHierarchical5.askOracle();
        boolean boolean9 = quorumHierarchical5.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader10 = null;
        java.util.Properties properties11 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical12 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties11);
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray14 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList15 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList15, proposalArray14);
        boolean boolean18 = quorumHierarchical12.revalidateOutstandingProp(leader13, proposalList15, (long) (-1));
        boolean boolean20 = quorumHierarchical5.revalidateOutstandingProp(leader10, proposalList15, 10L);
        boolean boolean22 = quorumHierarchical0.revalidateOutstandingProp(leader4, proposalList15, (long) (short) -1);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical23 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap24 = quorumHierarchical23.getAllMembers();
        java.util.Properties properties25 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical26 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties25);
        boolean boolean27 = quorumHierarchical23.equals((java.lang.Object) properties25);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical28 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties25);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical29 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties25);
        boolean boolean30 = quorumHierarchical0.equals((java.lang.Object) quorumHierarchical29);
        org.apache.zookeeper.server.quorum.Leader leader31 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical32 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long33 = quorumHierarchical32.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap34 = quorumHierarchical32.getObservingMembers();
        java.lang.String str35 = quorumHierarchical32.getOraclePath();
        java.util.Properties properties36 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical37 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties36);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap38 = quorumHierarchical37.getVotingMembers();
        java.lang.String str39 = quorumHierarchical37.toString();
        boolean boolean40 = quorumHierarchical37.askOracle();
        boolean boolean41 = quorumHierarchical32.equals((java.lang.Object) boolean40);
        quorumHierarchical32.setVersion((long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical44 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap45 = quorumHierarchical44.getAllMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical46 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        boolean boolean47 = quorumHierarchical46.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical48 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str49 = quorumHierarchical48.toString();
        boolean boolean50 = quorumHierarchical48.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap51 = quorumHierarchical48.getAllMembers();
        java.util.Properties properties52 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical53 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties52);
        org.apache.zookeeper.server.quorum.Leader leader54 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray55 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList56 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean57 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList56, proposalArray55);
        boolean boolean59 = quorumHierarchical53.revalidateOutstandingProp(leader54, proposalList56, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical60 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean61 = quorumHierarchical60.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader62 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray63 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList64 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean65 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList64, proposalArray63);
        boolean boolean67 = quorumHierarchical60.revalidateOutstandingProp(leader62, proposalList64, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray68 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList69 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean70 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69, learnerHandlerArray68);
        boolean boolean71 = quorumHierarchical60.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        boolean boolean72 = quorumHierarchical53.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        boolean boolean73 = quorumHierarchical48.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        boolean boolean74 = quorumHierarchical46.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        boolean boolean75 = quorumHierarchical44.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        boolean boolean76 = quorumHierarchical32.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        boolean boolean77 = quorumHierarchical32.getNeedOracle();
        long long79 = quorumHierarchical32.getWeight(1L);
        org.apache.zookeeper.server.quorum.Leader leader80 = null;
        java.util.Properties properties81 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical82 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties81);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap83 = quorumHierarchical82.getVotingMembers();
        java.lang.String str84 = quorumHierarchical82.toString();
        org.apache.zookeeper.server.quorum.Leader leader85 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray86 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList87 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean88 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList87, proposalArray86);
        boolean boolean90 = quorumHierarchical82.revalidateOutstandingProp(leader85, proposalList87, 0L);
        boolean boolean92 = quorumHierarchical32.revalidateOutstandingProp(leader80, proposalList87, 0L);
        boolean boolean94 = quorumHierarchical0.revalidateOutstandingProp(leader31, proposalList87, (long) (byte) 100);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical0, quorumHierarchical5, and quorumHierarchical32.", !(quorumHierarchical0.equals(quorumHierarchical5) && quorumHierarchical5.equals(quorumHierarchical32)) || quorumHierarchical0.equals(quorumHierarchical32));
    }

    @Test
    public void test511() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test511");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str1 = quorumHierarchical0.toString();
        boolean boolean3 = quorumHierarchical0.equals((java.lang.Object) 100.0f);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap4 = quorumHierarchical0.getVotingMembers();
        // during test generation this statement threw an exception of type java.lang.NullPointerException in error
        long long6 = quorumHierarchical0.getWeight((long) '#');
    }

    @Test
    public void test512() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test512");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker1 = null;
        boolean boolean3 = quorumHierarchical0.revalidateVoteset(syncedLearnerTracker1, false);
        java.lang.String str4 = quorumHierarchical0.getOraclePath();
        long long5 = quorumHierarchical0.getVersion();
        boolean boolean6 = quorumHierarchical0.askOracle();
        java.lang.String str7 = quorumHierarchical0.toString();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap8 = quorumHierarchical0.getVotingMembers();
        java.lang.String str9 = quorumHierarchical0.getOraclePath();
        boolean boolean10 = quorumHierarchical0.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap11 = quorumHierarchical0.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical12 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean13 = quorumHierarchical12.getNeedOracle();
        long long15 = quorumHierarchical12.getWeight(1L);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical16 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long17 = quorumHierarchical16.getVersion();
        boolean boolean18 = quorumHierarchical16.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray19 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList20 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean21 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20, learnerHandlerArray19);
        boolean boolean22 = quorumHierarchical16.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        boolean boolean23 = quorumHierarchical12.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList20);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap24 = quorumHierarchical12.getAllMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap25 = quorumHierarchical12.getObservingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap26 = quorumHierarchical12.getObservingMembers();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker27 = null;
        boolean boolean29 = quorumHierarchical12.revalidateVoteset(syncedLearnerTracker27, false);
        org.apache.zookeeper.server.quorum.Leader leader30 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical31 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str32 = quorumHierarchical31.toString();
        quorumHierarchical31.setVersion((long) 100);
        org.apache.zookeeper.server.quorum.Leader leader35 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical36 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean37 = quorumHierarchical36.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader38 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray39 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList40 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean41 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList40, proposalArray39);
        boolean boolean43 = quorumHierarchical36.revalidateOutstandingProp(leader38, proposalList40, 0L);
        boolean boolean45 = quorumHierarchical31.revalidateOutstandingProp(leader35, proposalList40, (long) '#');
        boolean boolean47 = quorumHierarchical12.revalidateOutstandingProp(leader30, proposalList40, (long) (-1));
        org.apache.zookeeper.server.quorum.Leader leader48 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical49 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str50 = quorumHierarchical49.toString();
        quorumHierarchical49.setVersion((long) 100);
        org.apache.zookeeper.server.quorum.Leader leader53 = null;
        java.util.Properties properties54 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical55 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties54);
        org.apache.zookeeper.server.quorum.Leader leader56 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray57 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList58 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean59 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList58, proposalArray57);
        boolean boolean61 = quorumHierarchical55.revalidateOutstandingProp(leader56, proposalList58, (long) (-1));
        boolean boolean63 = quorumHierarchical49.revalidateOutstandingProp(leader53, proposalList58, 100L);
        boolean boolean65 = quorumHierarchical12.revalidateOutstandingProp(leader48, proposalList58, (long) (short) 100);
        boolean boolean66 = quorumHierarchical12.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap67 = quorumHierarchical12.getAllMembers();
        boolean boolean68 = quorumHierarchical0.equals((java.lang.Object) quorumHierarchical12);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical12, quorumHierarchical0, and quorumHierarchical31.", !(quorumHierarchical12.equals(quorumHierarchical0) && quorumHierarchical0.equals(quorumHierarchical31)) || quorumHierarchical12.equals(quorumHierarchical31));
    }

    @Test
    public void test513() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test513");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        quorumHierarchical0.setVersion((long) (short) 1);
        java.lang.String str3 = quorumHierarchical0.toString();
        java.util.Properties properties4 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties4);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap6 = quorumHierarchical5.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap7 = quorumHierarchical5.getObservingMembers();
        java.util.Properties properties8 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical9 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties8);
        org.apache.zookeeper.server.quorum.Leader leader10 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray11 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList12 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList12, proposalArray11);
        boolean boolean15 = quorumHierarchical9.revalidateOutstandingProp(leader10, proposalList12, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical16 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean17 = quorumHierarchical16.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader18 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray19 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList20 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean21 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList20, proposalArray19);
        boolean boolean23 = quorumHierarchical16.revalidateOutstandingProp(leader18, proposalList20, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray24 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList25 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean26 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25, learnerHandlerArray24);
        boolean boolean27 = quorumHierarchical16.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25);
        boolean boolean28 = quorumHierarchical9.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25);
        java.lang.Long[] longArray33 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet34 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet34, longArray33);
        boolean boolean36 = quorumHierarchical9.containsQuorum((java.util.Set<java.lang.Long>) longSet34);
        boolean boolean37 = quorumHierarchical5.containsQuorum((java.util.Set<java.lang.Long>) longSet34);
        java.util.Properties properties38 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical39 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties38);
        org.apache.zookeeper.server.quorum.Leader leader40 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray41 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList42 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList42, proposalArray41);
        boolean boolean45 = quorumHierarchical39.revalidateOutstandingProp(leader40, proposalList42, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical46 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean47 = quorumHierarchical46.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader48 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray49 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList50 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean51 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList50, proposalArray49);
        boolean boolean53 = quorumHierarchical46.revalidateOutstandingProp(leader48, proposalList50, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray54 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList55 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean56 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList55, learnerHandlerArray54);
        boolean boolean57 = quorumHierarchical46.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList55);
        boolean boolean58 = quorumHierarchical39.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList55);
        boolean boolean59 = quorumHierarchical5.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList55);
        long long60 = quorumHierarchical5.getVersion();
        boolean boolean61 = quorumHierarchical0.equals((java.lang.Object) long60);
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker62 = null;
        boolean boolean64 = quorumHierarchical0.revalidateVoteset(syncedLearnerTracker62, false);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap65 = quorumHierarchical0.getObservingMembers();
        java.lang.String str66 = quorumHierarchical0.getOraclePath();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical67 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str68 = quorumHierarchical67.toString();
        quorumHierarchical67.setVersion((long) 100);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap71 = quorumHierarchical67.getVotingMembers();
        boolean boolean72 = quorumHierarchical67.getNeedOracle();
        quorumHierarchical67.setVersion((long) (byte) 1);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical75 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long76 = quorumHierarchical75.getVersion();
        boolean boolean77 = quorumHierarchical75.getNeedOracle();
        boolean boolean78 = quorumHierarchical75.askOracle();
        boolean boolean79 = quorumHierarchical75.getNeedOracle();
        boolean boolean80 = quorumHierarchical75.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical81 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long82 = quorumHierarchical81.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap83 = quorumHierarchical81.getVotingMembers();
        boolean boolean84 = quorumHierarchical75.equals((java.lang.Object) longMap83);
        boolean boolean85 = quorumHierarchical67.equals((java.lang.Object) quorumHierarchical75);
        boolean boolean86 = quorumHierarchical0.equals((java.lang.Object) quorumHierarchical75);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical0, quorumHierarchical75, and quorumHierarchical5.", !(quorumHierarchical0.equals(quorumHierarchical75) && quorumHierarchical75.equals(quorumHierarchical5)) || quorumHierarchical0.equals(quorumHierarchical5));
    }

    @Test
    public void test514() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test514");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long1 = quorumHierarchical0.getVersion();
        quorumHierarchical0.setVersion((-1L));
        quorumHierarchical0.setVersion((long) (-1));
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap6 = quorumHierarchical0.getVotingMembers();
        boolean boolean7 = quorumHierarchical0.askOracle();
        boolean boolean8 = quorumHierarchical0.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap9 = quorumHierarchical0.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical10 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str11 = quorumHierarchical10.toString();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical12 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean13 = quorumHierarchical12.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader14 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray15 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList16 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList16, proposalArray15);
        boolean boolean19 = quorumHierarchical12.revalidateOutstandingProp(leader14, proposalList16, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray20 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList21 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList21, learnerHandlerArray20);
        boolean boolean23 = quorumHierarchical12.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList21);
        java.lang.String str24 = quorumHierarchical12.getOraclePath();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap25 = quorumHierarchical12.getAllMembers();
        java.lang.String str26 = quorumHierarchical12.getOraclePath();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap27 = quorumHierarchical12.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical28 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        boolean boolean29 = quorumHierarchical28.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical30 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long31 = quorumHierarchical30.getVersion();
        boolean boolean32 = quorumHierarchical30.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray33 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList34 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList34, learnerHandlerArray33);
        boolean boolean36 = quorumHierarchical30.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList34);
        boolean boolean37 = quorumHierarchical28.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList34);
        boolean boolean38 = quorumHierarchical12.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList34);
        boolean boolean39 = quorumHierarchical10.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList34);
        boolean boolean40 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList34);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical10, quorumHierarchical28, and quorumHierarchical0.", !(quorumHierarchical10.equals(quorumHierarchical28) && quorumHierarchical28.equals(quorumHierarchical0)) || quorumHierarchical10.equals(quorumHierarchical0));
    }

    @Test
    public void test515() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test515");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long1 = quorumHierarchical0.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap2 = quorumHierarchical0.getObservingMembers();
        java.lang.String str3 = quorumHierarchical0.getOraclePath();
        java.util.Properties properties4 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties4);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap6 = quorumHierarchical5.getVotingMembers();
        java.lang.String str7 = quorumHierarchical5.toString();
        boolean boolean8 = quorumHierarchical5.askOracle();
        boolean boolean9 = quorumHierarchical0.equals((java.lang.Object) boolean8);
        org.apache.zookeeper.server.quorum.Leader leader10 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical11 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean12 = quorumHierarchical11.getNeedOracle();
        long long14 = quorumHierarchical11.getWeight(1L);
        org.apache.zookeeper.server.quorum.Leader leader15 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical16 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long17 = quorumHierarchical16.getVersion();
        boolean boolean18 = quorumHierarchical16.getNeedOracle();
        boolean boolean19 = quorumHierarchical16.askOracle();
        boolean boolean20 = quorumHierarchical16.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader21 = null;
        java.util.Properties properties22 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical23 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties22);
        org.apache.zookeeper.server.quorum.Leader leader24 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray25 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList26 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList26, proposalArray25);
        boolean boolean29 = quorumHierarchical23.revalidateOutstandingProp(leader24, proposalList26, (long) (-1));
        boolean boolean31 = quorumHierarchical16.revalidateOutstandingProp(leader21, proposalList26, 10L);
        boolean boolean33 = quorumHierarchical11.revalidateOutstandingProp(leader15, proposalList26, (long) (short) 10);
        boolean boolean35 = quorumHierarchical0.revalidateOutstandingProp(leader10, proposalList26, (long) (-1));
        java.lang.String str36 = quorumHierarchical0.getOraclePath();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical37 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long38 = quorumHierarchical37.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap39 = quorumHierarchical37.getObservingMembers();
        java.util.Properties properties40 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical41 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties40);
        org.apache.zookeeper.server.quorum.Leader leader42 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray43 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList44 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean45 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList44, proposalArray43);
        boolean boolean47 = quorumHierarchical41.revalidateOutstandingProp(leader42, proposalList44, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical48 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean49 = quorumHierarchical48.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader50 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray51 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList52 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean53 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList52, proposalArray51);
        boolean boolean55 = quorumHierarchical48.revalidateOutstandingProp(leader50, proposalList52, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray56 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList57 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean58 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList57, learnerHandlerArray56);
        boolean boolean59 = quorumHierarchical48.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList57);
        boolean boolean60 = quorumHierarchical41.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList57);
        java.lang.Long[] longArray65 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet66 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean67 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet66, longArray65);
        boolean boolean68 = quorumHierarchical41.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        boolean boolean69 = quorumHierarchical37.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        boolean boolean70 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet66);
        java.lang.String str71 = quorumHierarchical0.toString();
        java.lang.String str72 = quorumHierarchical0.toString();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap73 = quorumHierarchical0.getObservingMembers();
        quorumHierarchical0.setVersion((long) (byte) 100);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical5, quorumHierarchical16, and quorumHierarchical0.", !(quorumHierarchical5.equals(quorumHierarchical16) && quorumHierarchical16.equals(quorumHierarchical0)) || quorumHierarchical5.equals(quorumHierarchical0));
    }

    @Test
    public void test516() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test516");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        boolean boolean1 = quorumHierarchical0.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical2 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long3 = quorumHierarchical2.getVersion();
        boolean boolean4 = quorumHierarchical2.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray5 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList6 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList6, learnerHandlerArray5);
        boolean boolean8 = quorumHierarchical2.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList6);
        boolean boolean9 = quorumHierarchical0.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList6);
        quorumHierarchical0.setVersion((long) (short) -1);
        boolean boolean12 = quorumHierarchical0.askOracle();
        long long13 = quorumHierarchical0.getVersion();
        java.util.Properties properties14 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical15 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties14);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap16 = quorumHierarchical15.getVotingMembers();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker17 = null;
        boolean boolean19 = quorumHierarchical15.revalidateVoteset(syncedLearnerTracker17, true);
        long long20 = quorumHierarchical15.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap21 = quorumHierarchical15.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap22 = quorumHierarchical15.getObservingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap23 = quorumHierarchical15.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical24 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker25 = null;
        boolean boolean27 = quorumHierarchical24.revalidateVoteset(syncedLearnerTracker25, false);
        java.lang.String str28 = quorumHierarchical24.toString();
        long long29 = quorumHierarchical24.getVersion();
        boolean boolean30 = quorumHierarchical24.getNeedOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical31 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str32 = quorumHierarchical31.toString();
        boolean boolean33 = quorumHierarchical31.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap34 = quorumHierarchical31.getAllMembers();
        java.util.Properties properties35 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical36 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties35);
        org.apache.zookeeper.server.quorum.Leader leader37 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray38 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList39 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean40 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList39, proposalArray38);
        boolean boolean42 = quorumHierarchical36.revalidateOutstandingProp(leader37, proposalList39, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical43 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean44 = quorumHierarchical43.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader45 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray46 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList47 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList47, proposalArray46);
        boolean boolean50 = quorumHierarchical43.revalidateOutstandingProp(leader45, proposalList47, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray51 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList52 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean53 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52, learnerHandlerArray51);
        boolean boolean54 = quorumHierarchical43.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean55 = quorumHierarchical36.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean56 = quorumHierarchical31.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean57 = quorumHierarchical24.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean58 = quorumHierarchical15.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean59 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical0, quorumHierarchical2, and quorumHierarchical15.", !(quorumHierarchical0.equals(quorumHierarchical2) && quorumHierarchical2.equals(quorumHierarchical15)) || quorumHierarchical0.equals(quorumHierarchical15));
    }

    @Test
    public void test517() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test517");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical1 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap2 = quorumHierarchical1.getAllMembers();
        boolean boolean3 = quorumHierarchical0.equals((java.lang.Object) longMap2);
        java.lang.String str4 = quorumHierarchical0.getOraclePath();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean6 = quorumHierarchical5.getNeedOracle();
        long long8 = quorumHierarchical5.getWeight(1L);
        boolean boolean9 = quorumHierarchical5.getNeedOracle();
        java.lang.String str10 = quorumHierarchical5.toString();
        java.lang.String str11 = quorumHierarchical5.getOraclePath();
        boolean boolean12 = quorumHierarchical5.askOracle();
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical14 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str15 = quorumHierarchical14.toString();
        quorumHierarchical14.setVersion((long) 100);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap18 = quorumHierarchical14.getVotingMembers();
        boolean boolean19 = quorumHierarchical14.askOracle();
        org.apache.zookeeper.server.quorum.Leader leader20 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical21 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long22 = quorumHierarchical21.getVersion();
        boolean boolean23 = quorumHierarchical21.getNeedOracle();
        boolean boolean24 = quorumHierarchical21.askOracle();
        boolean boolean25 = quorumHierarchical21.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader26 = null;
        java.util.Properties properties27 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical28 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties27);
        org.apache.zookeeper.server.quorum.Leader leader29 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray30 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList31 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList31, proposalArray30);
        boolean boolean34 = quorumHierarchical28.revalidateOutstandingProp(leader29, proposalList31, (long) (-1));
        boolean boolean36 = quorumHierarchical21.revalidateOutstandingProp(leader26, proposalList31, 10L);
        boolean boolean38 = quorumHierarchical14.revalidateOutstandingProp(leader20, proposalList31, 0L);
        boolean boolean40 = quorumHierarchical5.revalidateOutstandingProp(leader13, proposalList31, (long) (byte) 1);
        java.util.Properties properties41 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical42 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties41);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap43 = quorumHierarchical42.getVotingMembers();
        java.lang.Class<?> wildcardClass44 = quorumHierarchical42.getClass();
        boolean boolean45 = quorumHierarchical5.equals((java.lang.Object) wildcardClass44);
        java.util.Properties properties46 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical47 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties46);
        org.apache.zookeeper.server.quorum.Leader leader48 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray49 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList50 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean51 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList50, proposalArray49);
        boolean boolean53 = quorumHierarchical47.revalidateOutstandingProp(leader48, proposalList50, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical54 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean55 = quorumHierarchical54.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader56 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray57 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList58 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean59 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList58, proposalArray57);
        boolean boolean61 = quorumHierarchical54.revalidateOutstandingProp(leader56, proposalList58, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray62 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList63 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean64 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList63, learnerHandlerArray62);
        boolean boolean65 = quorumHierarchical54.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList63);
        boolean boolean66 = quorumHierarchical47.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList63);
        quorumHierarchical47.setVersion((long) (short) 0);
        boolean boolean69 = quorumHierarchical47.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical70 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean71 = quorumHierarchical70.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader72 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray73 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList74 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean75 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList74, proposalArray73);
        boolean boolean77 = quorumHierarchical70.revalidateOutstandingProp(leader72, proposalList74, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray78 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList79 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean80 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList79, learnerHandlerArray78);
        boolean boolean81 = quorumHierarchical70.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList79);
        boolean boolean82 = quorumHierarchical47.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList79);
        boolean boolean83 = quorumHierarchical5.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList79);
        boolean boolean84 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList79);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical1, quorumHierarchical0, and quorumHierarchical14.", !(quorumHierarchical1.equals(quorumHierarchical0) && quorumHierarchical0.equals(quorumHierarchical14)) || quorumHierarchical1.equals(quorumHierarchical14));
    }

    @Test
    public void test518() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test518");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker1 = null;
        boolean boolean3 = quorumHierarchical0.revalidateVoteset(syncedLearnerTracker1, false);
        java.lang.String str4 = quorumHierarchical0.getOraclePath();
        long long5 = quorumHierarchical0.getVersion();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker6 = null;
        boolean boolean8 = quorumHierarchical0.revalidateVoteset(syncedLearnerTracker6, true);
        boolean boolean9 = quorumHierarchical0.getNeedOracle();
        quorumHierarchical0.setVersion((long) 0);
        boolean boolean12 = quorumHierarchical0.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap13 = quorumHierarchical0.getAllMembers();
        boolean boolean14 = quorumHierarchical0.askOracle();
        quorumHierarchical0.setVersion((long) 10);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical17 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createEmpty();
        java.lang.Long[] longArray20 = new java.lang.Long[] { 100L, (-1L) };
        java.util.LinkedHashSet<java.lang.Long> longSet21 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet21, longArray20);
        boolean boolean23 = quorumHierarchical17.containsQuorum((java.util.Set<java.lang.Long>) longSet21);
        boolean boolean24 = quorumHierarchical17.getNeedOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap25 = quorumHierarchical17.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical26 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap27 = quorumHierarchical26.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap28 = quorumHierarchical26.getAllMembers();
        boolean boolean29 = quorumHierarchical17.equals((java.lang.Object) quorumHierarchical26);
        boolean boolean30 = quorumHierarchical26.askOracle();
        boolean boolean31 = quorumHierarchical26.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical32 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean33 = quorumHierarchical32.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader34 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray35 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList36 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean37 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList36, proposalArray35);
        boolean boolean39 = quorumHierarchical32.revalidateOutstandingProp(leader34, proposalList36, 0L);
        long long40 = quorumHierarchical32.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap41 = quorumHierarchical32.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap42 = quorumHierarchical32.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical43 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str44 = quorumHierarchical43.toString();
        boolean boolean45 = quorumHierarchical43.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap46 = quorumHierarchical43.getAllMembers();
        boolean boolean47 = quorumHierarchical43.askOracle();
        boolean boolean48 = quorumHierarchical32.equals((java.lang.Object) quorumHierarchical43);
        boolean boolean49 = quorumHierarchical32.getNeedOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical50 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        java.lang.String str51 = quorumHierarchical50.getOraclePath();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap52 = quorumHierarchical50.getAllMembers();
        java.lang.String str53 = quorumHierarchical50.toString();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap54 = quorumHierarchical50.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical55 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical56 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long57 = quorumHierarchical56.getVersion();
        quorumHierarchical56.setVersion((-1L));
        java.lang.Long[] longArray64 = new java.lang.Long[] { 10L, 10L, (-1L), 10L };
        java.util.LinkedHashSet<java.lang.Long> longSet65 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean66 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet65, longArray64);
        boolean boolean67 = quorumHierarchical56.containsQuorum((java.util.Set<java.lang.Long>) longSet65);
        boolean boolean68 = quorumHierarchical55.containsQuorum((java.util.Set<java.lang.Long>) longSet65);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical69 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createEmpty();
        java.lang.Long[] longArray72 = new java.lang.Long[] { 100L, (-1L) };
        java.util.LinkedHashSet<java.lang.Long> longSet73 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean74 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet73, longArray72);
        boolean boolean75 = quorumHierarchical69.containsQuorum((java.util.Set<java.lang.Long>) longSet73);
        boolean boolean76 = quorumHierarchical55.containsQuorum((java.util.Set<java.lang.Long>) longSet73);
        boolean boolean77 = quorumHierarchical50.containsQuorum((java.util.Set<java.lang.Long>) longSet73);
        boolean boolean78 = quorumHierarchical32.containsQuorum((java.util.Set<java.lang.Long>) longSet73);
        boolean boolean79 = quorumHierarchical26.containsQuorum((java.util.Set<java.lang.Long>) longSet73);
        boolean boolean80 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet73);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical17, quorumHierarchical43, and quorumHierarchical0.", !(quorumHierarchical17.equals(quorumHierarchical43) && quorumHierarchical43.equals(quorumHierarchical0)) || quorumHierarchical17.equals(quorumHierarchical0));
    }

    @Test
    public void test519() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test519");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap1 = quorumHierarchical0.getVotingMembers();
        quorumHierarchical0.setVersion((long) ' ');
        java.lang.String str4 = quorumHierarchical0.getOraclePath();
        org.apache.zookeeper.server.quorum.Leader leader5 = null;
        java.util.Properties properties6 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical7 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties6);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap8 = quorumHierarchical7.getVotingMembers();
        java.lang.String str9 = quorumHierarchical7.toString();
        boolean boolean10 = quorumHierarchical7.askOracle();
        boolean boolean11 = quorumHierarchical7.getNeedOracle();
        java.lang.String str12 = quorumHierarchical7.getOraclePath();
        org.apache.zookeeper.server.quorum.Leader leader13 = null;
        java.util.Properties properties14 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical15 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties14);
        org.apache.zookeeper.server.quorum.Leader leader16 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray17 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList18 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean19 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList18, proposalArray17);
        boolean boolean21 = quorumHierarchical15.revalidateOutstandingProp(leader16, proposalList18, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical22 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean23 = quorumHierarchical22.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader24 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray25 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList26 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList26, proposalArray25);
        boolean boolean29 = quorumHierarchical22.revalidateOutstandingProp(leader24, proposalList26, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray30 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList31 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList31, learnerHandlerArray30);
        boolean boolean33 = quorumHierarchical22.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList31);
        boolean boolean34 = quorumHierarchical15.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList31);
        java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList35 = null;
        boolean boolean36 = quorumHierarchical15.overrideQuorumDecision(learnerHandlerList35);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap37 = quorumHierarchical15.getVotingMembers();
        java.lang.String str38 = quorumHierarchical15.getOraclePath();
        org.apache.zookeeper.server.quorum.Leader leader39 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical40 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createEmpty();
        org.apache.zookeeper.server.quorum.Leader leader41 = null;
        java.util.Properties properties42 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical43 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties42);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap44 = quorumHierarchical43.getVotingMembers();
        java.lang.String str45 = quorumHierarchical43.toString();
        org.apache.zookeeper.server.quorum.Leader leader46 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray47 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList48 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean49 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList48, proposalArray47);
        boolean boolean51 = quorumHierarchical43.revalidateOutstandingProp(leader46, proposalList48, 0L);
        boolean boolean53 = quorumHierarchical40.revalidateOutstandingProp(leader41, proposalList48, (long) (short) -1);
        boolean boolean55 = quorumHierarchical15.revalidateOutstandingProp(leader39, proposalList48, 10L);
        boolean boolean57 = quorumHierarchical7.revalidateOutstandingProp(leader13, proposalList48, (long) (byte) 1);
        boolean boolean59 = quorumHierarchical0.revalidateOutstandingProp(leader5, proposalList48, 0L);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical0, quorumHierarchical7, and quorumHierarchical22.", !(quorumHierarchical0.equals(quorumHierarchical7) && quorumHierarchical7.equals(quorumHierarchical22)) || quorumHierarchical0.equals(quorumHierarchical22));
    }

    @Test
    public void test520() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test520");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long1 = quorumHierarchical0.getVersion();
        quorumHierarchical0.setVersion((-1L));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical4 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str5 = quorumHierarchical4.toString();
        boolean boolean6 = quorumHierarchical4.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap7 = quorumHierarchical4.getAllMembers();
        java.util.Properties properties8 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical9 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties8);
        org.apache.zookeeper.server.quorum.Leader leader10 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray11 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList12 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList12, proposalArray11);
        boolean boolean15 = quorumHierarchical9.revalidateOutstandingProp(leader10, proposalList12, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical16 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean17 = quorumHierarchical16.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader18 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray19 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList20 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean21 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList20, proposalArray19);
        boolean boolean23 = quorumHierarchical16.revalidateOutstandingProp(leader18, proposalList20, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray24 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList25 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean26 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25, learnerHandlerArray24);
        boolean boolean27 = quorumHierarchical16.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25);
        boolean boolean28 = quorumHierarchical9.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25);
        boolean boolean29 = quorumHierarchical4.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25);
        boolean boolean30 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList25);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical31 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean32 = quorumHierarchical31.getNeedOracle();
        boolean boolean33 = quorumHierarchical31.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap34 = quorumHierarchical31.getAllMembers();
        boolean boolean35 = quorumHierarchical0.equals((java.lang.Object) quorumHierarchical31);
        boolean boolean36 = quorumHierarchical31.getNeedOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap37 = quorumHierarchical31.getObservingMembers();
        long long38 = quorumHierarchical31.getVersion();
        org.apache.zookeeper.server.quorum.Leader leader39 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical40 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap41 = quorumHierarchical40.getVotingMembers();
        java.lang.String str42 = quorumHierarchical40.toString();
        java.lang.String str43 = quorumHierarchical40.getOraclePath();
        boolean boolean44 = quorumHierarchical40.getNeedOracle();
        boolean boolean45 = quorumHierarchical40.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader46 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical47 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean48 = quorumHierarchical47.getNeedOracle();
        boolean boolean49 = quorumHierarchical47.askOracle();
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker50 = null;
        boolean boolean52 = quorumHierarchical47.revalidateVoteset(syncedLearnerTracker50, true);
        org.apache.zookeeper.server.quorum.Leader leader53 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical54 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long55 = quorumHierarchical54.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap56 = quorumHierarchical54.getVotingMembers();
        org.apache.zookeeper.server.quorum.Leader leader57 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical58 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str59 = quorumHierarchical58.toString();
        boolean boolean60 = quorumHierarchical58.askOracle();
        java.lang.String str61 = quorumHierarchical58.toString();
        org.apache.zookeeper.server.quorum.Leader leader62 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical63 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long64 = quorumHierarchical63.getVersion();
        boolean boolean65 = quorumHierarchical63.getNeedOracle();
        boolean boolean66 = quorumHierarchical63.askOracle();
        boolean boolean67 = quorumHierarchical63.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader68 = null;
        java.util.Properties properties69 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical70 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties69);
        org.apache.zookeeper.server.quorum.Leader leader71 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray72 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList73 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean74 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList73, proposalArray72);
        boolean boolean76 = quorumHierarchical70.revalidateOutstandingProp(leader71, proposalList73, (long) (-1));
        boolean boolean78 = quorumHierarchical63.revalidateOutstandingProp(leader68, proposalList73, 10L);
        boolean boolean80 = quorumHierarchical58.revalidateOutstandingProp(leader62, proposalList73, (long) (short) -1);
        boolean boolean82 = quorumHierarchical54.revalidateOutstandingProp(leader57, proposalList73, (long) (byte) 10);
        boolean boolean84 = quorumHierarchical47.revalidateOutstandingProp(leader53, proposalList73, (long) (short) 0);
        boolean boolean86 = quorumHierarchical40.revalidateOutstandingProp(leader46, proposalList73, (long) ' ');
        boolean boolean88 = quorumHierarchical31.revalidateOutstandingProp(leader39, proposalList73, 52L);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical0, quorumHierarchical54, and quorumHierarchical31.", !(quorumHierarchical0.equals(quorumHierarchical54) && quorumHierarchical54.equals(quorumHierarchical31)) || quorumHierarchical0.equals(quorumHierarchical31));
    }

    @Test
    public void test521() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test521");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical1 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        boolean boolean2 = quorumHierarchical1.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical3 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str4 = quorumHierarchical3.toString();
        boolean boolean5 = quorumHierarchical3.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap6 = quorumHierarchical3.getAllMembers();
        java.util.Properties properties7 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical8 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties7);
        org.apache.zookeeper.server.quorum.Leader leader9 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray10 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList11 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList11, proposalArray10);
        boolean boolean14 = quorumHierarchical8.revalidateOutstandingProp(leader9, proposalList11, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical15 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean16 = quorumHierarchical15.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader17 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray18 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList19 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean20 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList19, proposalArray18);
        boolean boolean22 = quorumHierarchical15.revalidateOutstandingProp(leader17, proposalList19, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray23 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList24 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean25 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24, learnerHandlerArray23);
        boolean boolean26 = quorumHierarchical15.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24);
        boolean boolean27 = quorumHierarchical8.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24);
        boolean boolean28 = quorumHierarchical3.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24);
        boolean boolean29 = quorumHierarchical1.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24);
        boolean boolean30 = quorumHierarchical0.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList24);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical31 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str32 = quorumHierarchical31.toString();
        boolean boolean33 = quorumHierarchical31.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap34 = quorumHierarchical31.getAllMembers();
        java.util.Properties properties35 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical36 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties35);
        org.apache.zookeeper.server.quorum.Leader leader37 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray38 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList39 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean40 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList39, proposalArray38);
        boolean boolean42 = quorumHierarchical36.revalidateOutstandingProp(leader37, proposalList39, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical43 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean44 = quorumHierarchical43.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader45 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray46 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList47 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList47, proposalArray46);
        boolean boolean50 = quorumHierarchical43.revalidateOutstandingProp(leader45, proposalList47, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray51 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList52 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean53 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52, learnerHandlerArray51);
        boolean boolean54 = quorumHierarchical43.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean55 = quorumHierarchical36.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean56 = quorumHierarchical31.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        boolean boolean57 = quorumHierarchical0.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList52);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap58 = quorumHierarchical0.getObservingMembers();
        quorumHierarchical0.setVersion(32L);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical1, quorumHierarchical3, and quorumHierarchical0.", !(quorumHierarchical1.equals(quorumHierarchical3) && quorumHierarchical3.equals(quorumHierarchical0)) || quorumHierarchical1.equals(quorumHierarchical0));
    }

    @Test
    public void test522() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test522");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean1 = quorumHierarchical0.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader2 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray3 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList4 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList4, proposalArray3);
        boolean boolean7 = quorumHierarchical0.revalidateOutstandingProp(leader2, proposalList4, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray8 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList9 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList9, learnerHandlerArray8);
        boolean boolean11 = quorumHierarchical0.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList9);
        java.lang.String str12 = quorumHierarchical0.getOraclePath();
        boolean boolean13 = quorumHierarchical0.getNeedOracle();
        boolean boolean14 = quorumHierarchical0.askOracle();
        quorumHierarchical0.setVersion((long) (byte) 10);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap17 = quorumHierarchical0.getAllMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical18 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long19 = quorumHierarchical18.getVersion();
        boolean boolean20 = quorumHierarchical18.getNeedOracle();
        long long22 = quorumHierarchical18.getWeight((long) (short) 1);
        java.lang.String str23 = quorumHierarchical18.toString();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical24 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str25 = quorumHierarchical24.toString();
        quorumHierarchical24.setVersion((long) 100);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap28 = quorumHierarchical24.getObservingMembers();
        java.util.Properties properties29 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical30 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties29);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap31 = quorumHierarchical30.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap32 = quorumHierarchical30.getObservingMembers();
        java.util.Properties properties33 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical34 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties33);
        org.apache.zookeeper.server.quorum.Leader leader35 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray36 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList37 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean38 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList37, proposalArray36);
        boolean boolean40 = quorumHierarchical34.revalidateOutstandingProp(leader35, proposalList37, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical41 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean42 = quorumHierarchical41.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader43 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray44 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList45 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList45, proposalArray44);
        boolean boolean48 = quorumHierarchical41.revalidateOutstandingProp(leader43, proposalList45, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray49 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList50 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean51 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList50, learnerHandlerArray49);
        boolean boolean52 = quorumHierarchical41.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList50);
        boolean boolean53 = quorumHierarchical34.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList50);
        java.lang.Long[] longArray58 = new java.lang.Long[] { 100L, 1L, (-1L), 0L };
        java.util.LinkedHashSet<java.lang.Long> longSet59 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean60 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet59, longArray58);
        boolean boolean61 = quorumHierarchical34.containsQuorum((java.util.Set<java.lang.Long>) longSet59);
        boolean boolean62 = quorumHierarchical30.containsQuorum((java.util.Set<java.lang.Long>) longSet59);
        boolean boolean63 = quorumHierarchical24.containsQuorum((java.util.Set<java.lang.Long>) longSet59);
        boolean boolean64 = quorumHierarchical18.containsQuorum((java.util.Set<java.lang.Long>) longSet59);
        boolean boolean65 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet59);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical18, quorumHierarchical41, and quorumHierarchical0.", !(quorumHierarchical18.equals(quorumHierarchical41) && quorumHierarchical41.equals(quorumHierarchical0)) || quorumHierarchical18.equals(quorumHierarchical0));
    }

    @Test
    public void test523() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test523");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createEmpty();
        java.lang.Long[] longArray3 = new java.lang.Long[] { 100L, (-1L) };
        java.util.LinkedHashSet<java.lang.Long> longSet4 = new java.util.LinkedHashSet<java.lang.Long>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longSet4, longArray3);
        boolean boolean6 = quorumHierarchical0.containsQuorum((java.util.Set<java.lang.Long>) longSet4);
        boolean boolean7 = quorumHierarchical0.getNeedOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap8 = quorumHierarchical0.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical9 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap10 = quorumHierarchical9.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap11 = quorumHierarchical9.getAllMembers();
        boolean boolean12 = quorumHierarchical0.equals((java.lang.Object) quorumHierarchical9);
        boolean boolean13 = quorumHierarchical9.askOracle();
        // during test generation this statement threw an exception of type java.lang.NullPointerException in error
        long long15 = quorumHierarchical9.getWeight((-1L));
    }

    @Test
    public void test524() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test524");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        boolean boolean1 = quorumHierarchical0.askOracle();
        quorumHierarchical0.setVersion((long) (byte) 10);
        java.lang.String str4 = quorumHierarchical0.getOraclePath();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean6 = quorumHierarchical5.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader7 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray8 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList9 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList9, proposalArray8);
        boolean boolean12 = quorumHierarchical5.revalidateOutstandingProp(leader7, proposalList9, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray13 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList14 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList14, learnerHandlerArray13);
        boolean boolean16 = quorumHierarchical5.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList14);
        java.lang.String str17 = quorumHierarchical5.getOraclePath();
        boolean boolean18 = quorumHierarchical5.getNeedOracle();
        long long20 = quorumHierarchical5.getWeight((long) 1);
        java.util.Properties properties21 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical22 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties21);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap23 = quorumHierarchical22.getVotingMembers();
        java.lang.String str24 = quorumHierarchical22.toString();
        boolean boolean26 = quorumHierarchical22.equals((java.lang.Object) "server.1=localhost:2888:3888:participant\nserver.2=localhost:2889:3889:observer\ngroup.1=1\nweight.1=1\nweight.2=0\nversion=0");
        quorumHierarchical22.setVersion((long) 0);
        boolean boolean29 = quorumHierarchical22.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap30 = quorumHierarchical22.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap31 = quorumHierarchical22.getObservingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap32 = quorumHierarchical22.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical33 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean34 = quorumHierarchical33.getNeedOracle();
        long long36 = quorumHierarchical33.getWeight(1L);
        boolean boolean37 = quorumHierarchical33.getNeedOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap38 = quorumHierarchical33.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical39 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean40 = quorumHierarchical39.getNeedOracle();
        java.lang.String str41 = quorumHierarchical39.getOraclePath();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap42 = quorumHierarchical39.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical43 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long44 = quorumHierarchical43.getVersion();
        quorumHierarchical43.setVersion((-1L));
        java.lang.String str47 = quorumHierarchical43.getOraclePath();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical48 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long49 = quorumHierarchical48.getVersion();
        quorumHierarchical48.setVersion((-1L));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical52 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str53 = quorumHierarchical52.toString();
        boolean boolean54 = quorumHierarchical52.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap55 = quorumHierarchical52.getAllMembers();
        java.util.Properties properties56 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical57 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties56);
        org.apache.zookeeper.server.quorum.Leader leader58 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray59 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList60 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean61 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList60, proposalArray59);
        boolean boolean63 = quorumHierarchical57.revalidateOutstandingProp(leader58, proposalList60, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical64 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean65 = quorumHierarchical64.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader66 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray67 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList68 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean69 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList68, proposalArray67);
        boolean boolean71 = quorumHierarchical64.revalidateOutstandingProp(leader66, proposalList68, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray72 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList73 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean74 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73, learnerHandlerArray72);
        boolean boolean75 = quorumHierarchical64.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean76 = quorumHierarchical57.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean77 = quorumHierarchical52.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean78 = quorumHierarchical48.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean79 = quorumHierarchical43.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean80 = quorumHierarchical39.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean81 = quorumHierarchical33.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean82 = quorumHierarchical22.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean83 = quorumHierarchical5.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        boolean boolean84 = quorumHierarchical0.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList73);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical5, quorumHierarchical22, and quorumHierarchical0.", !(quorumHierarchical5.equals(quorumHierarchical22) && quorumHierarchical22.equals(quorumHierarchical0)) || quorumHierarchical5.equals(quorumHierarchical0));
    }

    @Test
    public void test525() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test525");
        java.util.Properties properties0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical1 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical2 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical3 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical4 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical6 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical7 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical8 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical9 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical10 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical11 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical12 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical13 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical14 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties0);
        long long15 = quorumHierarchical14.getVersion();
        // during test generation this statement threw an exception of type java.lang.NullPointerException in error
        long long17 = quorumHierarchical14.getWeight((long) ' ');
    }

    @Test
    public void test526() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test526");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long1 = quorumHierarchical0.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap2 = quorumHierarchical0.getObservingMembers();
        java.lang.String str3 = quorumHierarchical0.getOraclePath();
        java.util.Properties properties4 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties4);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap6 = quorumHierarchical5.getVotingMembers();
        java.lang.String str7 = quorumHierarchical5.toString();
        boolean boolean8 = quorumHierarchical5.askOracle();
        boolean boolean9 = quorumHierarchical0.equals((java.lang.Object) boolean8);
        org.apache.zookeeper.server.quorum.Leader leader10 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical11 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean12 = quorumHierarchical11.getNeedOracle();
        long long14 = quorumHierarchical11.getWeight(1L);
        org.apache.zookeeper.server.quorum.Leader leader15 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical16 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long17 = quorumHierarchical16.getVersion();
        boolean boolean18 = quorumHierarchical16.getNeedOracle();
        boolean boolean19 = quorumHierarchical16.askOracle();
        boolean boolean20 = quorumHierarchical16.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader21 = null;
        java.util.Properties properties22 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical23 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties22);
        org.apache.zookeeper.server.quorum.Leader leader24 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray25 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList26 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList26, proposalArray25);
        boolean boolean29 = quorumHierarchical23.revalidateOutstandingProp(leader24, proposalList26, (long) (-1));
        boolean boolean31 = quorumHierarchical16.revalidateOutstandingProp(leader21, proposalList26, 10L);
        boolean boolean33 = quorumHierarchical11.revalidateOutstandingProp(leader15, proposalList26, (long) (short) 10);
        boolean boolean35 = quorumHierarchical0.revalidateOutstandingProp(leader10, proposalList26, (long) (-1));
        java.lang.String str36 = quorumHierarchical0.getOraclePath();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical37 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean38 = quorumHierarchical37.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader39 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray40 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList41 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean42 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList41, proposalArray40);
        boolean boolean44 = quorumHierarchical37.revalidateOutstandingProp(leader39, proposalList41, 0L);
        long long45 = quorumHierarchical37.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap46 = quorumHierarchical37.getVotingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap47 = quorumHierarchical37.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical48 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str49 = quorumHierarchical48.toString();
        boolean boolean50 = quorumHierarchical48.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap51 = quorumHierarchical48.getAllMembers();
        boolean boolean52 = quorumHierarchical48.askOracle();
        boolean boolean53 = quorumHierarchical37.equals((java.lang.Object) quorumHierarchical48);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical54 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createThreeNodeCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap55 = quorumHierarchical54.getObservingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap56 = quorumHierarchical54.getVotingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical57 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str58 = quorumHierarchical57.toString();
        boolean boolean60 = quorumHierarchical57.equals((java.lang.Object) 100.0f);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical61 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean62 = quorumHierarchical61.getNeedOracle();
        long long64 = quorumHierarchical61.getWeight(1L);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical65 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long66 = quorumHierarchical65.getVersion();
        boolean boolean67 = quorumHierarchical65.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray68 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList69 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean70 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69, learnerHandlerArray68);
        boolean boolean71 = quorumHierarchical65.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        boolean boolean72 = quorumHierarchical61.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList69);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap73 = quorumHierarchical61.getAllMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap74 = quorumHierarchical61.getObservingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap75 = quorumHierarchical61.getObservingMembers();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical76 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createWithObserver();
        boolean boolean77 = quorumHierarchical76.getNeedOracle();
        long long79 = quorumHierarchical76.getWeight(1L);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical80 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long81 = quorumHierarchical80.getVersion();
        boolean boolean82 = quorumHierarchical80.getNeedOracle();
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray83 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList84 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean85 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList84, learnerHandlerArray83);
        boolean boolean86 = quorumHierarchical80.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList84);
        boolean boolean87 = quorumHierarchical76.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList84);
        boolean boolean88 = quorumHierarchical61.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList84);
        boolean boolean89 = quorumHierarchical57.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList84);
        boolean boolean90 = quorumHierarchical54.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList84);
        boolean boolean91 = quorumHierarchical37.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList84);
        boolean boolean92 = quorumHierarchical0.equals((java.lang.Object) learnerHandlerList84);
        quorumHierarchical0.setVersion((long) (-1));
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical5, quorumHierarchical16, and quorumHierarchical0.", !(quorumHierarchical5.equals(quorumHierarchical16) && quorumHierarchical16.equals(quorumHierarchical0)) || quorumHierarchical5.equals(quorumHierarchical0));
    }

    @Test
    public void test527() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test527");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.lang.String str1 = quorumHierarchical0.toString();
        quorumHierarchical0.setVersion((long) 100);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap4 = quorumHierarchical0.getVotingMembers();
        boolean boolean5 = quorumHierarchical0.getNeedOracle();
        quorumHierarchical0.setVersion((long) (byte) 1);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical8 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long9 = quorumHierarchical8.getVersion();
        boolean boolean10 = quorumHierarchical8.getNeedOracle();
        boolean boolean11 = quorumHierarchical8.askOracle();
        boolean boolean12 = quorumHierarchical8.getNeedOracle();
        boolean boolean13 = quorumHierarchical8.askOracle();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical14 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long15 = quorumHierarchical14.getVersion();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap16 = quorumHierarchical14.getVotingMembers();
        boolean boolean17 = quorumHierarchical8.equals((java.lang.Object) longMap16);
        boolean boolean18 = quorumHierarchical0.equals((java.lang.Object) quorumHierarchical8);
        org.apache.zookeeper.server.quorum.Leader leader19 = null;
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical20 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createSingleNode();
        long long21 = quorumHierarchical20.getVersion();
        quorumHierarchical20.setVersion((-1L));
        java.lang.String str24 = quorumHierarchical20.getOraclePath();
        quorumHierarchical20.setVersion((long) 10);
        org.apache.zookeeper.server.quorum.Leader leader27 = null;
        java.util.Properties properties28 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical29 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties28);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap30 = quorumHierarchical29.getVotingMembers();
        java.lang.String str31 = quorumHierarchical29.toString();
        org.apache.zookeeper.server.quorum.Leader leader32 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray33 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList34 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList34, proposalArray33);
        boolean boolean37 = quorumHierarchical29.revalidateOutstandingProp(leader32, proposalList34, 0L);
        boolean boolean39 = quorumHierarchical20.revalidateOutstandingProp(leader27, proposalList34, 1L);
        org.apache.zookeeper.server.quorum.Leader leader40 = null;
        java.util.Properties properties41 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical42 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties41);
        org.apache.zookeeper.server.quorum.Leader leader43 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray44 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList45 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList45, proposalArray44);
        boolean boolean48 = quorumHierarchical42.revalidateOutstandingProp(leader43, proposalList45, (long) (-1));
        boolean boolean50 = quorumHierarchical20.revalidateOutstandingProp(leader40, proposalList45, (long) (-1));
        boolean boolean52 = quorumHierarchical8.revalidateOutstandingProp(leader19, proposalList45, 35L);
        // Transitivity of equals
        org.junit.Assert.assertTrue("Contract failed: equals-transitive on quorumHierarchical20, quorumHierarchical8, and quorumHierarchical29.", !(quorumHierarchical20.equals(quorumHierarchical8) && quorumHierarchical8.equals(quorumHierarchical29)) || quorumHierarchical20.equals(quorumHierarchical29));
    }

    @Test
    public void test528() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "ErrorTest1.test528");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createMultiGroupCluster();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap1 = quorumHierarchical0.getAllMembers();
        java.util.Properties properties2 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical3 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties2);
        boolean boolean4 = quorumHierarchical0.equals((java.lang.Object) properties2);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical5 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties2);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical6 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties2);
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical quorumHierarchical7 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical(properties2);
        boolean boolean8 = quorumHierarchical7.getNeedOracle();
        boolean boolean9 = quorumHierarchical7.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap10 = quorumHierarchical7.getObservingMembers();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap11 = quorumHierarchical7.getVotingMembers();
        // during test generation this statement threw an exception of type java.lang.NullPointerException in error
        long long13 = quorumHierarchical7.getWeight(10L);
    }
}

