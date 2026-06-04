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

 */
public class ClientCnxnIT {

    // ========================================================================
    // Helper: costruzione dell'ambiente di integrazione
    // ========================================================================

    /**
     * Costruisce una ClientCnxn minimale con mock infrastrutturali.
     */
    private ClientCnxn buildCnxn() throws IOException {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(1);
        ClientCnxnSocket socket = mock(ClientCnxnSocket.class);
        return new ClientCnxn(hp, 30000, new ZKClientConfig(), mock(Watcher.class), socket, false);
    }

    /**
     * Accede alla pendingQueue privata di ClientCnxn tramite Reflection.
     */
    @SuppressWarnings("unchecked")
    private Queue<ClientCnxn.Packet> getPendingQueue(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("pendingQueue");
        f.setAccessible(true);
        return (Queue<ClientCnxn.Packet>) f.get(cnxn);
    }

    /**
     * Accede al SendThread interno di ClientCnxn.
     */
    private ClientCnxn.SendThread getSendThread(ClientCnxn cnxn) throws Exception {
        Field f = ClientCnxn.class.getDeclaredField("sendThread");
        f.setAccessible(true);
        return (ClientCnxn.SendThread) f.get(cnxn);
    }

    /**
     * Serializza un ReplyHeader in un ByteBuffer, simulando una risposta del server.
     */
    private ByteBuffer buildServerResponse(int xid, int err, long zxid) throws IOException {
        ReplyHeader rh = new ReplyHeader(xid, zxid, err);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
        rh.serialize(boa, "header");
        byte[] bytes = baos.toByteArray();
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return bb;
    }

    // ========================================================================
    // TEST SUITE IT — Integration Test: queuePacket ↔ SendThread.readResponse
    // ========================================================================

    /**
     * queuePacket() inserisce un pacchetto getData nella
     * outgoingQueue. Lo trasferiamo manualmente nella pendingQueue
     * (simulando il lavoro di SendThread.run()), poi invochiamo
     * readResponse() con una risposta del server avente lo stesso XID.
     *
     * Verifica: il ReplyHeader del Packet originale viene aggiornato
     * con i valori della risposta del server (XID, err=0, zxid).
     */
    @Test
    @Timeout(5)
    public void IT01_HappyPath_QueueAndReadResponse_ReplyHeaderUpdated() throws Exception {
        ClientCnxn cnxn = buildCnxn();
        ClientCnxn.SendThread sendThread = getSendThread(cnxn);
        Queue<ClientCnxn.Packet> pendingQueue = getPendingQueue(cnxn);

        // 1. PRODUTTORE: accoda un pacchetto getData tramite queuePacket
        RequestHeader reqHeader = new RequestHeader(42, OpCode.getData);
        ReplyHeader replyHeader = new ReplyHeader();
        ClientCnxn.Packet packet = cnxn.queuePacket(reqHeader, replyHeader,
                null, null, null, "/zk/data", "/zk/data", null, null);

        // 2. Simula il trasferimento outgoingQueue → pendingQueue
        //    (normalmente eseguito dal loop di SendThread.run() dopo l'invio)
        pendingQueue.add(packet);

        // 3. CONSUMATORE: readResponse riceve la risposta del server
        long expectedZxid = 100L;
        ByteBuffer serverResponse = buildServerResponse(42, 0, expectedZxid);
        sendThread.readResponse(serverResponse);

        // 4. VERIFICA dell'interazione: il ReplyHeader del pacchetto originale
        //    è stato aggiornato dalla readResponse
        assertEquals(42, replyHeader.getXid(),
                "L'XID della risposta deve corrispondere a quello della richiesta");
        assertEquals(0, replyHeader.getErr(),
                "L'errore deve essere 0 (successo)");
        assertEquals(expectedZxid, replyHeader.getZxid(),
                "Lo ZXID deve essere propagato dalla risposta del server");

        // 5. Il pacchetto deve essere stato finalizzato (rimosso dalla pendingQueue)
        assertTrue(pendingQueue.isEmpty(),
                "La pendingQueue deve essere vuota dopo il consumo del pacchetto");
    }

    /**
     * readResponse() riceve una risposta con XID = PING_XID (-2).
     * Questo tipo di risposta è gestito direttamente dal SendThread, non richiede
     * un pacchetto nella pendingQueue.
     *
     * Verifica: la pendingQueue rimane invariata (nessun pacchetto estratto).
     */
    @Test
    @Timeout(5)
    public void IT02_PingResponse_PendingQueueUntouched() throws Exception {
        ClientCnxn cnxn = buildCnxn();
        ClientCnxn.SendThread sendThread = getSendThread(cnxn);
        Queue<ClientCnxn.Packet> pendingQueue = getPendingQueue(cnxn);

        // La pendingQueue è vuota (nessun pacchetto in attesa)
        assertTrue(pendingQueue.isEmpty(), "Precondizione: pendingQueue vuota");

        // readResponse riceve un PING (XID = -2)
        ByteBuffer pingResponse = buildServerResponse(-2, 0, 0L);

        // Non deve lanciare eccezioni e non deve toccare la pendingQueue
        assertDoesNotThrow(() -> sendThread.readResponse(pingResponse));
        assertTrue(pendingQueue.isEmpty(),
                "La pendingQueue deve rimanere vuota dopo una risposta ping");
    }

    /**
     * Un pacchetto con XID=42 è nella pendingQueue, ma la risposta ha XID=99. 
     * Questo rappresenta una violazione del protocollo di ordinamento.
     *
     * Verifica: readResponse() lancia IOException e setta replyHeader.err = CONNECTIONLOSS.
     */
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

        // Il server risponde con XID=99 (mismatch!)
        ByteBuffer wrongResponse = buildServerResponse(99, 0, 50L);

        // readResponse deve lanciare IOException per XID out of order
        assertThrows(IOException.class, () -> sendThread.readResponse(wrongResponse),
                "XID mismatch deve causare IOException");

        // Il replyHeader deve essere settato con CONNECTIONLOSS
        assertEquals(KeeperException.Code.CONNECTIONLOSS.intValue(), replyHeader.getErr(),
                "Il codice errore deve essere CONNECTIONLOSS dopo XID mismatch");
    }

    /**
     * Il server invia una risposta con un XID "normale" (non ping, non notifica), 
     * ma la pendingQueue è vuota. Rappresenta uno stato inconsistente del protocollo.
     *
     * Verifica: readResponse() lancia IOException con messaggio "Nothing in the queue".
     */
    @Test
    @Timeout(5)
    public void EmptyQueue_ThrowsIOException() throws Exception {
        ClientCnxn cnxn = buildCnxn();
        ClientCnxn.SendThread sendThread = getSendThread(cnxn);
        Queue<ClientCnxn.Packet> pendingQueue = getPendingQueue(cnxn);

        // Precondizione: pendingQueue vuota
        assertTrue(pendingQueue.isEmpty(), "Precondizione: pendingQueue vuota");

        // Il server invia una risposta con XID normale (non è un ping/notifica)
        ByteBuffer orphanResponse = buildServerResponse(42, 0, 100L);

        // readResponse deve lanciare IOException
        IOException ex = assertThrows(IOException.class,
                () -> sendThread.readResponse(orphanResponse),
                "Risposta senza pacchetto in coda deve causare IOException");

        assertTrue(ex.getMessage().contains("Nothing in the queue"),
                "Il messaggio deve indicare che la coda è vuota");
    }

}
