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

/**
 * Test suite per ClientCnxn.
 * Metodologia Category Partition + Coverage Enhancement + Mutation Testing.
 */
public class ClientCnxnTest {

    // --- Helper ---

    private ClientCnxn buildCnxn(int sessionTimeout, int hostProviderSize) throws IOException {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(hostProviderSize);
        ClientCnxnSocket socket = mock(ClientCnxnSocket.class);
        return new ClientCnxn(hp, sessionTimeout, new ZKClientConfig(), mock(Watcher.class), socket, false);
    }

    @SuppressWarnings("unchecked")
    private LinkedBlockingDeque<ClientCnxn.Packet> getOutgoingQueue(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("outgoingQueue");
        f.setAccessible(true);
        return (LinkedBlockingDeque<ClientCnxn.Packet>) f.get(cnxn);
    }

    private boolean getClosing(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("closing");
        f.setAccessible(true);
        return f.getBoolean(cnxn);
    }

    // ========================================================================
    // T1.x – Costruttore ClientCnxn (Base Choice Coverage)
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

    @Test
    @Timeout(5)
    public void Constructor_BaseChoice_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_NullHostProvider_ThrowsNPE() {
        assertThrows(NullPointerException.class, () ->
            buildBaseChoice(null, 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_HostProviderSizeZero_ThrowsArithmeticException() {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(0);
        assertThrows(ArithmeticException.class, () ->
            buildBaseChoice(hp, 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_TimeoutZero_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 0, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_TimeoutNegative_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), -1, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_NullConfig_ThrowsNPE() {
        assertThrows(NullPointerException.class, () ->
            buildBaseChoice(validHp(), 30000, null, validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_NullWatcher_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), null, validSocket(), 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_NullSocket_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), null, 0L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_PositiveSessionId_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 12345L, validPwd(), false));
    }

    @Test
    @Timeout(5)
    public void Constructor_NullSessionPasswd_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 0L, null, false));
    }

