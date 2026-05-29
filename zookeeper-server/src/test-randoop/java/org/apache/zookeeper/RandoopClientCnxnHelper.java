package org.apache.zookeeper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.StaticHostProvider;
import org.apache.zookeeper.client.ZKClientConfig;

/**
 * Helper Factory per Randoop.
 * Fornisce istanze pre-configurate di ClientCnxn che Randoop
 * può utilizzare come "seed sequences" per esplorare i metodi della classe.
 *
 * Senza questa classe, Randoop non riesce a costruire ClientCnxn perché
 * il costruttore richiede interfacce (HostProvider) e classi astratte
 * (ClientCnxnSocket) che non possono essere istanziate direttamente.
 *
 * NOTA: Usiamo ClientCnxnSocketNIO reale ma NON chiamiamo start(),
 * così i thread SendThread/EventThread non vengono avviati e non ci
 * sono connessioni di rete reali.
 */
public class RandoopClientCnxnHelper {

    /**
     * Crea un ClientCnxn minimale con 1 host, timeout 30s, senza avviare i thread.
     * Randoop potrà invocare getXid(), addAuthInfo(), ecc. sull'istanza restituita.
     */
    public static ClientCnxn createDefault() throws IOException {
        HostProvider hp = new StaticHostProvider(
            Collections.singletonList(new InetSocketAddress("localhost", 2181)));
        ZKClientConfig config = new ZKClientConfig();
        ClientCnxnSocket socket = new ClientCnxnSocketNIO(config);
        return new ClientCnxn(hp, 30000, config, null, socket, false);
    }

    /**
     * Crea un ClientCnxn con un timeout specifico.
     */
    public static ClientCnxn createWithTimeout(int timeout) throws IOException {
        HostProvider hp = new StaticHostProvider(
            Collections.singletonList(new InetSocketAddress("localhost", 2181)));
        ZKClientConfig config = new ZKClientConfig();
        ClientCnxnSocket socket = new ClientCnxnSocketNIO(config);
        return new ClientCnxn(hp, timeout, config, null, socket, false);
    }

    /**
     * Crea un ClientCnxn con sessionId e password pre-esistenti.
     */
    public static ClientCnxn createWithSession(long sessionId, byte[] sessionPasswd) throws IOException {
        HostProvider hp = new StaticHostProvider(
            Collections.singletonList(new InetSocketAddress("localhost", 2181)));
        ZKClientConfig config = new ZKClientConfig();
        ClientCnxnSocket socket = new ClientCnxnSocketNIO(config);
        return new ClientCnxn(hp, 30000, config, null, socket, sessionId, sessionPasswd, false);
    }

    /**
     * Crea un ClientCnxn in modalità read-only.
     */
    public static ClientCnxn createReadOnly() throws IOException {
        HostProvider hp = new StaticHostProvider(
            Collections.singletonList(new InetSocketAddress("localhost", 2181)));
        ZKClientConfig config = new ZKClientConfig();
        ClientCnxnSocket socket = new ClientCnxnSocketNIO(config);
        return new ClientCnxn(hp, 30000, config, null, socket, true);
    }

    /**
     * Fornisce un HostProvider valido che Randoop può usare direttamente.
     */
    public static HostProvider createHostProvider() {
        return new StaticHostProvider(
            Collections.singletonList(new InetSocketAddress("localhost", 2181)));
    }

    /**
     * Fornisce un ClientCnxnSocket valido che Randoop può usare direttamente.
     */
    public static ClientCnxnSocket createSocket() throws IOException {
        return new ClientCnxnSocketNIO(new ZKClientConfig());
    }

    /**
     * Fornisce un Watcher dummy che Randoop può usare.
     */
    public static Watcher createWatcher() {
        return event -> { /* no-op watcher */ };
    }
}
