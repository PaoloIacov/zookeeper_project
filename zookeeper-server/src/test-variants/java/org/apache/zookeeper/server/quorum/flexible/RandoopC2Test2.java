package org.apache.zookeeper.server.quorum.flexible;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RandoopC2Test2 {

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
    public void test1001() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RandoopC2Test2.test1001");
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical_C2 quorumHierarchical_C2_0 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper_C2.createMultiGroupCluster();
        java.lang.String str1 = quorumHierarchical_C2_0.toString();
        boolean boolean2 = quorumHierarchical_C2_0.askOracle();
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap3 = quorumHierarchical_C2_0.getAllMembers();
        java.util.Properties properties4 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper_C2.createValidProperties();
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical_C2 quorumHierarchical_C2_5 = new org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical_C2(properties4);
        org.apache.zookeeper.server.quorum.Leader leader6 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray7 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList8 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean9 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList8, proposalArray7);
        boolean boolean11 = quorumHierarchical_C2_5.revalidateOutstandingProp(leader6, proposalList8, (long) (-1));
        org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical_C2 quorumHierarchical_C2_12 = org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper_C2.createWithObserver();
        boolean boolean13 = quorumHierarchical_C2_12.getNeedOracle();
        org.apache.zookeeper.server.quorum.Leader leader14 = null;
        org.apache.zookeeper.server.quorum.Leader.Proposal[] proposalArray15 = new org.apache.zookeeper.server.quorum.Leader.Proposal[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal> proposalList16 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.Leader.Proposal>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.Leader.Proposal>) proposalList16, proposalArray15);
        boolean boolean19 = quorumHierarchical_C2_12.revalidateOutstandingProp(leader14, proposalList16, 0L);
        org.apache.zookeeper.server.quorum.LearnerHandler[] learnerHandlerArray20 = new org.apache.zookeeper.server.quorum.LearnerHandler[] {};
        java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler> learnerHandlerList21 = new java.util.ArrayList<org.apache.zookeeper.server.quorum.LearnerHandler>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList21, learnerHandlerArray20);
        boolean boolean23 = quorumHierarchical_C2_12.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList21);
        boolean boolean24 = quorumHierarchical_C2_5.updateNeedOracle((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList21);
        boolean boolean25 = quorumHierarchical_C2_0.overrideQuorumDecision((java.util.List<org.apache.zookeeper.server.quorum.LearnerHandler>) learnerHandlerList21);
        org.apache.zookeeper.server.quorum.SyncedLearnerTracker syncedLearnerTracker26 = null;
        boolean boolean28 = quorumHierarchical_C2_0.revalidateVoteset(syncedLearnerTracker26, true);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap29 = quorumHierarchical_C2_0.getObservingMembers();
        quorumHierarchical_C2_0.setVersion(0L);
        java.util.Map<java.lang.Long, org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer> longMap32 = quorumHierarchical_C2_0.getObservingMembers();
        java.lang.Class<?> wildcardClass33 = longMap32.getClass();
        org.junit.Assert.assertNotNull(quorumHierarchical_C2_0);
        org.junit.Assert.assertEquals("'" + str1 + "' != '" + "server.1=localhost:2888:3888:participant\nserver.2=localhost:2889:3889:participant\nserver.3=localhost:2890:3890:participant\nserver.4=localhost:2891:3891:participant\nserver.5=localhost:2892:3892:participant\nserver.6=localhost:2893:3893:participant\ngroup.1=1:2:3\ngroup.2=4:5:6\nweight.1=1\nweight.2=1\nweight.3=1\nweight.4=1\nweight.5=1\nweight.6=1\nversion=0" + "'", str1, "server.1=localhost:2888:3888:participant\nserver.2=localhost:2889:3889:participant\nserver.3=localhost:2890:3890:participant\nserver.4=localhost:2891:3891:participant\nserver.5=localhost:2892:3892:participant\nserver.6=localhost:2893:3893:participant\ngroup.1=1:2:3\ngroup.2=4:5:6\nweight.1=1\nweight.2=1\nweight.3=1\nweight.4=1\nweight.5=1\nweight.6=1\nversion=0");
        org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + false + "'", boolean2 == false);
        org.junit.Assert.assertNotNull(longMap3);
        org.junit.Assert.assertNotNull(properties4);
        org.junit.Assert.assertNotNull(proposalArray7);
        org.junit.Assert.assertArrayEquals(proposalArray7, new org.apache.zookeeper.server.quorum.Leader.Proposal[] {});
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", boolean9 == false);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", boolean11 == false);
        org.junit.Assert.assertNotNull(quorumHierarchical_C2_12);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", boolean13 == false);
        org.junit.Assert.assertNotNull(proposalArray15);
        org.junit.Assert.assertArrayEquals(proposalArray15, new org.apache.zookeeper.server.quorum.Leader.Proposal[] {});
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", boolean17 == false);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", boolean19 == false);
        org.junit.Assert.assertNotNull(learnerHandlerArray20);
        org.junit.Assert.assertArrayEquals(learnerHandlerArray20, new org.apache.zookeeper.server.quorum.LearnerHandler[] {});
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", boolean22 == false);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", boolean23 == false);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", boolean24 == false);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", boolean25 == false);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", boolean28 == false);
        org.junit.Assert.assertNotNull(longMap29);
        org.junit.Assert.assertNotNull(longMap32);
        org.junit.Assert.assertNotNull(wildcardClass33);
    }
}