    @Test
    @Timeout(5)
    public void Constructor_ReadOnlyTrue_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 30000, validConf(), validWatcher(), validSocket(), 0L, validPwd(), true));
    }

    @Test
    @Timeout(5)
    public void Constructor_TimeoutOne_ObjectCreated() {
        assertDoesNotThrow(() -> buildBaseChoice(validHp(), 1, validConf(), validWatcher(), validSocket(), 0L, validPwd(), false));
    }

    // ========================================================================
    // T2.x – isInEventThread()
    // ========================================================================

    @Test
    @Timeout(5)
    public void IsInEventThread_FromTestThread_ReturnsFalse() {
        assertFalse(ClientCnxn.isInEventThread());
    }

    @Test
    @Timeout(5)
    public void IsInEventThread_FromEventThread_ReturnsTrue() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        java.util.concurrent.atomic.AtomicBoolean result = new java.util.concurrent.atomic.AtomicBoolean(false);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);

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
    // T3.x – getXid()
    // ========================================================================

    @Test
    @Timeout(5)
    public void GetXid_NominalIncrement_ReturnsCurrentAndIncrements() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.xid = 1;
        assertEquals(1, cnxn.getXid());
        assertEquals(2, cnxn.xid);
    }

    @Test
    @Timeout(5)
    public void GetXid_ConsecutiveCalls_AreSequential() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.xid = 5;
        assertEquals(5, cnxn.getXid());
        assertEquals(6, cnxn.getXid());
        assertEquals(7, cnxn.getXid());
    }

    @Test
    @Timeout(5)
    public void GetXid_AtMaxValue_WrapsToOne() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.xid = Integer.MAX_VALUE;
        assertEquals(1, cnxn.getXid());
    }

    // ========================================================================
    // T4.x – addAuthInfo(String, byte[])
    // ========================================================================

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

    @Test
    @Timeout(5)
    public void AddAuthInfo_NullAuth_AcceptedWithoutException() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertDoesNotThrow(() -> cnxn.addAuthInfo("digest", null));
    }

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

    @Test
    @Timeout(5)
    public void AddAuthInfo_EmptyScheme_AcceptedWithoutException() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertDoesNotThrow(() -> cnxn.addAuthInfo("", new byte[]{0x01}));
        Field f = ClientCnxn.class.getDeclaredField("authInfo");
        f.setAccessible(true);
        CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) f.get(cnxn);
        assertEquals(1, authInfo.size());
    }

    @Test
    @Timeout(5)
    public void AddAuthInfo_EmptySchemeNullAuth_AcceptedWithoutException() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertDoesNotThrow(() -> cnxn.addAuthInfo("", null));
        Field f = ClientCnxn.class.getDeclaredField("authInfo");
        f.setAccessible(true);
        CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) f.get(cnxn);
        assertEquals(1, authInfo.size());
    }

    // ========================================================================
    // T5.x – Classe interna Packet
    // ========================================================================

    @Test
    @Timeout(5)
    public void Packet_CreateBB_NullHeaderNullRequest_BufferCreated() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.createBB();
        assertNotNull(p.bb, "Il ByteBuffer deve essere creato anche senza header e request");
    }

    @Test
    @Timeout(5)
    public void Packet_CreateBB_NullHeaderConnectRequest_BufferCreated() {
        ConnectRequest cr = new ConnectRequest(0, 0, 30000, 0, new byte[16], false);
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), cr, null, null);
        p.createBB();
        assertNotNull(p.bb);
        assertTrue(p.bb.capacity() > 4, "Il buffer deve contenere i dati della ConnectRequest");
    }

    @Test
    @Timeout(5)
    @Disabled("Bug: il metodo accetta un Record senza header creando un pacchetto corrotto")
    public void Packet_CreateBB_NullHeaderOtherRecord_ShouldThrowException() {
        GetDataRequest gdr = new GetDataRequest("/test", false);
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), gdr, null, null);
        assertThrows(IllegalStateException.class, () -> p.createBB(),
            "Dovrebbe lanciare eccezione per mancanza di RequestHeader");
    }

    @Test
    @Timeout(5)
    public void Packet_CreateBB_HeaderOnlyNullRequest_BufferCreated() {
        RequestHeader rh = new RequestHeader(1, OpCode.getData);
        ClientCnxn.Packet p = new ClientCnxn.Packet(rh, new ReplyHeader(), null, null, null);
        p.createBB();
        assertNotNull(p.bb);
        assertTrue(p.bb.capacity() > 4, "Il buffer deve contenere i dati dell'header");
    }

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

    @Test
    @Timeout(5)
    public void Packet_ToString_ContainsFinishedFlag() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.finished = false;
        assertTrue(p.toString().contains("finished:false"));
    }

    @Test
    @Timeout(5)
    public void Packet_ToString_StripsNewlines() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.clientPath = "/a\nb";
        String result = p.toString();
        assertFalse(result.contains("\n"), "I newline devono essere rimossi da toString()");
    }

    // ========================================================================
    // T6.x – queuePacket(...)
    // ========================================================================

    @Test
    @Timeout(5)
    public void QueuePacket_AliveNormalPacket_AddedToQueue() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        RequestHeader h = new RequestHeader(1, OpCode.getData);
        ReplyHeader r = new ReplyHeader();

        ClientCnxn.Packet result = cnxn.queuePacket(h, r, null, null, null, "/test", "/test", null, null);

        assertNotNull(result, "Il pacchetto restituito non deve essere null");
        LinkedBlockingDeque<ClientCnxn.Packet> queue = getOutgoingQueue(cnxn);
        assertEquals(1, queue.size(), "Il pacchetto deve essere nella outgoingQueue");
    }

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
        assertEquals(KeeperException.Code.SESSIONEXPIRED.intValue(), r.getErr(),
                "Il codice d'errore deve essere SESSIONEXPIRED");
    }

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

    @Test
    @Timeout(5)
    public void QueuePacket_AlreadyClosing_PacketRejected() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
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

    @Test
    @Timeout(5)
    public void QueuePacket_NullReplyHeader_StateClosed_NoCrash() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        cnxn.state = States.CLOSED;
        RequestHeader h = new RequestHeader(1, OpCode.getData);

        assertDoesNotThrow(() ->
                cnxn.queuePacket(h, null, null, null, null, "/test", "/test", null, null));
    }

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

    // ========================================================================
    // COVERAGE ENHANCEMENT (Fase 4.a)
    // Test sui metodi di lifecycle (start, disconnect, close).
    // I pure getters non sono testati per evitare Coverage Chasing.
    // ========================================================================

    @Test
    @Timeout(5)
    public void CE_LifecycleMethods() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);

        cnxn.start();
        cnxn.disconnect();
        cnxn.close();

        assertEquals(States.CLOSED, cnxn.getState(), "Dopo la close() lo stato deve essere CLOSED");
    }

    // ========================================================================
    // MUTATION TESTING (Fase 4.b)
    // Test specifici per uccidere mutanti sopravvissuti identificati da PIT.
    // ========================================================================

    @Test
    @Timeout(5)
    public void Constructor_ReadTimeoutCalculation() throws Exception {
        int sessionTimeout = 30000;
        ClientCnxn cnxn = buildCnxn(sessionTimeout, 1);
        Field f = ClientCnxn.class.getDeclaredField("readTimeout");
        f.setAccessible(true);
        int readTimeout = f.getInt(cnxn);
        assertEquals(20000, readTimeout, "readTimeout deve essere sessionTimeout * 2 / 3");
    }

    @Test
    @Timeout(5)
    public void Constructor_ExpirationTimeoutCalculation() throws Exception {
        int sessionTimeout = 30000;
        ClientCnxn cnxn = buildCnxn(sessionTimeout, 1);
        Field f = ClientCnxn.class.getDeclaredField("expirationTimeout");
        f.setAccessible(true);
        int expirationTimeout = f.getInt(cnxn);
        assertEquals(40000, expirationTimeout, "expirationTimeout deve essere sessionTimeout * 4 / 3");
    }

    @Test
    @Timeout(5)
    public void Constructor_InitRequestTimeoutCalled() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        Field f = ClientCnxn.class.getDeclaredField("requestTimeout");
        f.setAccessible(true);
        long requestTimeout = f.getLong(cnxn);
        assertTrue(requestTimeout >= 0, "requestTimeout deve essere inizializzato da initRequestTimeout()");
    }

    @Test
    @Timeout(5)
    public void GetSessionId_ReturnsStoredValue() throws Exception {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        Field f = ClientCnxn.class.getDeclaredField("sessionId");
        f.setAccessible(true);
        f.setLong(cnxn, 123456789L);
        assertEquals(123456789L, cnxn.getSessionId(), "getSessionId deve restituire il valore impostato, non 0");
    }

    @Test
    @Timeout(5)
    public void MakeThreadName_ReturnsNonEmpty() throws Exception {
        java.lang.reflect.Method m = ClientCnxn.class.getDeclaredMethod("makeThreadName", String.class);
        m.setAccessible(true);
        String result = (String) m.invoke(null, "-TestSuffix");
        assertNotNull(result, "makeThreadName non deve restituire null");
        assertFalse(result.isEmpty(), "makeThreadName non deve restituire stringa vuota");
        assertTrue(result.endsWith("-TestSuffix"), "makeThreadName deve terminare con il suffisso fornito");
    }

}
