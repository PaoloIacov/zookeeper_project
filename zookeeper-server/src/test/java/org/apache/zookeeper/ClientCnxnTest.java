package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.zookeeper.ZooDefs.OpCode;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.proto.ConnectRequest;
import org.apache.zookeeper.proto.GetDataRequest;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.RequestHeader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

public class ClientCnxnTest {

    // Helper: costruisce una ClientCnxn minimale con Mockito
    private ClientCnxn buildCnxn(int sessionTimeout, int hostProviderSize) throws IOException {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(hostProviderSize);
        ClientCnxnSocket socket = mock(ClientCnxnSocket.class);
        return new ClientCnxn(hp, sessionTimeout, new ZKClientConfig(), mock(Watcher.class), socket, false);
    }



    // Helper: accede al campo privato outgoingQueue via Reflection
    @SuppressWarnings("unchecked")
    private LinkedBlockingDeque<ClientCnxn.Packet> getOutgoingQueue(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("outgoingQueue");
        f.setAccessible(true);
        return (LinkedBlockingDeque<ClientCnxn.Packet>) f.get(cnxn);
    }

    // Helper: accede al campo privato closing via Reflection
    private boolean getClosing(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("closing");
        f.setAccessible(true);
        return f.getBoolean(cnxn);
    }

    // ========================================================================
    // TEST SUITE T1.x – Costruttore ClientCnxn(...) [Base Choice Coverage]
    // ========================================================================

    private ClientCnxn buildBaseChoice(
        HostProvider hp, int timeout, ZKClientConfig conf, 
        Watcher w, ClientCnxnSocket sock, long sid, byte[] pwd, boolean ro) throws IOException {
        return new ClientCnxn(hp, timeout, conf, w, sock, sid, pwd, ro);
    }

    private HostProvider validHp() { 
        HostProvider hp = mock(HostProvider.class); 
        when(hp.size()).thenReturn(1); 
        return hp; 
    }
    private ZKClientConfig validConf() { return new ZKClientConfig(); }
    private Watcher validWatcher() { return mock(Watcher.class); }
    private ClientCnxnSocket validSocket() { return mock(ClientCnxnSocket.class); }
    private byte[] validPwd() { return new byte[16]; }

    // T1.1 - Base Choice (Caso nominale)
    @Test
    @Timeout(5)
    public void Constructor_BaseChoice_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // T1.2 - Variazione D1: HostProvider nullo
    @Test
    @Timeout(5)
    public void Constructor_NullHostProvider_ThrowsNPE() {
        assertThrows(NullPointerException.class, () -> 
            buildBaseChoice(null, 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // T1.3 - Variazione D1: HostProvider size 0
    @Test
    @Timeout(5)
    public void Constructor_HostProviderSizeZero_ThrowsArithmeticException() {
        HostProvider hp = mock(HostProvider.class); 
        when(hp.size()).thenReturn(0);
        assertThrows(ArithmeticException.class, () -> 
            buildBaseChoice(hp, 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // T1.4 - Variazione D2: Timeout 0
    @Test
    @Timeout(5)
    public void Constructor_TimeoutZero_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 0, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // T1.5 - Variazione D2: Timeout Negativo
    @Test
    @Timeout(5)
    public void Constructor_TimeoutNegative_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), -1, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // T1.6 - Variazione D3: ZKClientConfig nullo
    @Test
    @Timeout(5)
    public void Constructor_NullConfig_ThrowsNPE() {
        assertThrows(NullPointerException.class, () -> 
            buildBaseChoice(validHp(), 30000, null, validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // T1.7 - Variazione D4: Watcher nullo
    @Test
    @Timeout(5)
    public void Constructor_NullWatcher_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), null, validSocket(), 0L, validPwd(), false));
    }

    // T1.8 - Variazione D5: Socket nullo
    @Test
    @Timeout(5)
    public void Constructor_NullSocket_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), null, 0L, validPwd(), false));
    }

