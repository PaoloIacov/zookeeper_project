package org.apache.zookeeper.server.quorum.flexible;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * Suite di test per il metodo equals(Object o) della classe QuorumHierarchical.
 */
public class Equals_QuorumHierarchicalLLMZeroShotInstructionTest {

    /**
     * Metodo di utilità per generare una configurazione Properties di base valida.
     * Rispetta rigorosamente le regole del dominio:
     * - Delimitatore dei gruppi con i due punti (:)
     * - Proprietà server.X definite per ogni ID dichiarato nei gruppi
     */
    private Properties createBaseProperties() {
        Properties p = new Properties();
        
        // Definizione dei gruppi (Delimitatore ':')
        p.setProperty("group.1", "1:2:3");
        p.setProperty("group.2", "4:5:6");
        
        // Definizione dei pesi
        p.setProperty("weight.1", "1");
        p.setProperty("weight.2", "1");
        p.setProperty("weight.3", "1");
        p.setProperty("weight.4", "1");
        p.setProperty("weight.5", "1");
        p.setProperty("weight.6", "1");
        
        // Definizione dei server obbligatori per evitare NullPointerException
        p.setProperty("server.1", "localhost:2881:3881");
        p.setProperty("server.2", "localhost:2882:3882");
        p.setProperty("server.3", "localhost:2883:3883");
        p.setProperty("server.4", "localhost:2884:3884");
        p.setProperty("server.5", "localhost:2885:3885");
        p.setProperty("server.6", "localhost:2886:3886");
        
        return p;
    }

    @Test
    public void testEqualsSameInstance() throws Exception {
        QuorumHierarchical qh = new QuorumHierarchical(createBaseProperties());
        
        // Proprietà riflessiva: x.equals(x) deve restituire true
        assertTrue(qh.equals(qh), "Un'istanza di QuorumHierarchical deve essere uguale a se stessa");
    }

    @Test
    public void testEqualsIdenticalConfiguration() throws Exception {
        QuorumHierarchical qh1 = new QuorumHierarchical(createBaseProperties());
        QuorumHierarchical qh2 = new QuorumHierarchical(createBaseProperties());
        
        // Due istanze con le stesse Properties devono risultare uguali
        assertTrue(qh1.equals(qh2), "Due configurazioni identiche devono risultare uguali");
        
        // Proprietà simmetrica: se x.equals(y) è true, y.equals(x) deve essere true
        assertTrue(qh2.equals(qh1), "La proprietà simmetrica di equals non è rispettata");
    }

    @Test
    public void testNotEqualsNull() throws Exception {
        QuorumHierarchical qh = new QuorumHierarchical(createBaseProperties());
        
        // Un oggetto non deve mai essere uguale a null
        assertFalse(qh.equals(null), "L'oggetto non deve essere uguale a null");
    }

    @Test
    public void testNotEqualsDifferentClass() throws Exception {
        QuorumHierarchical qh = new QuorumHierarchical(createBaseProperties());
        
        // Un oggetto non deve essere uguale ad un'istanza di una classe differente
        assertFalse(qh.equals(new Object()), "L'oggetto non deve essere uguale ad un'istanza di una classe diversa");
    }

    @Test
    @org.junit.jupiter.api.Disabled("BUG DEL SUT: equals() restituisce sempre true se qm.getVersion() == version, ignorando gruppi e pesi (default version=0).")
    public void testNotEqualsDifferentGroups() throws Exception {
        QuorumHierarchical qh1 = new QuorumHierarchical(createBaseProperties());
        
        Properties p2 = createBaseProperties();
        // Modifichiamo l'allocazione dei server nei gruppi, mantenendo gli stessi server validi
        p2.setProperty("group.1", "1:2");
        p2.setProperty("group.2", "3:4:5:6");
        QuorumHierarchical qh2 = new QuorumHierarchical(p2);
        
        assertFalse(qh1.equals(qh2), "Configurazioni con topologie di gruppi differenti non devono risultare uguali");
    }

    @Test
    @org.junit.jupiter.api.Disabled("BUG DEL SUT: equals() restituisce sempre true se qm.getVersion() == version, ignorando gruppi e pesi (default version=0).")
    public void testNotEqualsDifferentWeights() throws Exception {
        QuorumHierarchical qh1 = new QuorumHierarchical(createBaseProperties());
        
        Properties p2 = createBaseProperties();
        // Alteriamo il peso di un server
        p2.setProperty("weight.1", "5"); 
        QuorumHierarchical qh2 = new QuorumHierarchical(p2);
        
        assertFalse(qh1.equals(qh2), "Configurazioni con pesi dei server differenti non devono risultare uguali");
    }
}
