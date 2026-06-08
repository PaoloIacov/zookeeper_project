package org.apache.zookeeper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.apache.zookeeper.ClientCnxn;
import org.apache.zookeeper.ClientCnxnSocket;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class Constructor_ClientCnxnLLMFewShotTest {

    private HostProvider mockHostProvider;
    private ZKClientConfig mockClientConfig;
    private Watcher mockWatcher;
    private ClientCnxnSocket mockSocket;
    private byte[] dummySessionPasswd;

    @BeforeEach
    public void setUp() {
        // Setup dei mock allineati allo stile del progetto
        mockHostProvider = mock(HostProvider.class);
        // Stubbing di default per evitare ArithmeticException non volute nell'happy path
        when(mockHostProvider.size()).thenReturn(1);
        
        mockClientConfig = mock(ZKClientConfig.class);
        // Previene una NullPointerException fatale durante l'inizializzazione del ZKWatchManager interno
        when(mockClientConfig.getBoolean(ZKClientConfig.DISABLE_AUTO_WATCH_RESET)).thenReturn(false);
        
        mockWatcher = mock(Watcher.class);
        mockSocket = mock(ClientCnxnSocket.class);
        
        // Passwd standard di 16 byte per le sessioni ZK
        dummySessionPasswd = new byte[16];
    }

    @Test
    @Timeout(5)
    @DisplayName("Costruttore: parametri pienamente validi istanziano correttamente la classe")
    public void FullConstructor_ValidParameters_SuccessfullyInstantiated() {
        assertDoesNotThrow(() -> {
            new ClientCnxn(
                mockHostProvider,
                30000,
                mockClientConfig,
                mockWatcher,
                mockSocket,
                123456789L,
                dummySessionPasswd,
                true
            );
        }, "Il costruttore non deve lanciare eccezioni con parametri standard");
    }

    @Test
    @Timeout(5)
    @DisplayName("Costruttore: HostProvider nullo causa una NullPointerException")
    public void FullConstructor_NullHostProvider_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new ClientCnxn(
                null,
                30000,
                mockClientConfig,
                mockWatcher,
                mockSocket,
                123456789L,
                dummySessionPasswd,
                true
            );
        }, "L'assenza dell'HostProvider deve causare una NPE alla chiamata di .size()");
    }

    @Test
    @Timeout(5)
    @DisplayName("Costruttore: HostProvider.size() a 0 causa eccezione (Divisione per Zero)")
    public void FullConstructor_EmptyHostProvider_ThrowsArithmeticException() {
        // Sovrascriviamo lo stub per forzare il boundary zero
        when(mockHostProvider.size()).thenReturn(0);
        
        Exception exception = assertThrows(Exception.class, () -> {
            new ClientCnxn(
                mockHostProvider,
                30000,
                mockClientConfig,
                mockWatcher,
                mockSocket,
                123456789L,
                dummySessionPasswd,
                true
            );
        });
        
        // In base alla specifica versione e refactoring di ZK, potrebbe lanciare 
        // IllegalArgumentException (se validato prima) o ArithmeticException (se sfugge).
        assertTrue(exception instanceof ArithmeticException || exception instanceof IllegalArgumentException,
                "Deve fallire matematicamente o per pre-condizione violata sul calcolo del connectTimeout");
    }

    @Test
    @Timeout(5)
    @DisplayName("Costruttore: ZKClientConfig nullo causa NullPointerException")
    public void FullConstructor_NullClientConfig_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new ClientCnxn(
                mockHostProvider,
                30000,
                null,
                mockWatcher,
                mockSocket,
                123456789L,
                dummySessionPasswd,
                true
            );
        }, "L'assenza di config deve fallire durante il lookup di ZKClientConfig.DISABLE_AUTO_WATCH_RESET");
    }

    @Test
    @Timeout(5)
    @DisplayName("Costruttore: Boundary Value - sessionTimeout a zero è strutturalmente accettato")
    public void FullConstructor_ZeroSessionTimeout_AcceptedWithoutException() {
        assertDoesNotThrow(() -> {
            new ClientCnxn(
                mockHostProvider,
                0, // Boundary value timeout nullo
                mockClientConfig,
                mockWatcher,
                mockSocket,
                123456789L,
                dummySessionPasswd,
                true
            );
        }, "Un timeout di 0 deve essere processato, settando i read/connect timeout a 0 senza crashare");
    }
}