    // T1.9 - Variazione D6: SessionId positivo
    @Test
    @Timeout(5)
    public void Constructor_PositiveSessionId_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 12345L, validPwd(), false));
    }

    // T1.10 - Variazione D7: SessionPasswd nullo
    @Test
    @Timeout(5)
    public void Constructor_NullSessionPasswd_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 0L, null, false));
    }

    // T1.11 - Variazione D8: canBeReadOnly true
    @Test
    @Timeout(5)
    public void Constructor_ReadOnlyTrue_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), true));
    }

    // T1.12 - Variazione D2 (Boundary Value): Timeout = 1
    @Test
    @Timeout(5)
    public void Constructor_TimeoutOne_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 1, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // ========================================================================
    // TEST SUITE T2.x – isInEventThread() (static)
    // ========================================================================

    // T2.1 - CE1: thread JUnit (non EventThread) → false
    @Test
    @Timeout(5)
    public void IsInEventThread_FromTestThread_ReturnsFalse() {
        assertFalse(ClientCnxn.isInEventThread());
    }

    // T2.2 - CE2: thread corrente è un EventThread → true
    @Test
    @Timeout(5)
    public void IsInEventThread_FromEventThread_ReturnsTrue() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        java.util.concurrent.atomic.AtomicBoolean result = new java.util.concurrent.atomic.AtomicBoolean(false);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

        // Sovrascriviamo run() per evitare il ciclo di vita reale dell'EventThread
        Thread eventThread = cnxn.new EventThread() {
            @Override
            public void run() {
                result.set(ClientCnxn.isInEventThread());
                latch.countDown();
            }
        };

        eventThread.start();
        latch.await();
        assertTrue(result.get(), "isInEventThread deve restituire true dall'EventThread");
    }

    // ========================================================================
    // TEST SUITE T3.x – getXid()
    // ========================================================================

    // T3.1 - CE1, BV1: xid = 1 → restituisce 1, poi diventa 2
    @Test
    @Timeout(5)
    public void GetXid_NominalIncrement_ReturnsCurrentAndIncrements() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.xid = 1;
        assertEquals(1, cnxn.getXid());
        assertEquals(2, cnxn.xid);
    }

    // T3.2 - CE1: tre chiamate consecutive sono sequenziali
    @Test
    @Timeout(5)
    public void GetXid_ConsecutiveCalls_AreSequential() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.xid = 5;
        assertEquals(5, cnxn.getXid());
        assertEquals(6, cnxn.getXid());
        assertEquals(7, cnxn.getXid());
    }

    // T3.3 - CE2, BV2: xid = MAX_VALUE → wrap-around a 1
    @Test
    @Timeout(5)
    public void GetXid_AtMaxValue_WrapsToOne() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.xid = Integer.MAX_VALUE;
        assertEquals(1, cnxn.getXid());
    }

    // ========================================================================
    // TEST SUITE T4.x – addAuthInfo(String scheme, byte[] auth)
    // ========================================================================

    // T4.1 - CE2: stato CLOSED → metodo è no-op (nessuna entry aggiunta)
    @Test
    @Timeout(5)
    public void AddAuthInfo_WhenStateClosed_IsNoOp() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.state = States.CLOSED;
        cnxn.addAuthInfo("digest", new byte[]{0x01});
        Field f = ClientCnxn.class.getDeclaredField("authInfo");
        f.setAccessible(true);
        CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) f.get(cnxn);
        assertEquals(0, authInfo.size(), "Nessuna entry deve essere aggiunta con stato CLOSED");
    }

    // T4.2 - CE1×CE3: scheme = null → accettato senza eccezione
    @Test
    @Timeout(5)
    public void AddAuthInfo_NullScheme_AcceptedWithoutException() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertDoesNotThrow(() -> cnxn.addAuthInfo(null, new byte[]{0x01}));
        Field f = ClientCnxn.class.getDeclaredField("authInfo");
        f.setAccessible(true);
        CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) f.get(cnxn);
        assertEquals(1, authInfo.size());
    }

    // T4.3 - CE1×CE5: auth = null → accettato senza eccezione
    @Test
    @Timeout(5)
    public void AddAuthInfo_NullAuth_AcceptedWithoutException() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertDoesNotThrow(() -> cnxn.addAuthInfo("digest", null));
    }

    // T4.4 - CE1×CE4×CE6: parametri validi → entry aggiunta
    @Test
    @Timeout(5)
    public void AddAuthInfo_ValidParams_EntryAddedToAuthInfo() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.addAuthInfo("digest", new byte[]{0x01, 0x02});
        Field f = ClientCnxn.class.getDeclaredField("authInfo");
        f.setAccessible(true);
        CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) f.get(cnxn);
        assertEquals(1, authInfo.size(), "L'entry di autenticazione deve essere presente");
    }

    // T4.5 - BVA: scheme = stringa vuota, auth popolato → accettato senza eccezione
    @Test
    @Timeout(5)
    public void AddAuthInfo_EmptyScheme_AcceptedWithoutException() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertDoesNotThrow(() -> cnxn.addAuthInfo("", new byte[]{0x01}));
        Field f = ClientCnxn.class.getDeclaredField("authInfo");
        f.setAccessible(true);
        CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) f.get(cnxn);
        assertEquals(1, authInfo.size(), "L'entry con scheme vuoto viene comunque aggiunta");
    }

    // T4.6 - BVA: scheme = stringa vuota, auth = null → accettato senza eccezione
    @Test
    @Timeout(5)
    public void AddAuthInfo_EmptySchemeNullAuth_AcceptedWithoutException() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertDoesNotThrow(() -> cnxn.addAuthInfo("", null));
        Field f = ClientCnxn.class.getDeclaredField("authInfo");
        f.setAccessible(true);
        CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) f.get(cnxn);
        assertEquals(1, authInfo.size(), "L'entry con scheme vuoto e auth null viene comunque aggiunta");
    }

    // ========================================================================
    // TEST SUITE T5.x – Classe Interna Packet
    // ========================================================================

    // T5.1 - createBB: header=null, request=null → buffer creato (solo prefisso lunghezza)
    @Test
    @Timeout(5)
    public void Packet_CreateBB_NullHeaderNullRequest_BufferCreated() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.createBB();
        assertNotNull(p.bb, "Il ByteBuffer deve essere creato anche senza header e request");
    }

    // T5.2 - createBB: header=null, request=ConnectRequest → buffer contiene solo il payload connect
    @Test
    @Timeout(5)
    public void Packet_CreateBB_NullHeaderConnectRequest_BufferCreated() {
        ConnectRequest cr = new ConnectRequest(0, 0, 30000, 0, new byte[16], false);
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), cr, null, null);
        p.createBB();
        assertNotNull(p.bb);
        assertTrue(p.bb.capacity() > 4, "Il buffer deve contenere i dati della ConnectRequest");
    }

    // T5.3 - createBB: header=null, request=altro Record → dovrebbe fallire per violazione protocollo
    @Test
    @Timeout(5)
    @Disabled("BUG (Mancata validazione): Il metodo accetta un Record senza header creando un pacchetto corrotto. Aspettavamo IllegalStateException.")
    public void Packet_CreateBB_NullHeaderOtherRecord_ShouldThrowException() {
        GetDataRequest gdr = new GetDataRequest("/test", false);
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), gdr, null, null);
        assertThrows(IllegalStateException.class, () -> p.createBB(), 
            "Dovrebbe lanciare eccezione per mancanza di RequestHeader associato a Record operativo");
    }

    // T5.4 - createBB: header=valorizzato, request=null → buffer contiene solo l'header
    @Test
    @Timeout(5)
    public void Packet_CreateBB_HeaderOnlyNullRequest_BufferCreated() {
        RequestHeader rh = new RequestHeader(1, OpCode.getData);
        ClientCnxn.Packet p = new ClientCnxn.Packet(rh, new ReplyHeader(), null, null, null);
        p.createBB();
        assertNotNull(p.bb);
        assertTrue(p.bb.capacity() > 4, "Il buffer deve contenere i dati dell'header");
    }

    // T5.5 - createBB: header=valorizzato, request=ConnectRequest → buffer contiene header + connect
    @Test
    @Timeout(5)
    public void Packet_CreateBB_HeaderAndConnectRequest_BufferCreated() {
        RequestHeader rh = new RequestHeader(1, OpCode.getData);
        ConnectRequest cr = new ConnectRequest(0, 0, 30000, 0, new byte[16], false);
        ClientCnxn.Packet p = new ClientCnxn.Packet(rh, new ReplyHeader(), cr, null, null);
        p.createBB();
        assertNotNull(p.bb);
        assertTrue(p.bb.capacity() > 4, "Il buffer deve contenere header + ConnectRequest");
    }

    // T5.6 - createBB: header=valorizzato, request=altro Record → caso nominale completo
    @Test
    @Timeout(5)
    public void Packet_CreateBB_HeaderAndOtherRecord_BufferCreated() {
        RequestHeader rh = new RequestHeader(1, OpCode.getData);
        GetDataRequest gdr = new GetDataRequest("/test", false);
        ClientCnxn.Packet p = new ClientCnxn.Packet(rh, new ReplyHeader(), gdr, null, null);
        p.createBB();
        assertNotNull(p.bb);
        assertTrue(p.bb.capacity() > 4, "Il buffer deve contenere header + GetDataRequest");
    }

    // T5.7 - toString: contiene clientPath e serverPath
    @Test
    @Timeout(5)
    public void Packet_ToString_ContainsClientAndServerPath() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.clientPath = "/utenti";
        p.serverPath = "/miaApp/utenti";
        String result = p.toString();
        assertTrue(result.contains("clientPath:/utenti"));
        assertTrue(result.contains("serverPath:/miaApp/utenti"));
    }

    // T5.8 - toString: contiene il flag finished
    @Test
    @Timeout(5)
    public void Packet_ToString_ContainsFinishedFlag() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.finished = false;
        assertTrue(p.toString().contains("finished:false"));
    }

    // T5.9 - toString: rimuove i newline dal contenuto
    @Test
    @Timeout(5)
    public void Packet_ToString_StripsNewlines() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.clientPath = "/a\nb";
        String result = p.toString();
        assertFalse(result.contains("\n"), "I newline devono essere rimossi da toString()");
    }

    // ========================================================================
    // TEST SUITE T6.x – queuePacket(...) — Cuore della gestione code
    // ========================================================================

    // T6.1 - CE1: stato alive, pacchetto normale → aggiunto alla outgoingQueue
    @Test
    @Timeout(5)
    public void QueuePacket_AliveNormalPacket_AddedToQueue() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        // Lo stato iniziale dopo il costruttore è NOT_CONNECTED, che è alive
        RequestHeader h = new RequestHeader(1, OpCode.getData);
        ReplyHeader r = new ReplyHeader();

        ClientCnxn.Packet result = cnxn.queuePacket(h, r, null, null, null, "/test", "/test", null, null);

        assertNotNull(result, "Il pacchetto restituito non deve essere null");
        LinkedBlockingDeque<ClientCnxn.Packet> queue = getOutgoingQueue(cnxn);
        assertEquals(1, queue.size(), "Il pacchetto deve essere nella outgoingQueue");
    }

    // T6.2 - CE2: stato CLOSED (not alive) → conLossPacket, NON aggiunto alla coda
    @Test
    @Timeout(5)
    public void QueuePacket_StateClosed_NotAddedToQueue() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.state = States.CLOSED;
        RequestHeader h = new RequestHeader(1, OpCode.getData);
        ReplyHeader r = new ReplyHeader();

        cnxn.queuePacket(h, r, null, null, null, "/test", "/test", null, null);

        LinkedBlockingDeque<ClientCnxn.Packet> queue = getOutgoingQueue(cnxn);
        assertEquals(0, queue.size(), "Con stato CLOSED, il pacchetto non va nella coda");
        // Il replyHeader deve essere settato con errore SESSIONEXPIRED
        assertEquals(KeeperException.Code.SESSIONEXPIRED.intValue(), r.getErr(),
                "Il codice d'errore deve essere SESSIONEXPIRED");
    }

    // T6.3 - CE3: stato AUTH_FAILED (not alive) → conLossPacket con errore AUTHFAILED
    @Test
    @Timeout(5)
    public void QueuePacket_StateAuthFailed_SetsAuthFailedError() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.state = States.AUTH_FAILED;
        RequestHeader h = new RequestHeader(1, OpCode.getData);
        ReplyHeader r = new ReplyHeader();

        cnxn.queuePacket(h, r, null, null, null, "/test", "/test", null, null);

        LinkedBlockingDeque<ClientCnxn.Packet> queue = getOutgoingQueue(cnxn);
        assertEquals(0, queue.size(), "Con stato AUTH_FAILED, il pacchetto non va nella coda");
        assertEquals(KeeperException.Code.AUTHFAILED.intValue(), r.getErr(),
                "Il codice d'errore deve essere AUTHFAILED");
    }

    // T6.4 - CE4: pacchetto closeSession → flag closing diventa true
    @Test
    @Timeout(5)
    public void QueuePacket_CloseSession_SetsClosingFlag() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        RequestHeader h = new RequestHeader(1, OpCode.closeSession);
        ReplyHeader r = new ReplyHeader();

        assertFalse(getClosing(cnxn), "Prima della chiamata, closing deve essere false");

        cnxn.queuePacket(h, r, null, null, null, null, null, null, null);

        assertTrue(getClosing(cnxn), "Dopo closeSession, closing deve essere true");
        LinkedBlockingDeque<ClientCnxn.Packet> queue = getOutgoingQueue(cnxn);
        assertEquals(1, queue.size(), "Il pacchetto closeSession deve essere nella coda");
    }

    // T6.5 - CE5: closing=true (già in chiusura) → conLossPacket, pacchetto rifiutato
    @Test
    @Timeout(5)
    public void QueuePacket_AlreadyClosing_PacketRejected() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        // Impostiamo closing a true via Reflection
        Field closingField = ClientCnxn.class.getDeclaredField("closing");
        closingField.setAccessible(true);
        closingField.setBoolean(cnxn, true);

        RequestHeader h = new RequestHeader(1, OpCode.getData);
        ReplyHeader r = new ReplyHeader();

        cnxn.queuePacket(h, r, null, null, null, "/test", "/test", null, null);

        LinkedBlockingDeque<ClientCnxn.Packet> queue = getOutgoingQueue(cnxn);
        assertEquals(0, queue.size(), "Con closing=true, il pacchetto deve essere rifiutato");
        assertEquals(KeeperException.Code.CONNECTIONLOSS.intValue(), r.getErr(),
                "Il codice d'errore deve essere CONNECTIONLOSS");
    }

    // T6.6 - CE6: pacchetto con replyHeader null in stato CLOSED → no crash (guardia interna)
    @Test
    @Timeout(5)
    public void QueuePacket_NullReplyHeader_StateClosed_NoCrash() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.state = States.CLOSED;
        RequestHeader h = new RequestHeader(1, OpCode.getData);

        // replyHeader = null: conLossPacket() deve gestirlo senza NPE
        assertDoesNotThrow(() ->
                cnxn.queuePacket(h, null, null, null, null, "/test", "/test", null, null));
    }

    // T6.7 - CE1 + verifica campi Packet: clientPath e serverPath devono essere preservati
    @Test
    @Timeout(5)
    public void QueuePacket_FieldsPreserved_InPacket() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        RequestHeader h = new RequestHeader(1, OpCode.getData);
        ReplyHeader r = new ReplyHeader();

        ClientCnxn.Packet pkt = cnxn.queuePacket(h, r, null, null, null,
                "/client/path", "/server/path", "myContext", null);

        assertEquals("/client/path", pkt.clientPath, "clientPath deve essere preservato");
        assertEquals("/server/path", pkt.serverPath, "serverPath deve essere preservato");
        assertEquals("myContext", pkt.ctx, "ctx deve essere preservato");
    }

}
