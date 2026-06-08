package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.zookeeper.ClientCnxn;
import org.apache.zookeeper.ClientCnxnSocket;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class IsInEventThread_ClientCnxnLLMFewShotTest {

    /**
     * Metodo helper ripreso in conformità con gli standard del progetto
     * per istanziare l'oggetto ClientCnxn isolandone le dipendenze.
     */
    private ClientCnxn buildCnxn(int sessionTimeout, int hostProviderSize) throws Exception {
        HostProvider hp = mock(HostProvider.class);
        when(hp.size()).thenReturn(hostProviderSize);
        ClientCnxnSocket socket = mock(ClientCnxnSocket.class);
        return new ClientCnxn(hp, sessionTimeout, new ZKClientConfig(), mock(Watcher.class), socket, false);
    }

    @Test
    @Timeout(5)
    public void IsInEventThread_CalledFromMainThread_ReturnsFalse() throws Exception {
        // Esecuzione effettuata dal thread principale (thread del runner JUnit).
        // Dato che non si tratta di un'istanza di EventThread, il metodo deve restituire false.
        assertFalse(ClientCnxn.isInEventThread(), 
                "Il metodo dovrebbe ritornare false quando invocato dal main thread di test");
    }

    @Test
    @Timeout(5)
    public void IsInEventThread_CalledFromEventThread_ReturnsTrue() throws Exception {
        // Arrange
        ClientCnxn cnxn = buildCnxn(30000, 1);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean isInEventThreadResult = new AtomicBoolean(false);
        
        // Avviamo la connessione, innescando così lo start() del SendThread e dell'EventThread
        cnxn.start();
        
        try {
            // Definiamo un callback che verrà accodato per l'esecuzione direttamente nell'EventThread
            AsyncCallback.VoidCallback asyncTask = (rc, path, ctx) -> {
                // Act all'interno del loop eventi
                isInEventThreadResult.set(ClientCnxn.isInEventThread());
                latch.countDown();
            };
            
            // Accodiamo il task nell'EventThread. queueCallback è package-private 
            // e inietta il processo nella coda LinkedBlockingQueue dei "waitingEvents".
            cnxn.queueCallback(asyncTask, 0, "/dummy-path", null);
            
            // Aspettiamo che l'EventThread prelevi il task dalla coda e lo esegua (timeout di sicurezza)
            boolean taskExecuted = latch.await(3, TimeUnit.SECONDS);
            
            // Assert
            assertTrue(taskExecuted, "Il callback non è stato elaborato in tempo dall'EventThread");
            assertTrue(isInEventThreadResult.get(), 
                    "Il metodo dovrebbe ritornare true quando l'invocazione avviene dentro il ciclo dell'EventThread");
                    
        } finally {
            // Teardown: ci assicuriamo che in ogni caso la connessione venga chiusa per bloccare i thread asincroni
            cnxn.close();
        }
    }
}
