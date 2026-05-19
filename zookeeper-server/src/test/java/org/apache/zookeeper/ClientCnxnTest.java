package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.proto.ReplyHeader;
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

    // ========================================================================
    // TEST SUITE T1.x – Costruttore ClientCnxn(...)
    // ========================================================================

    // T1.1 - CE1×CE4×CE7: parametri nominali → stato CONNECTING
    @Test
    @Timeout(5)
    public void Constructor_ValidParams_StateIsConnecting() throws IOException {
        ClientCnxn cnxn = buildCnxn(30000, 1);
        assertEquals(States.CONNECTING, cnxn.state);
    }

    // T1.2 - BV3: hostProvider.size() = 0 → ArithmeticException (bug: divisione non protetta)
    @Test
    @Timeout(5)
    public void Constructor_HostProviderSizeZero_ThrowsArithmeticException() {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(0);
        assertThrows(ArithmeticException.class, () ->
                new ClientCnxn(hp, 30000, new ZKClientConfig(), mock(Watcher.class),
                        mock(ClientCnxnSocket.class), false));
    }

    // T1.3 - CE2×CE4: sessionTimeout = 0 → oggetto creato
    @Test
    @Timeout(5)
    public void Constructor_SessionTimeoutZero_ObjectCreated() throws IOException {
        ClientCnxn cnxn = buildCnxn(0, 1);
        assertNotNull(cnxn);
        assertEquals(States.CONNECTING, cnxn.state);
    }

    // T1.4 - CE3×CE4: sessionTimeout negativo → nessuna eccezione (bug: mancata validazione)
    @Test
    @Timeout(5)
    public void Constructor_NegativeSessionTimeout_NoExceptionThrown() {
        assertDoesNotThrow(() -> buildCnxn(-1, 1));
    }

    // T1.5 - CE1×CE4×CE6: sessionPasswd = null → accettato
    @Test
    @Timeout(5)
    public void Constructor_NullSessionPasswd_ObjectCreated() throws IOException {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(1);
        ClientCnxn cnxn = new ClientCnxn(hp, 30000, new ZKClientConfig(), mock(Watcher.class),
                mock(ClientCnxnSocket.class), 0L, null, false);
        assertNotNull(cnxn);
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

    // ========================================================================
    // TEST SUITE T5.x – Classe Interna Packet
    // ========================================================================

    // T5.1 - createBB con requestHeader e request null: buffer viene creato
    @Test
    @Timeout(5)
    public void Packet_CreateBB_WithNullHeaderAndRequest_BufferNotNull() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.createBB();
        assertNotNull(p.bb, "Il ByteBuffer deve essere creato anche senza request");
    }

    // T5.2 - toString: contiene clientPath e serverPath
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

    // T5.3 - toString: contiene il flag finished
    @Test
    @Timeout(5)
    public void Packet_ToString_ContainsFinishedFlag() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.finished = false;
        assertTrue(p.toString().contains("finished:false"));
    }

    // T5.4 - toString: rimuove i newline dal contenuto
    @Test
    @Timeout(5)
    public void Packet_ToString_StripsNewlines() {
        ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
        p.clientPath = "/a\nb";
        String result = p.toString();
        assertFalse(result.contains("\n"), "I newline devono essere rimossi da toString()");
    }

}
