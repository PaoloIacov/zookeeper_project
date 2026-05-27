package org.apache.zookeeper.server.quorum.flexible;

import java.util.Properties;
import org.apache.zookeeper.common.ConfigException;

/**
 * Helper Factory per Randoop.
 * Fornisce istanze pre-configurate di QuorumHierarchical che Randoop
 * può utilizzare come "seed sequences" per esplorare i metodi della classe.
 * 
 * Senza questa classe, Randoop non riesce a superare il costruttore
 * perché non sa generare Properties con il formato specifico richiesto
 * da ZooKeeper (group.X, weight.X, server.X).
 */
public class RandoopQuorumHelper {

    /**
     * Crea un QuorumHierarchical con un singolo server participant.
     * Configurazione minimale valida: 1 gruppo, 1 server, weight 1.
     */
    public static QuorumHierarchical createSingleNode() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        return new QuorumHierarchical(qp);
    }

    /**
     * Crea un QuorumHierarchical con 3 server in un singolo gruppo.
     * Configurazione tipica per un cluster ZooKeeper minimale.
     */
    public static QuorumHierarchical createThreeNodeCluster() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2:3");
        for (int i = 1; i <= 3; i++) {
            qp.setProperty("weight." + i, "1");
            qp.setProperty("server." + i,
                "localhost:" + (2887 + i) + ":" + (3887 + i) + ":participant");
        }
        return new QuorumHierarchical(qp);
    }

    /**
     * Crea un QuorumHierarchical con 2 gruppi (3 server ciascuno).
     * Configurazione multi-gruppo per testare la logica di quorum gerarchico.
     */
    public static QuorumHierarchical createMultiGroupCluster() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2:3");
        qp.setProperty("group.2", "4:5:6");
        for (int i = 1; i <= 6; i++) {
            qp.setProperty("weight." + i, "1");
            qp.setProperty("server." + i,
                "localhost:" + (2887 + i) + ":" + (3887 + i) + ":participant");
        }
        return new QuorumHierarchical(qp);
    }

    /**
     * Crea un QuorumHierarchical con participant e observer.
     * Utile per testare i metodi che distinguono tra tipi di server.
     */
    public static QuorumHierarchical createWithObserver() throws ConfigException {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1");
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "0");
        qp.setProperty("server.1", "localhost:2888:3888:participant");
        qp.setProperty("server.2", "localhost:2889:3889:observer");
        return new QuorumHierarchical(qp);
    }

    /**
     * Crea un QuorumHierarchical vuoto (Properties vuote).
     * Utile per testare il comportamento su configurazione minima.
     */
    public static QuorumHierarchical createEmpty() throws ConfigException {
        return new QuorumHierarchical(new Properties());
    }

    /**
     * Restituisce un set di Properties valido che Randoop può manipolare.
     */
    public static Properties createValidProperties() {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2:3");
        for (int i = 1; i <= 3; i++) {
            qp.setProperty("weight." + i, "1");
            qp.setProperty("server." + i,
                "localhost:" + (2887 + i) + ":" + (3887 + i) + ":participant");
        }
        return qp;
    }
}
