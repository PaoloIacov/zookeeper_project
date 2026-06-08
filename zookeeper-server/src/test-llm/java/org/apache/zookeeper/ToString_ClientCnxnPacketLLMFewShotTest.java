package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.jute.Record;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.RequestHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ToString_ClientCnxnPacketLLMFewShotTest {

    /**
     * Helper per costruire l'oggetto Packet. 
     * Il costruttore accetta: (RequestHeader, ReplyHeader, request, response, WatchRegistration).
     */
    private ClientCnxn.Packet buildPacket(RequestHeader header, ReplyHeader replyHeader, Record request, Record response) {
        return new ClientCnxn.Packet(header, replyHeader, request, response, null);
    }

    @Test
    @Timeout(5)
    public void ToString_NullFields_FormatsCorrectly() {
        ClientCnxn.Packet packet = buildPacket(null, null, null, null);
        
        String result = packet.toString();
        
        // Verifica la base della formattazione con campi a null e finished di default (false)
        String expected = "clientPath:null serverPath:null finished:false header:: null replyHeader:: null request:: null response:: null";
        assertEquals(expected, result, "La stringa formattata non corrisponde al formato atteso per i valori null/default");
    }

    @Test
    @Timeout(5)
    public void ToString_ValidFields_FormatsCorrectly() {
        RequestHeader headerMock = mock(RequestHeader.class);
        ReplyHeader replyHeaderMock = mock(ReplyHeader.class);
        Record requestMock = mock(Record.class);
        Record responseMock = mock(Record.class);

        when(headerMock.toString()).thenReturn("HeaderMock");
        when(replyHeaderMock.toString()).thenReturn("ReplyMock");
        when(requestMock.toString()).thenReturn("ReqMock");
        when(responseMock.toString()).thenReturn("ResMock");

        ClientCnxn.Packet packet = buildPacket(headerMock, replyHeaderMock, requestMock, responseMock);
        // Accesso diretto ai campi package-private
        packet.clientPath = "/client/path";
        packet.serverPath = "/server/path";
        packet.finished = true;

        String result = packet.toString();
        
        String expected = "clientPath:/client/path serverPath:/server/path finished:true header:: HeaderMock replyHeader:: ReplyMock request:: ReqMock response:: ResMock";
        assertEquals(expected, result, "La concatenazione dei campi valorizzati non è formattata correttamente");
    }

    @Test
    @Timeout(5)
    public void ToString_FieldsWithLineBreaks_ReplacesLineBreaksWithSpaces() {
        RequestHeader headerMock = mock(RequestHeader.class);
        Record requestMock = mock(Record.class);

        // Simuliamo interruzioni di riga standard (LF) e Windows (CRLF)
        when(headerMock.toString()).thenReturn("Header\nLine2");
        when(requestMock.toString()).thenReturn("Request\r\nLine2\nLine3");

        ClientCnxn.Packet packet = buildPacket(headerMock, null, requestMock, null);
        packet.clientPath = "/path\n1";
        packet.serverPath = "/path\r\n2";

        String result = packet.toString();

        assertFalse(result.contains("\n"), "Non devono esserci newline (LF) nel risultato finale");
        assertFalse(result.contains("\r"), "Non devono esserci ritorni a capo (CR) nel risultato finale");
        
        String expected = "clientPath:/path 1 serverPath:/path 2 finished:false header:: Header Line2 replyHeader:: null request:: Request Line2 Line3 response:: null";
        assertEquals(expected, result, "Le sequenze di interruzione di riga non sono state sostituite correttamente con spazi");
    }
}
