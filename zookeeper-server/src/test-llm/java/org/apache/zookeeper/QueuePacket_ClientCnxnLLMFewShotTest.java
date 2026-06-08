package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.jute.Record;
import org.apache.zookeeper.ZooDefs.OpCode;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.RequestHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class QueuePacket_ClientCnxnLLMFewShotTest {

    private ClientCnxnSocket socketMock;
    private ClientCnxn cnxn;

    @BeforeEach
    public void setUp() throws Exception {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(1);
        socketMock = mock(ClientCnxnSocket.class);
        
        // Istanziazione standard come da baseline di progetto
        cnxn = new ClientCnxn(hp, 30000, new ZKClientConfig(), mock(Watcher.class), socketMock, false);
    }

    /* ==========================================================
     * Utility Method per setup test tramite Reflection
     * ========================================================== */

    private void setCnxnState(ClientCnxn cnxn, ZooKeeper.States state) throws Exception {
        Field stateField = ClientCnxn.class.getDeclaredField("state");
        stateField.setAccessible(true);
        stateField.set(cnxn, state);
    }

    private void setClosing(ClientCnxn cnxn, boolean closing) throws Exception {
        Field closingField = ClientCnxn.class.getDeclaredField("closing");
        closingField.setAccessible(true);
        closingField.set(cnxn, closing);
    }
    
    private boolean isClosing(ClientCnxn cnxn) throws Exception {
        Field closingField = ClientCnxn.class.getDeclaredField("closing");
        closingField.setAccessible(true);
        return (boolean) closingField.get(cnxn);
    }

    private LinkedBlockingDeque<?> getOutgoingQueue(ClientCnxn cnxn) throws Exception {
        Field field = ClientCnxn.class.getDeclaredField("outgoingQueue");
        field.setAccessible(true);
        return (LinkedBlockingDeque<?>) field.get(cnxn);
    }

    /* ==========================================================
     * Test Cases (Partizionamento dello stato logico)
     * ========================================================== */

    @Test
    @Timeout(5)
    public void QueuePacket_StateNotAlive_HandledAsConnectionLoss() throws Exception {
        // Arrange: la sessione non è viva (CLOSED)
        setCnxnState(cnxn, ZooKeeper.States.CLOSED);
        
        RequestHeader h = new RequestHeader();
        h.setType(OpCode.create);
        ReplyHeader r = new ReplyHeader();
        
        // Act
        cnxn.queuePacket(
            h, r, mock(Record.class), mock(Record.class), 
            null, "/client", "/server", null, null, null
        );

        // Assert
        // Il pacchetto intercetta la loss logic (!state.isAlive()) ed eroga error code (SESSIONEXPIRED per lo stato CLOSED).
        assertEquals(KeeperException.Code.SESSIONEXPIRED.intValue(), r.getErr());
        // Non deve essere accodato
        assertTrue(getOutgoingQueue(cnxn).isEmpty(), "La outgoingQueue deve rimanere vuota.");
        // Nonostante lo scarto, l'implementazione legacy notifica sempre il socket alla fine del metodo
        verify(socketMock, times(1)).packetAdded();
    }

    @Test
    @Timeout(5)
    public void QueuePacket_AliveButClosing_HandledAsConnectionLoss() throws Exception {
        // Arrange: Sessione viva ma marcata per la chiusura anticipata
        setCnxnState(cnxn, ZooKeeper.States.CONNECTED);
        setClosing(cnxn, true);
        
        RequestHeader h = new RequestHeader();
        h.setType(OpCode.getData);
        ReplyHeader r = new ReplyHeader();
        
        // Act
        cnxn.queuePacket(
            h, r, mock(Record.class), mock(Record.class), 
            null, "/client", "/server", null, null, null
        );

        // Assert
        // Stessa intercettazione del test precedente, ma restituisce default fallback error = CONNECTIONLOSS
        assertEquals(KeeperException.Code.CONNECTIONLOSS.intValue(), r.getErr());
        assertTrue(getOutgoingQueue(cnxn).isEmpty(), "La outgoingQueue deve rimanere vuota.");
        // Verifica l'invocazione incondizionata al socket
        verify(socketMock, times(1)).packetAdded();
    }

    @Test
    @Timeout(5)
    public void QueuePacket_AliveNormalPacket_QueuedSuccessfully() throws Exception {
        // Arrange: Sessione viva e stabile. Operazione generica
        setCnxnState(cnxn, ZooKeeper.States.CONNECTED);
        setClosing(cnxn, false);
        
        RequestHeader h = new RequestHeader();
        h.setType(OpCode.getData);
        ReplyHeader r = new ReplyHeader();
        
        // Act
        cnxn.queuePacket(
            h, r, mock(Record.class), mock(Record.class), 
            null, "/client", "/server", null, null, null
        );

        // Assert
        LinkedBlockingDeque<?> outgoingQueue = getOutgoingQueue(cnxn);
        assertEquals(1, outgoingQueue.size(), "Il pacchetto deve essere regolarmente inserito in coda.");
        verify(socketMock, times(1)).packetAdded();
        assertFalse(isClosing(cnxn), "Il flag 'closing' non deve essere alterato.");
    }

    @Test
    @Timeout(5)
    public void QueuePacket_AliveCloseSessionPacket_QueuedAndClosingTriggered() throws Exception {
        // Arrange: Sessione viva e stabile. Viene chiesto di chiudere esplicitamente il Client
        setCnxnState(cnxn, ZooKeeper.States.CONNECTED);
        setClosing(cnxn, false);
        
        RequestHeader h = new RequestHeader();
        h.setType(OpCode.closeSession);
        ReplyHeader r = new ReplyHeader();
        
        // Act
        cnxn.queuePacket(
            h, r, mock(Record.class), mock(Record.class), 
            null, "/client", "/server", null, null, null
        );

        // Assert
        LinkedBlockingDeque<?> outgoingQueue = getOutgoingQueue(cnxn);
        assertEquals(1, outgoingQueue.size(), "Il pacchetto (di chiusura) deve essere inserito in coda.");
        verify(socketMock, times(1)).packetAdded();
        // L'intercettazione dell'OpCode.closeSession deve aver modificato lo stato volatile 'closing'
        assertTrue(isClosing(cnxn), "Il flag 'closing' DEVE essere true dopo una closeSession request.");
    }
}
