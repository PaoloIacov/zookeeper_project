package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Queue;

import org.apache.jute.BinaryOutputArchive;
import org.apache.zookeeper.ZooDefs.OpCode;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.RequestHeader;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Test;

/**
 * Integration test per ClientCnxn.
 * Verifica l'interazione tra queuePacket() e SendThread.readResponse().
 */
public class ClientCnxnIT {

    // --- Helper ---

    private ClientCnxn buildCnxn() throws IOException {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(1);
        ClientCnxnSocket socket = mock(ClientCnxnSocket.class);

        ZKClientConfig config = new ZKClientConfig();
        config.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, "false");

        return new ClientCnxn(hp, 30000, config, mock(Watcher.class), socket, false);
    }

    @SuppressWarnings("unchecked")
    private Queue<ClientCnxn.Packet> getPendingQueue(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("pendingQueue");
        f.setAccessible(true);
        return (Queue<ClientCnxn.Packet>) f.get(cnxn);
    }

    private ClientCnxn.SendThread getSendThread(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("sendThread");
        f.setAccessible(true);
        return (ClientCnxn.SendThread) f.get(cnxn);
    }

    private ByteBuffer buildServerResponse(int xid, int err, long zxid) throws IOException {
        ReplyHeader rh = new ReplyHeader(xid, zxid, err);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
        rh.serialize(boa, "header");
        return ByteBuffer.wrap(baos.toByteArray());
    }

    // ========================================================================
    // Integration Test: queuePacket ↔ SendThread.readResponse
    // ========================================================================

    @Test
    @Timeout(5)
    public void HappyPath_QueueAndReadResponse_ReplyHeaderUpdated() throws Exception {
        ClientCnxn cnxn = buildCnxn();
        ClientCnxn.SendThread sendThread = getSendThread(cnxn);
        Queue<ClientCnxn.Packet> pendingQueue = getPendingQueue(cnxn);

        // Accoda un pacchetto getData
        RequestHeader reqHeader = new RequestHeader(42, OpCode.getData);
        ReplyHeader replyHeader = new ReplyHeader();
        ClientCnxn.Packet packet = cnxn.queuePacket(reqHeader, replyHeader,
                null, null, null, "/zk/data", "/zk/data", null, null);

        // Simula il trasferimento outgoingQueue → pendingQueue
        pendingQueue.add(packet);

        // readResponse riceve la risposta del server
        long expectedZxid = 100L;
        sendThread.readResponse(buildServerResponse(42, 0, expectedZxid));

        assertEquals(42, replyHeader.getXid(), "L'XID deve corrispondere");
        assertEquals(0, replyHeader.getErr(), "L'errore deve essere 0 (successo)");
        assertEquals(expectedZxid, replyHeader.getZxid(), "Lo ZXID deve essere propagato");
        assertTrue(pendingQueue.isEmpty(), "La pendingQueue deve essere vuota dopo il consumo");
    }

    @Test
    @Timeout(5)
    public void PingResponse_PendingQueueUntouched() throws Exception {
        ClientCnxn cnxn = buildCnxn();
        ClientCnxn.SendThread sendThread = getSendThread(cnxn);
        Queue<ClientCnxn.Packet> pendingQueue = getPendingQueue(cnxn);

        assertTrue(pendingQueue.isEmpty(), "Precondizione: pendingQueue vuota");

        // readResponse riceve un PING (XID = -2)
        assertDoesNotThrow(() -> sendThread.readResponse(buildServerResponse(-2, 0, 0L)));
        assertTrue(pendingQueue.isEmpty(),
                "La pendingQueue deve rimanere vuota dopo una risposta ping");
    }

    @Test
    @Timeout(5)
    public void XidMismatch_ThrowsIOException() throws Exception {
        ClientCnxn cnxn = buildCnxn();
        ClientCnxn.SendThread sendThread = getSendThread(cnxn);
        Queue<ClientCnxn.Packet> pendingQueue = getPendingQueue(cnxn);

        // Accoda un pacchetto con XID=42
        RequestHeader reqHeader = new RequestHeader(42, OpCode.getData);
        ReplyHeader replyHeader = new ReplyHeader();
        ClientCnxn.Packet packet = cnxn.queuePacket(reqHeader, replyHeader,
                null, null, null, "/zk/data", "/zk/data", null, null);
        pendingQueue.add(packet);

        // Il server risponde con XID=99 (mismatch)
        assertThrows(IOException.class, () -> sendThread.readResponse(buildServerResponse(99, 0, 50L)),
                "XID mismatch deve causare IOException");
        assertEquals(KeeperException.Code.CONNECTIONLOSS.intValue(), replyHeader.getErr(),
                "Il codice errore deve essere CONNECTIONLOSS dopo XID mismatch");
    }

    @Test
    @Timeout(5)
    public void EmptyQueue_ThrowsIOException() throws Exception {
        ClientCnxn cnxn = buildCnxn();
        ClientCnxn.SendThread sendThread = getSendThread(cnxn);
        Queue<ClientCnxn.Packet> pendingQueue = getPendingQueue(cnxn);

        assertTrue(pendingQueue.isEmpty(), "Precondizione: pendingQueue vuota");

        // Risposta con XID normale ma nessun pacchetto in coda
        IOException ex = assertThrows(IOException.class,
                () -> sendThread.readResponse(buildServerResponse(42, 0, 100L)),
                "Risposta senza pacchetto in coda deve causare IOException");

        assertTrue(ex.getMessage().contains("Nothing in the queue"),
                "Il messaggio deve indicare che la coda è vuota");
    }

}
