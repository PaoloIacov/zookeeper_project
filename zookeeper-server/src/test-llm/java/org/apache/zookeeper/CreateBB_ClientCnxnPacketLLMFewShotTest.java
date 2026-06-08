package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.apache.jute.OutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.ConnectRequest;
import org.apache.zookeeper.proto.RequestHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class CreateBB_ClientCnxnPacketLLMFewShotTest {

    /**
     * Helper per costruire un oggetto Packet. 
     * Il costruttore accetta (RequestHeader, ReplyHeader, request, response, WatchRegistration).
     */
    private ClientCnxn.Packet buildPacket(RequestHeader header, Record request) {
        return new ClientCnxn.Packet(header, null, request, null, null);
    }

    @Test
    @Timeout(5)
    public void CreateBB_NullHeaderAndRequest_BufferContainsOnlyLength() {
        ClientCnxn.Packet packet = buildPacket(null, null);
        
        packet.createBB();
        
        assertNotNull(packet.bb, "Il ByteBuffer non deve essere nullo");
        assertEquals(4, packet.bb.capacity(), "La capacità del buffer deve essere pari solo all'int pre-allocato");
        assertEquals(0, packet.bb.getInt(), "La lunghezza registrata nel payload netto deve essere 0");
    }

    @Test
    @Timeout(5)
    public void CreateBB_WithHeaderOnly_SerializesHeaderSuccessfully() throws IOException {
        RequestHeader headerMock = mock(RequestHeader.class);
        ClientCnxn.Packet packet = buildPacket(headerMock, null);
        
        packet.createBB();
        
        assertNotNull(packet.bb);
        verify(headerMock, times(1)).serialize(any(OutputArchive.class), eq("header"));
    }

    @Test
    @Timeout(5)
    public void CreateBB_WithConnectRequest_SerializesRequestAndValidatesCapacity() throws IOException {
        ConnectRequest connectReqMock = mock(ConnectRequest.class);
        ClientCnxn.Packet packet = buildPacket(null, connectReqMock);
        
        // Configuriamo il mock per scrivere effettivamente dei byte fittizi per simulare il payload
        doAnswer(invocation -> {
            OutputArchive archive = invocation.getArgument(0);
            archive.writeInt(12345, "mockField");
            return null;
        }).when(connectReqMock).serialize(any(OutputArchive.class), eq("connect"));

        packet.createBB();
        
        assertNotNull(packet.bb);
        verify(connectReqMock, times(1)).serialize(any(OutputArchive.class), eq("connect"));
        assertTrue(packet.bb.capacity() > 4, "Il buffer deve contenere l'int della lunghezza e i byte del payload, quindi > 4");
    }

    @Test
    @Timeout(5)
    public void CreateBB_WithRegularRequest_SerializesHeaderAndRequest() throws IOException {
        RequestHeader headerMock = mock(RequestHeader.class);
        Record regularReqMock = mock(Record.class); 
        
        ClientCnxn.Packet packet = buildPacket(headerMock, regularReqMock);
        
        packet.createBB();
        
        assertNotNull(packet.bb);
        verify(headerMock, times(1)).serialize(any(OutputArchive.class), eq("header"));
        verify(regularReqMock, times(1)).serialize(any(OutputArchive.class), eq("request"));
    }

    @Test
    @Timeout(5)
    public void CreateBB_IOExceptionDuringSerialization_ExceptionCaughtAndBufferIsNull() throws IOException {
        RequestHeader headerMock = mock(RequestHeader.class);
        
        doThrow(new IOException("Simulazione errore I/O")).when(headerMock)
                .serialize(any(OutputArchive.class), anyString());
        
        ClientCnxn.Packet packet = buildPacket(headerMock, null);
        
        assertDoesNotThrow(() -> packet.createBB(), "Il metodo deve gestire internamente le IOException");
        assertNull(packet.bb, "Il ByteBuffer bb deve rimanere null a causa dell'interruzione nel try-catch");
    }
}
