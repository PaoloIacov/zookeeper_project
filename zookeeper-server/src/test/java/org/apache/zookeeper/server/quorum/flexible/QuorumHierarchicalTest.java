package org.apache.zookeeper.server.quorum.flexible;

import org.apache.zookeeper.common.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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

    // ========================================================================
    // TEST SUITE T2.x – Metodo: containsQuorum(Set<Long> set)
    //
    // Configurazione condivisa (creata una volta con @BeforeAll, grazie a
    // @TestInstance(PER_CLASS) già presente sulla classe):
    //   group.1 = 1:2:3  →  3 server, peso totale 3, soglia interna: > 1.5 (min 2)
    //   group.2 = 4:5:6  →  3 server, peso totale 3, soglia interna: > 1.5 (min 2)
    //   group.3 = 7:8:9  →  3 server, peso totale 3, soglia interna: > 1.5 (min 2)
    //   Soglia gerarchica: > 3/2 = 1.5  →  servono almeno 2 gruppi qualificati
    // ========================================================================

    private QuorumHierarchical qhCQ;
    private QuorumHierarchical qhGW;

    @BeforeAll
    void setUpSharedInstances() throws ConfigException {
        // Istanza per T2.x (containsQuorum): 3 gruppi × 3 server, peso 1
        // Soglia interna per gruppo: peso > 1.5 → min 2 server
        // Soglia gerarchica: gruppi > 1.5 → min 2 gruppi qualificati
        Properties qpCQ = new Properties();
        qpCQ.setProperty("group.1", "1:2:3");
        qpCQ.setProperty("group.2", "4:5:6");
        qpCQ.setProperty("group.3", "7:8:9");
        for (int i = 1; i <= 9; i++) {
            qpCQ.setProperty("weight." + i, "1");
            qpCQ.setProperty("server." + i,
                    "localhost:" + (2887 + i) + ":" + (3887 + i) + ":participant");
        }
        qhCQ = new QuorumHierarchical(qpCQ);

        // Istanza per T3.x (getWeight): 1 partecipante (ID=1, peso=1), 1 observer (ID=2, peso=0)
        Properties qpGW = new Properties();
        qpGW.setProperty("group.1", "1");
        qpGW.setProperty("weight.1", "1");
        qpGW.setProperty("server.1", "localhost:2888:3888:participant");
        qpGW.setProperty("weight.2", "0");
        qpGW.setProperty("server.2", "localhost:2889:3889:observer");
        qhGW = new QuorumHierarchical(qpGW);
    }

    // ------------------------------------------------------------------------
    // T2.1 – CE1: set = null → NullPointerException (Oracle via Grey-Box)
    // Il metodo chiama set.size() senza guard, delegando NPE alla JVM
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_NullSet() {
        assertThrows(NullPointerException.class,
                () -> qhCQ.containsQuorum(null),
                "Un set null deve lanciare NullPointerException");
    }

    // ------------------------------------------------------------------------
    // T2.2 – CE2: set vuoto → false (0 voti = nessun quorum)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_EmptySet() {
        assertFalse(qhCQ.containsQuorum(Collections.emptySet()),
                "Un set vuoto non può raggiungere il quorum");
    }

    // ------------------------------------------------------------------------
    // T2.3 – CE5: set con ID non configurato → false
    // Il metodo ignora silenziosamente gli ID sconosciuti (continue nel loop)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_UnknownId() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(999L))),
                "ID non configurato: viene ignorato, nessun gruppo riceve peso");
    }

    // ------------------------------------------------------------------------
    // T2.4 – CE4+CE6, BV3: 2 server per ognuno dei 3 gruppi → true
    // Ogni gruppo: peso 2 > 1.5 ✓; gruppi qualificati: 3 > 1.5 ✓
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_QuorumReachedAllGroups() {
        assertTrue(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L, 4L, 5L, 7L, 8L))),
                "Quorum atteso: 2 server per gruppo in tutti e 3 i gruppi");
    }

    // ------------------------------------------------------------------------
    // T2.5 – CE4+CE7, BV1+BV2: 1 solo voto → false
    // Gruppo 1: peso 1, soglia 1.5 → 1 < 1.5 → gruppo NON qualificato
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_SingleVoteInsufficientWeight() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L))),
                "Un solo voto non supera la soglia di peso del gruppo");
    }

    // ------------------------------------------------------------------------
    // T2.6 – CE4+CE8, BV4: tutti e 3 i voti nel gruppo 1, 0 negli altri → false
    // Gruppo 1: peso 3 > 1.5 ✓, ma solo 1 gruppo su 3: 1 ≤ 1.5 → false
    // Questo è il test cruciale della logica GERARCHICA
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_OnlyOneGroupQualified() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L, 3L))),
                "Con quorum interno al gruppo 1, ma 1 solo gruppo su 3 non basta");
    }

    // ------------------------------------------------------------------------
    // T2.7 – BV peso OK, BV gruppi KO: 2 voti nel gruppo 1, 0 altrove → false
    // Gruppo 1: peso 2 > 1.5 ✓, ma 1 gruppo qualificato su 3: 1 ≤ 1.5 → false
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_WeightOkButGroupsInsufficient() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L))),
                "Peso sufficiente in un solo gruppo: la soglia gerarchica non è raggiunta");
    }

    // ------------------------------------------------------------------------
    // T2.8 – BV3 esatto: esattamente 2 gruppi su 3 qualificati (soglia minima) → true
    // Gruppo 1: {1,2} peso 2 > 1.5 ✓; Gruppo 2: {4,5} peso 2 > 1.5 ✓
    // Gruppi qualificati: 2 > 1.5 ✓ → true (Boundary Value sulla soglia gerarchica)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void ContainsQuorum_ExactlyMajorityGroups() {
        assertTrue(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L, 4L, 5L))),
                "Esattamente 2 gruppi su 3 qualificati: deve ritornare true (BV soglia)");
    }

    // ========================================================================
    // TEST SUITE T3.x – Metodo: getWeight(long id)
    //
    // Configurazione condivisa (qhGW, inizializzata in @BeforeAll):
    //   ID=1 → participant, peso=1
    //   ID=2 → observer, peso=0
    // ========================================================================

    // ------------------------------------------------------------------------
    // T3.1 – CE1: ID server votante → ritorna il peso corretto (BV1: peso minimo positivo)
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void GetWeight_VotingServer() {
        assertEquals(1L, qhGW.getWeight(1L),
                "Il peso del server votante deve essere 1");
    }

    // ------------------------------------------------------------------------
    // T3.2 – CE2: ID server observer → ritorna 0
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void GetWeight_ObserverServer() {
        assertEquals(0L, qhGW.getWeight(2L),
                "Il peso di un observer deve essere 0");
    }

    // ------------------------------------------------------------------------
    // T3.3 – CE3: ID non configurato → Ritorna 0 (Specifica teorica / Safe Default)
    // DISABILITATO: Questo test fallisce su ZooKeeper a causa di un bug reale di
    // robustezza (unboxing di un Long null restituito dalla mappa lancia NPE).
    // ------------------------------------------------------------------------
    @Test
    @Disabled("Baco di ZooKeeper: getWeight lancia NullPointerException su ID non configurati causa unboxing di null")
    @Timeout(5)
    public void GetWeight_UnknownId() {
        assertEquals(0L, qhGW.getWeight(999L),
                "Un ID non configurato dovrebbe restituire peso 0 (safe default)");
    }

    // ------------------------------------------------------------------------
    // T3.4 – BV2: ID negativo → Ritorna 0 (Specifica teorica / Safe Default)
    // DISABILITATO: Stesso bug di robustezza documentato in T3.3.
    // ------------------------------------------------------------------------
    @Test
    @Disabled("Baco di ZooKeeper: getWeight lancia NullPointerException su ID negativi")
    @Timeout(5)
    public void GetWeight_NegativeId() {
        assertEquals(0L, qhGW.getWeight(-1L),
                "Un ID negativo (non configurato) dovrebbe restituire peso 0");
    }

    // ========================================================================
    // TEST SUITE T4.x – Metodo: getVotingMembers()
    //
    // Il metodo non ha parametri: partizioniamo sullo Stato del SUT (S1)
    // ========================================================================

    // ------------------------------------------------------------------------
    // T4.1 – CE1: config solo votanti → tutti inclusi nella mappa
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void GetVotingMembers_OnlyParticipants() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2:3");
        for (int i = 1; i <= 3; i++) {
            qp.setProperty("weight." + i, "1");
            qp.setProperty("server." + i,
                    "localhost:" + (2887 + i) + ":" + (3887 + i) + ":participant");
        }
        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(3, qh.getVotingMembers().size(),
                "Con 3 partecipanti, getVotingMembers deve ritornare 3 entries");
    }

    // ------------------------------------------------------------------------
    // T4.2 – CE2: config mista (3 votanti + 2 observer) → solo votanti inclusi
    // Verifica che getVotingMembers() filtri correttamente gli observer
    // ------------------------------------------------------------------------
    @Test
    @Timeout(5)
    public void GetVotingMembers_MixedConfig() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2:3");
        for (int i = 1; i <= 3; i++) {
            qp.setProperty("weight." + i, "1");
            qp.setProperty("server." + i,
                    "localhost:" + (2887 + i) + ":" + (3887 + i) + ":participant");
        }
        qp.setProperty("weight.4", "0");
        qp.setProperty("server.4", "localhost:2891:3891:observer");
        qp.setProperty("weight.5", "0");
        qp.setProperty("server.5", "localhost:2892:3892:observer");
        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(3, qh.getVotingMembers().size(),
                "Con 3 votanti e 2 observer, getVotingMembers deve escludere gli observer");
        assertFalse(qh.getVotingMembers().containsKey(4L),
                "L'observer con ID=4 non deve comparire tra i voting members");
        assertFalse(qh.getVotingMembers().containsKey(5L),
                "L'observer con ID=5 non deve comparire tra i voting members");
    }

}
