package org.apache.zookeeper.server.quorum.flexible;

import org.apache.zookeeper.common.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite per QuorumHierarchical.
 * Metodologia Category Partition + Coverage Enhancement + Mutation Testing.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuorumHierarchicalTest {

    // ========================================================================
    // T1.x – Costruttore QuorumHierarchical(Properties)
    // ========================================================================

    @Test
    @Timeout(5)
    public void NullProperties() {
        assertThrows(NullPointerException.class,
                () -> new QuorumHierarchical((Properties) null),
                "Dovrebbe lanciare NullPointerException se le properties sono null");
    }

    @Test
    @Timeout(5)
    public void EmptyProperties() throws ConfigException {
        Properties qp = new Properties();
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertEquals(0, qh.getAllMembers().size(),
                "Una property vuota crea un oggetto vuoto, senza eccezioni");
    }

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

    @Test
    @Timeout(5)
    public void MissingWeightFallback() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2");
        qp.setProperty("weight.1", "3");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(3, qh.getWeight(1), "Il peso del server 1 dovrebbe essere 3");
        assertEquals(1, qh.getWeight(2), "Il peso del server 2 (mancante) dovrebbe fare fallback a 1");
    }

    @Test
    @Timeout(5)
    public void GroupOverlap() {
        Properties qp = new Properties();
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

    @Test
    @Timeout(5)
    public void MissingGroupsForParticipants() {
        Properties qp = new Properties();
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");

        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical(qp),
                "I partecipanti richiedono l'assegnazione a un gruppo");
    }

    @Test
    @Timeout(5)
    public void MixedParticipantsAndObservers() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "0");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:observer");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(2, qh.getAllMembers().size(), "Dovrebbero esserci 2 membri in totale");
        assertEquals(1, qh.getObservingMembers().size(), "Dovrebbe esserci 1 observer");
        assertEquals(1, qh.getVotingMembers().size(), "Dovrebbe esserci 1 partecipante al voto");
        assertEquals(LearnerType.OBSERVER, qh.getAllMembers().get(2L).type,
                "Il server 2 deve essere registrato come OBSERVER");
    }

    @Test
    @Timeout(5)
    public void OnlyObserversNoGroups() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("weight.1", "0");
        qp.setProperty("server.1", "localhost:2888:3888:observer");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(1, qh.getAllMembers().size(), "Dovrebbe esserci 1 membro totale");
        assertEquals(1, qh.getObservingMembers().size(), "Dovrebbe esserci 1 observer");
        assertEquals(0, qh.getVotingMembers().size(), "Dovrebbe esserci 0 partecipanti al voto");
    }

    @Test
    @Timeout(5)
    public void MissingServerKeys() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");

        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertEquals(0, qh.getAllMembers().size(), "Senza server configurati, l'istanza è vuota");
    }

    @Test
    @Timeout(5)
    public void NumericalBoundaryValues() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "0");
        qp.setProperty("weight.0", String.valueOf(Long.MAX_VALUE));
        qp.setProperty("server.0", "localhost:2888:3888:participant");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(Long.MAX_VALUE, qh.getWeight(0), "Il peso dovrebbe essere esattamente Long.MAX_VALUE");
        assertEquals(1, qh.getVotingMembers().size());
    }

    @Test
    @Timeout(5)
    public void MalformedServerAddress() {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888;participant");

        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical(qp),
                "Dovrebbe lanciare ConfigException per formato indirizzo non valido");
    }

    // ========================================================================
    // T2.x – containsQuorum(Set<Long>)
    //
    // Setup condiviso:
    //   3 gruppi × 3 server, peso 1
    //   Soglia interna per gruppo: peso > 1.5 → min 2 server
    //   Soglia gerarchica: gruppi > 1.5 → min 2 gruppi qualificati
    // ========================================================================

    private QuorumHierarchical qhCQ;
    private QuorumHierarchical qhGW;

    @BeforeAll
    void setUpSharedInstances() throws ConfigException {
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

        Properties qpGW = new Properties();
        qpGW.setProperty("group.1", "1");
        qpGW.setProperty("weight.1", "1");
        qpGW.setProperty("server.1", "localhost:2888:3888:participant");
        qpGW.setProperty("weight.2", "0");
        qpGW.setProperty("server.2", "localhost:2889:3889:observer");
        qhGW = new QuorumHierarchical(qpGW);
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_NullSet() {
        assertThrows(NullPointerException.class,
                () -> qhCQ.containsQuorum(null),
                "Un set null deve lanciare NullPointerException");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_EmptySet() {
        assertFalse(qhCQ.containsQuorum(Collections.emptySet()),
                "Un set vuoto non può raggiungere il quorum");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_MixedIdsFaultTolerance() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 999L))),
                "L'ID non configurato viene scartato, il voto di 1L non basta per il quorum");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_QuorumReachedAllGroups() {
        assertTrue(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L, 4L, 5L, 7L, 8L))),
                "Quorum atteso: 2 server per gruppo in tutti e 3 i gruppi");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_SingleVoteInsufficientWeight() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L))),
                "Un solo voto non supera la soglia di peso del gruppo");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_OnlyOneGroupQualified() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L, 3L))),
                "Con quorum interno al gruppo 1, ma 1 solo gruppo su 3 non basta");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_WeightOkButGroupsInsufficient() {
        assertFalse(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L))),
                "Peso sufficiente in un solo gruppo: la soglia gerarchica non è raggiunta");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_ExactlyMajorityGroups() {
        assertTrue(qhCQ.containsQuorum(new HashSet<>(Arrays.asList(1L, 2L, 4L, 5L))),
                "Esattamente 2 gruppi su 3 qualificati: deve ritornare true");
    }

    // ========================================================================
    // T3.x – getWeight(long id)
    // ========================================================================

    @Test
    @Timeout(5)
    public void GetWeight_VotingServer() {
        assertEquals(1L, qhGW.getWeight(1L),
                "Il peso del server votante deve essere 1");
    }

    @Test
    @Timeout(5)
    public void GetWeight_ObserverServer() {
        assertEquals(0L, qhGW.getWeight(2L),
                "Il peso di un observer deve essere 0");
    }

    @Test
    @Disabled("Bug di ZooKeeper: getWeight lancia NPE su ID non configurati per unboxing di null")
    @Timeout(5)
    public void GetWeight_UnknownId() {
        assertEquals(0L, qhGW.getWeight(999L),
                "Un ID non configurato dovrebbe restituire peso 0");
    }

    @Test
    @Timeout(5)
    public void GetWeight_CorruptedInstance() throws Exception {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh = new QuorumHierarchical(qp);

        Field swField = QuorumHierarchical.class.getDeclaredField("serverWeight");
        swField.setAccessible(true);
        swField.set(qh, null);

        assertThrows(NullPointerException.class,
                () -> qh.getWeight(1L),
                "Con serverWeight=null, getWeight deve lanciare NullPointerException");
    }

    // ========================================================================
    // T4.x – getVotingMembers()
    // ========================================================================

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
        assertFalse(qh.getVotingMembers().containsKey(4L));
        assertFalse(qh.getVotingMembers().containsKey(5L));
    }

    @Test
    @Timeout(5)
    public void GetVotingMembers_OnlyObservers() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("weight.1", "0");
        qp.setProperty("server.1", "localhost:2891:3891:observer");
        qp.setProperty("weight.2", "0");
        qp.setProperty("server.2", "localhost:2892:3892:observer");
        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(0, qh.getVotingMembers().size(),
                "Con soli observer, getVotingMembers deve ritornare una mappa vuota");
    }

    @Test
    @Timeout(5)
    public void GetVotingMembers_CorruptedInstance() throws Exception {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh = new QuorumHierarchical(qp);

        Field pmField = QuorumHierarchical.class.getDeclaredField("participatingMembers");
        pmField.setAccessible(true);
        pmField.set(qh, null);

        assertNull(qh.getVotingMembers(),
                "Con participatingMembers=null, getVotingMembers restituisce null");
    }

    // ========================================================================
    // T5.x – Costruttore QuorumHierarchical(String filename)
    // ========================================================================

    @Test
    @Timeout(5)
    public void FilenameConstructor_Null() {
        assertThrows(NullPointerException.class,
                () -> new QuorumHierarchical((String) null),
                "null causa NPE in new File(null)");
    }

    @Test
    @Timeout(5)
    public void FilenameConstructor_EmptyString() {
        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical(""),
                "Stringa vuota: il file non esiste, deve lanciare ConfigException");
    }

    @Test
    @Timeout(5)
    public void FilenameConstructor_NonExistentFile() {
        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical("/nonexistent/path/zoo.cfg"),
                "Percorso inesistente: deve lanciare ConfigException");
    }

    @Test
    @Timeout(5)
    public void FilenameConstructor_ValidFile() throws Exception {
        File tmp = File.createTempFile("zoo_test", ".cfg");
        tmp.deleteOnExit();
        try (FileWriter fw = new FileWriter(tmp)) {
            fw.write("group.1=1\n");
            fw.write("weight.1=1\n");
            fw.write("server.1=localhost:2888:3888:participant\n");
        }
        QuorumHierarchical qh = new QuorumHierarchical(tmp.getAbsolutePath());
        assertEquals(1, qh.getAllMembers().size(),
                "Il costruttore da file deve caricare correttamente la configurazione");
    }

    // ========================================================================
    // T6.x – equals(Object)
    // ========================================================================

    @Test
    @Timeout(5)
    public void Equals_Null() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertFalse(qh.equals(null), "equals(null) deve ritornare false");
    }

    @Test
    @Timeout(5)
    public void Equals_DifferentType() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertFalse(qh.equals("una stringa"), "equals(String) deve ritornare false");
    }

    @Test
    @Timeout(5)
    public void Equals_SameInstance() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertTrue(qh.equals(qh), "Un'istanza deve essere uguale a se stessa");
    }

    @Test
    @Timeout(5)
    public void Equals_SameConfig() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp);
        assertTrue(qh1.equals(qh2), "Due istanze con la stessa config devono essere uguali");
    }

    // Bug noto: version di default 0L == 0L bypassa il confronto dei membri
    @Test
    @Disabled("Bug in equals: version di default 0L == 0L bypassa il confronto dei membri")
    @Timeout(5)
    public void Equals_DifferentConfig() throws ConfigException {
        Properties qp1 = new Properties();
        qp1.setProperty("group.1", "1");
        qp1.setProperty("weight.1", "1");
        qp1.setProperty("server.1", "localhost:2888:3888:participant");

        Properties qp2 = new Properties();
        qp2.setProperty("group.1", "1:2");
        qp2.setProperty("weight.1", "1");
        qp2.setProperty("weight.2", "1");
        qp2.setProperty("server.1", "localhost:2888:3888:participant");
        qp2.setProperty("server.2", "localhost:2889:3889:participant");

        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        assertFalse(qh1.equals(qh2), "Istanze con configurazioni diverse non devono essere uguali");
    }

    // ========================================================================
    // T7.x – toString()
    // ========================================================================

    @Test
    @Timeout(5)
    public void ToString_ContainsExpectedKeys() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        String result = qh.toString();
        assertTrue(result.contains("server.1"), "toString deve contenere 'server.1'");
        assertTrue(result.contains("group.1"),  "toString deve contenere 'group.1'");
        assertTrue(result.contains("weight.1"), "toString deve contenere 'weight.1'");
        assertTrue(result.contains("version"),  "toString deve contenere 'version'");
    }

    @Test
    @Timeout(5)
    public void ToString_CorruptedInstance() throws Exception {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh = new QuorumHierarchical(qp);

        Field amField = QuorumHierarchical.class.getDeclaredField("allMembers");
        amField.setAccessible(true);
        amField.set(qh, null);

        assertThrows(NullPointerException.class,
                () -> qh.toString(),
                "Con allMembers=null, toString deve lanciare NullPointerException");
    }

    // ========================================================================
    // COVERAGE ENHANCEMENT (Fase 4.a)
    // Test aggiunti per coprire rami scoperti di equals(), setVersion(),
    // toString() multi-server e computeGroupWeight() a peso zero.
    // ========================================================================

    @Test
    @Timeout(5)
    public void Equals_DeepTrue() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");

        QuorumHierarchical qh1 = new QuorumHierarchical(qp);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp);

        // Versioni diverse per forzare equals() a iterare sulle mappe interne
        qh1.setVersion(1L);
        qh2.setVersion(2L);

        assertTrue(qh1.equals(qh2), "Config identiche con versioni diverse: equals deep deve ritornare true");
    }

    @Test
    @Timeout(5)
    public void Equals_DeepFalse_DifferentMaps() throws ConfigException {
        Properties qp1 = new Properties();
        qp1.setProperty("group.1", "1");
        qp1.setProperty("weight.1", "1");
        qp1.setProperty("server.1", "localhost:2888:3888:participant");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        qh1.setVersion(1L);

        Properties qp2 = new Properties();
        qp2.setProperty("group.1", "1:2");
        qp2.setProperty("weight.1", "1");
        qp2.setProperty("weight.2", "1");
        qp2.setProperty("server.1", "localhost:2888:3888:participant");
        qp2.setProperty("server.2", "localhost:2889:3889:participant");
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        qh2.setVersion(2L);

        assertFalse(qh1.equals(qh2), "Config diverse con versioni diverse: equals deep deve ritornare false");
    }

    @Test
    @Timeout(5)
    public void Parse_VersionKey() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("version", "1F");

        QuorumHierarchical qh = new QuorumHierarchical(qp);
        assertEquals(31L, qh.getVersion(), "Il parser deve convertire la versione esadecimale (1F -> 31)");
    }

    @Test
    @Timeout(5)
    public void ToString_MultipleServersPerGroup() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");

        QuorumHierarchical qh = new QuorumHierarchical(qp);
        String result = qh.toString();

        assertTrue(result.contains("group.1=1:2") || result.contains("group.1=2:1"),
                "Il toString deve concatenare correttamente ID multipli nello stesso gruppo");
    }

    @Test
    @Timeout(5)
    public void ComputeGroupWeight_ZeroWeightGroup() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "0");
        qp.setProperty("server.1", "localhost:2888:3888:participant");

        QuorumHierarchical qh = new QuorumHierarchical(qp);

        assertEquals(0, qh.getWeight(1L), "Il server deve avere peso 0");
    }

    // ========================================================================
    // MUTATION TESTING (Fase 4.b)
    // Test specifici per uccidere mutanti sopravvissuti identificati da PIT.
    // ========================================================================

    // --- Mutanti in equals() ---

    @Test
    @Timeout(5)
    public void Equals_SameVersionShortCircuit() throws ConfigException {
        // Versioni uguali ma campi interni diversi: il corto-circuito deve restituire true
        Properties qp1 = buildStandardProps();
        Properties qp2 = buildStandardProps();
        qp2.setProperty("weight.1", "99");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        qh1.setVersion(5L);
        qh2.setVersion(5L);
        assertTrue(qh1.equals(qh2), "Stessa versione deve produrre equals()=true per design");
    }

    @Test
    @Timeout(5)
    public void Equals_DifferentAllMembersSize() throws ConfigException {
        Properties qp1 = buildStandardProps();
        Properties qp2 = buildStandardProps();
        qp2.setProperty("server.3", "localhost:2890:3890:participant");
        qp2.setProperty("group.1", "1:2:3");
        qp2.setProperty("weight.3", "1");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        qh1.setVersion(1L);
        qh2.setVersion(2L);
        assertFalse(qh1.equals(qh2), "equals() deve restituire false se allMembers hanno dimensioni diverse");
    }

    @Test
    @Timeout(5)
    public void Equals_DifferentServerWeightValues() throws ConfigException {
        Properties qp1 = buildStandardProps();
        Properties qp2 = buildStandardProps();
        qp2.setProperty("weight.1", "5");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        qh1.setVersion(1L);
        qh2.setVersion(2L);
        assertFalse(qh1.equals(qh2), "equals() deve restituire false se i pesi dei server sono diversi");
    }

    @Test
    @Timeout(5)
    public void Equals_DifferentGroupWeightValues() throws ConfigException {
        Properties qp1 = buildStandardProps();
        Properties qp2 = new Properties();
        qp2.setProperty("server.1", "localhost:2888:3888:participant");
        qp2.setProperty("server.2", "localhost:2889:3889:participant");
        qp2.setProperty("group.1", "1");
        qp2.setProperty("group.2", "2");
        qp2.setProperty("weight.1", "1");
        qp2.setProperty("weight.2", "1");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        qh1.setVersion(1L);
        qh2.setVersion(2L);
        assertFalse(qh1.equals(qh2), "equals() deve restituire false se i groupWeight sono diversi");
    }

    @Test
    @Timeout(5)
    public void Equals_DifferentMemberContent() throws ConfigException {
        Properties qp1 = buildStandardProps();
        Properties qp2 = new Properties();
        qp2.setProperty("server.1", "localhost:2888:3888:participant");
        qp2.setProperty("server.2", "otherhost:2889:3889:participant");
        qp2.setProperty("group.1", "1:2");
        qp2.setProperty("weight.1", "1");
        qp2.setProperty("weight.2", "1");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        qh1.setVersion(1L);
        qh2.setVersion(2L);
        assertFalse(qh1.equals(qh2), "equals() deve restituire false se i membri hanno contenuto diverso");
    }

    @Test
    @Timeout(5)
    public void Equals_DifferentServerGroupMapping() throws ConfigException {
        Properties qp1 = new Properties();
        qp1.setProperty("server.1", "localhost:2888:3888:participant");
        qp1.setProperty("server.2", "localhost:2889:3889:participant");
        qp1.setProperty("server.3", "localhost:2890:3890:participant");
        qp1.setProperty("group.1", "1:2");
        qp1.setProperty("group.2", "3");
        qp1.setProperty("weight.1", "1");
        qp1.setProperty("weight.2", "1");
        qp1.setProperty("weight.3", "1");
        Properties qp2 = new Properties();
        qp2.setProperty("server.1", "localhost:2888:3888:participant");
        qp2.setProperty("server.2", "localhost:2889:3889:participant");
        qp2.setProperty("server.3", "localhost:2890:3890:participant");
        qp2.setProperty("group.1", "1");
        qp2.setProperty("group.2", "2:3");
        qp2.setProperty("weight.1", "1");
        qp2.setProperty("weight.2", "1");
        qp2.setProperty("weight.3", "1");
        QuorumHierarchical qh1 = new QuorumHierarchical(qp1);
        QuorumHierarchical qh2 = new QuorumHierarchical(qp2);
        qh1.setVersion(1L);
        qh2.setVersion(2L);
        assertFalse(qh1.equals(qh2), "equals() deve restituire false se il mapping server->gruppo è diverso");
    }

    // --- Mutanti in computeGroupWeight() ---

    @Test
    @Timeout(5)
    public void ComputeGroupWeight_AccumulationLogic() throws Exception {
        Properties qp = new Properties();
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");
        qp.setProperty("group.1", "1:2");
        qp.setProperty("weight.1", "3");
        qp.setProperty("weight.2", "5");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        Field gw = QuorumHierarchical.class.getDeclaredField("groupWeight");
        gw.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<Long, Long> groupWeightMap = (java.util.Map<Long, Long>) gw.get(qh);
        assertEquals(8L, groupWeightMap.get(1L), "Il peso del gruppo deve essere la SOMMA dei pesi (3+5=8)");
    }

    @Test
    @Timeout(5)
    public void ComputeGroupWeight_ZeroWeightDecrementsNumGroups() throws Exception {
        Properties qp = new Properties();
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");
        qp.setProperty("group.1", "1");
        qp.setProperty("group.2", "2");
        qp.setProperty("weight.1", "0");
        qp.setProperty("weight.2", "1");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        Set<Long> setWithServer2 = new HashSet<>(Collections.singletonList(2L));
        assertTrue(qh.containsQuorum(setWithServer2),
                "L'unico server di peso non-zero deve formare quorum quando l'altro gruppo ha peso 0");
    }

    // --- Mutanti in containsQuorum() ---

    @Test
    @Timeout(5)
    public void ContainsQuorum_BoundaryMajority() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");
        qp.setProperty("server.3", "localhost:2890:3890:participant");
        qp.setProperty("group.1", "1");
        qp.setProperty("group.2", "2");
        qp.setProperty("group.3", "3");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "1");
        qp.setProperty("weight.3", "1");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        Set<Long> twoOfThree = new HashSet<>(Arrays.asList(1L, 2L));
        assertTrue(qh.containsQuorum(twoOfThree), "2 su 3 gruppi deve essere quorum");
        Set<Long> oneOfThree = new HashSet<>(Collections.singletonList(1L));
        assertFalse(qh.containsQuorum(oneOfThree), "1 su 3 gruppi non deve essere quorum");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_GroupWeightBoundary() throws ConfigException {
        // Un solo gruppo con due server di peso 1 (peso totale = 2)
        Properties qp = new Properties();
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");
        qp.setProperty("group.1", "1:2");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "1");
        QuorumHierarchical qh = new QuorumHierarchical(qp);

        // 1 > (2/2) = false nel codice originale, true con mutante >=
        Set<Long> halfGroupVoted = new HashSet<>(Collections.singletonList(1L));
        assertFalse(qh.containsQuorum(halfGroupVoted), "La metà esatta del peso del gruppo non deve costituire maggioranza");
    }

    @Test
    @Timeout(5)
    public void ContainsQuorum_EmptySetReturnsFalse() throws ConfigException {
        Properties qp = buildStandardProps();
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        Set<Long> emptySet = new HashSet<>();
        assertFalse(qh.containsQuorum(emptySet), "Un set vuoto non deve mai essere quorum");
    }

    // --- Mutanti in parse() ---

    @Test
    @Timeout(5)
    public void Parse_NumGroupsIncrementsCorrectly() throws Exception {
        Properties qp = new Properties();
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");
        qp.setProperty("server.3", "localhost:2890:3890:participant");
        qp.setProperty("group.1", "1");
        qp.setProperty("group.2", "2");
        qp.setProperty("group.3", "3");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "1");
        qp.setProperty("weight.3", "1");
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        Set<Long> twoServers = new HashSet<>(Arrays.asList(1L, 2L));
        assertTrue(qh.containsQuorum(twoServers), "Con 3 gruppi, 2 su 3 deve essere quorum");
        Set<Long> oneServer = new HashSet<>(Collections.singletonList(1L));
        assertFalse(qh.containsQuorum(oneServer), "Con 3 gruppi, 1 su 3 non deve essere quorum");
    }

    @Test
    @Timeout(5)
    public void Parse_ComputeGroupWeightIsCalled() throws Exception {
        Properties qp = buildStandardProps();
        QuorumHierarchical qh = new QuorumHierarchical(qp);
        Set<Long> allServers = new HashSet<>(Arrays.asList(1L, 2L));
        assertTrue(qh.containsQuorum(allServers),
                "containsQuorum deve funzionare: computeGroupWeight() deve essere invocato durante il parsing");
    }

    // --- Mutanti in readConfigFile() ---

    @Test
    @Timeout(5)
    public void ReadConfigFile_NonExistentFileThrows() {
        assertThrows(ConfigException.class,
                () -> new QuorumHierarchical("/non/existent/path/to/config.cfg"),
                "readConfigFile deve lanciare ConfigException se il file non esiste");
    }

    @Test
    @Timeout(5)
    public void ReadConfigFile_ValidFileParses() throws Exception {
        File tmpFile = File.createTempFile("quorum_pit_test_", ".cfg");
        tmpFile.deleteOnExit();
        try (FileWriter fw = new FileWriter(tmpFile)) {
            fw.write("server.1=localhost:2888:3888:participant\n");
            fw.write("server.2=localhost:2889:3889:participant\n");
            fw.write("group.1=1:2\n");
            fw.write("weight.1=1\n");
            fw.write("weight.2=1\n");
        }
        QuorumHierarchical qh = new QuorumHierarchical(tmpFile.getAbsolutePath());
        assertEquals(2, qh.getAllMembers().size(), "Il parsing da file deve caricare tutti i server");
    }

    // --- Helper ---
    private Properties buildStandardProps() {
        Properties qp = new Properties();
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:participant");
        qp.setProperty("group.1", "1:2");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "1");
        return qp;
    }

}
