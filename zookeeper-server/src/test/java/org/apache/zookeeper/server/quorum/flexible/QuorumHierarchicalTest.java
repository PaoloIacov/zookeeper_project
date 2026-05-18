package org.apache.zookeeper.server.quorum.flexible;

import org.apache.zookeeper.common.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests per il metodo QuorumHierarchical(Properties qp)
 * Generati seguendo la metodologia Category Partition.
 *
 * Pattern: un metodo @Test per ogni caso di test (T1.1 - T1.11).
 * Ogni test ha @Timeout(5) come safety net contro loop infiniti o deadlock.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuorumHierarchicalTest {

    // ------------------------------------------------------------------------
    // T1.1 - C1: qp = null
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void NullProperties() {
        assertThrows(NullPointerException.class,
                () -> new QuorumHierarchical((Properties) null),
                "Dovrebbe lanciare NullPointerException se le properties sono null");
    }

    // ------------------------------------------------------------------------
    // T1.2 - C2: qp = empty
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void EmptyProperties() throws ConfigException {
        Properties qp = new Properties();
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertEquals(0, qh.getAllMembers().size(),
                "Una property vuota crea un oggetto vuoto, senza eccezioni");
    }

    // ------------------------------------------------------------------------
    // T1.3 - C3 (Base Choice con BV1): qp minimo 1 gruppo, 1 server
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void MinimalValidConfiguration() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(1, qh.getAllMembers().size(), "Dovrebbe esserci 1 membro totale");
        assertEquals(1, qh.getVotingMembers().size(), "Dovrebbe esserci 1 membro votante");
        assertEquals(1, qh.getWeight(1), "Il peso del server 1 dovrebbe essere 1");
    }

    // ------------------------------------------------------------------------
    // T1.4 - C4 (BV2): qp con server nel gruppo ma senza chiave weight
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void MissingWeightFallback() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2");
        // Forniamo il peso solo per il server 1, il server 2 userà il fallback
        qp.setProperty("weight.1", "3");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(3, qh.getWeight(1), "Il peso del server 1 dovrebbe essere 3");
        assertEquals(1, qh.getWeight(2), "Il peso del server 2 (mancante) dovrebbe fare fallback a 1");
    }

    // ------------------------------------------------------------------------
    // T1.5 - C5 (BV6): qp con overlap di ID=0
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void GroupOverlap() {
        Properties qp = new Properties();
        // Il server 0 è presente sia nel gruppo 1 che nel gruppo 2
        qp.setProperty("group.1", "0:1");
        qp.setProperty("group.2", "0:2");
        qp.setProperty("weight.0", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "1");
        qp.setProperty("server.0", "localhost:2888:3888:participant");
        qp.setProperty("server.1", "localhost:2889:3889:participant");
        qp.setProperty("server.2", "localhost:2890:3890:participant");

        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical(qp),
                "Dovrebbe lanciare ConfigException se un server appartiene a più gruppi");
    }

    // ------------------------------------------------------------------------
    // T1.6 - C6: qp con partecipanti ma nessuna chiave group.X
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void MissingGroupsForParticipants() {
        Properties qp = new Properties();
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        // Nessun group.1 definito

        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical(qp),
                "I partecipanti richiedono l'assegnazione a un gruppo");
    }

    // ------------------------------------------------------------------------
    // T1.7 - C7: qp misto (1 participant, 1 observer)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void MixedParticipantsAndObservers() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "0"); // Observer weight
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:observer");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(2, qh.getAllMembers().size(), "Dovrebbero esserci 2 membri in totale");
        assertEquals(1, qh.getObservingMembers().size(), "Dovrebbe esserci 1 observer");
        assertEquals(1, qh.getVotingMembers().size(), "Dovrebbe esserci 1 partecipante al voto");
        assertEquals(LearnerType.OBSERVER, qh.getAllMembers().get(2L).type,
                "Il server 2 deve essere registrato come OBSERVER");
    }

    // ------------------------------------------------------------------------
    // T1.8 - C8: qp con solo observer e nessun gruppo (valido)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void OnlyObserversNoGroups() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("weight.1", "0");
        qp.setProperty("server.1", "localhost:2888:3888:observer");
        // Nessun gruppo definito, ma essendo observer non dovrebbero causare eccezione

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(1, qh.getAllMembers().size(), "Dovrebbe esserci 1 membro totale");
        assertEquals(1, qh.getObservingMembers().size(), "Dovrebbe esserci 1 observer");
        assertEquals(0, qh.getVotingMembers().size(), "Dovrebbe esserci 0 partecipanti al voto");
    }

    // ------------------------------------------------------------------------
    // T1.9 - C9: qp popolata ma senza chiavi server.X
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void MissingServerKeys() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        // Manca la dichiarazione server.1

        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertEquals(0, qh.getAllMembers().size(), "Senza server configurati, l'istanza è vuota");
    }

    // ------------------------------------------------------------------------
    // T1.10 - C3: qp con ID=0 (BV6) e Peso=Long.MAX_VALUE (BV5)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void NumericalBoundaryValues() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "0");
        // Usiamo Long.MAX_VALUE per il peso, testando possibili overflow aritmetici
        qp.setProperty("weight.0", String.valueOf(Long.MAX_VALUE));
        qp.setProperty("server.0", "localhost:2888:3888:participant");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(Long.MAX_VALUE, qh.getWeight(0), "Il peso dovrebbe essere esattamente Long.MAX_VALUE");
        assertEquals(1, qh.getVotingMembers().size());
    }

    // ------------------------------------------------------------------------
    // T1.11 - C10: qp con formato server errato (es. ; invece di :)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void MalformedServerAddress() {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        // Usiamo il punto e virgola che avevamo visto causare l'errore di parsing
        qp.setProperty("server.1", "localhost:2888:3888;participant");

        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical(qp),
                "Dovrebbe lanciare ConfigException per formato indirizzo non valido");
    }

}
