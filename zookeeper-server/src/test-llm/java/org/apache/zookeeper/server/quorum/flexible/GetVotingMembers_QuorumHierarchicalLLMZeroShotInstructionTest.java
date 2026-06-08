package org.apache.zookeeper.server.quorum.flexible;

import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per la verifica del metodo getVotingMembers() 
 * della classe QuorumHierarchical.
 */
public class GetVotingMembers_QuorumHierarchicalLLMZeroShotInstructionTest {

    @Test
    @DisplayName("Dato un singolo gruppo con 3 server, getVotingMembers deve restituire tutti i 3 membri")
    public void testGetVotingMembers_SingleGroup_ReturnsAllMembers() throws Exception {
        // Arrange
        Properties props = new Properties();
        // Regola 1: Delimitatore ":"
        props.setProperty("group.1", "1:2:3");
        
        // Regola 2: Definizione obbligatoria dei server per evitare NPE
        props.setProperty("server.1", "localhost:2888:3888");
        props.setProperty("server.2", "localhost:2889:3889");
        props.setProperty("server.3", "localhost:2890:3890");
        
        props.setProperty("weight.1", "1");
        props.setProperty("weight.2", "1");
        props.setProperty("weight.3", "1");

        QuorumHierarchical quorumVerifier = new QuorumHierarchical(props);

        // Act
        Map<Long, QuorumServer> votingMembers = quorumVerifier.getVotingMembers();

        // Assert
        assertNotNull(votingMembers, "La mappa dei membri votanti non deve essere nulla");
        assertEquals(3, votingMembers.size(), "Devono esserci esattamente 3 membri votanti");
        assertTrue(votingMembers.containsKey(1L));
        assertTrue(votingMembers.containsKey(2L));
        assertTrue(votingMembers.containsKey(3L));
    }

    @Test
    @DisplayName("Dati gruppi multipli, getVotingMembers deve aggregare e restituire i membri di tutti i gruppi")
    public void testGetVotingMembers_MultipleGroups_ReturnsAggregatedMembers() throws Exception {
        // Arrange
        Properties props = new Properties();
        // Regola 1: Delimitatore ":"
        props.setProperty("group.1", "1:2");
        props.setProperty("group.2", "3:4");
        
        // Regola 2: Definizione obbligatoria dei server
        props.setProperty("server.1", "localhost:2888:3888");
        props.setProperty("server.2", "localhost:2889:3889");
        props.setProperty("server.3", "localhost:2890:3890");
        props.setProperty("server.4", "localhost:2891:3891");

        QuorumHierarchical quorumVerifier = new QuorumHierarchical(props);

        // Act
        Map<Long, QuorumServer> votingMembers = quorumVerifier.getVotingMembers();

        // Assert
        assertNotNull(votingMembers);
        assertEquals(4, votingMembers.size(), "Devono esserci esattamente 4 membri votanti registrati dai due gruppi");
        
        // Verifica puntuale dell'identità dei server
        assertEquals(1L, votingMembers.get(1L).id);
        assertEquals(2L, votingMembers.get(2L).id);
        assertEquals(3L, votingMembers.get(3L).id);
        assertEquals(4L, votingMembers.get(4L).id);
    }

    @Test
    @DisplayName("Dato un server con peso 0, deve comunque essere registrato nei membri votanti (confluence del parsing)")
    public void testGetVotingMembers_WithZeroWeight_IsIncludedInMembers() throws Exception {
        // Arrange
        Properties props = new Properties();
        props.setProperty("group.1", "1:2");
        
        props.setProperty("server.1", "localhost:2888:3888");
        props.setProperty("server.2", "localhost:2889:3889");
        
        // Assegniamo intenzionalmente un peso pari a 0 al server 2
        props.setProperty("weight.1", "1");
        props.setProperty("weight.2", "0");

        QuorumHierarchical quorumVerifier = new QuorumHierarchical(props);

        // Act
        Map<Long, QuorumServer> votingMembers = quorumVerifier.getVotingMembers();

        // Assert
        assertNotNull(votingMembers);
        assertEquals(2, votingMembers.size(), "Il server con peso 0 deve comunque essere presente nella struttura dati dei membri");
        assertTrue(votingMembers.containsKey(2L), "Il server 2 deve essere presente");
        assertEquals(0L, quorumVerifier.getWeight(2L), "Il peso del server 2 deve essere 0");
    }
}
