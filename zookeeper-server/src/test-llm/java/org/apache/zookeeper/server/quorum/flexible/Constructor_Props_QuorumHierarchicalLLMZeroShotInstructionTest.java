package org.apache.zookeeper.server.quorum.flexible;

import org.apache.zookeeper.common.ConfigException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test per il costruttore QuorumHierarchical(Properties qp).
 * Verifica le corrette configurazioni di gruppi e pesi, ponendo enfasi
 * sull'uso corretto del delimitatore ':' per i membri del gruppo.
 */
public class Constructor_Props_QuorumHierarchicalLLMZeroShotInstructionTest {

    @Test
    @DisplayName("Test configurazione valida con delimitatore a due punti (':')")
    public void testValidConfigurationWithColons() throws ConfigException {
        Properties qp = new Properties();
        // Configurazione corretta: uso dei due punti
        qp.setProperty("group.1", "1:2:3");
        qp.setProperty("group.2", "4:5:6");
        
        qp.setProperty("weight.1", "2");
        qp.setProperty("weight.2", "1");
        qp.setProperty("weight.4", "3");

        QuorumHierarchical quorum = new QuorumHierarchical(qp);

        // Verifiche sui pesi configurati
        assertEquals(2, quorum.getWeight(1), "Il peso del server 1 deve essere 2");
        assertEquals(1, quorum.getWeight(2), "Il peso del server 2 deve essere 1");
        assertEquals(3, quorum.getWeight(4), "Il peso del server 4 deve essere 3");
        
        // Verifica dei pesi di default (se un server è nel gruppo ma non ha peso esplicito, assume peso 1)
        assertEquals(1, quorum.getWeight(3), "Il server 3 senza peso esplicito deve avere peso 1");
        assertEquals(1, quorum.getWeight(5), "Il server 5 senza peso esplicito deve avere peso 1");
    }

    @Test
    @DisplayName("Test fallimento con delimitatore a virgola (',')")
    @Disabled("Ignored because LLM didn't have the rule for delimiter")
    public void testInvalidDelimiterCommaThrowsException() {
        Properties qp = new Properties();
        // Configurazione errata: uso della virgola invece dei due punti
        qp.setProperty("group.1", "1,2,3");
        qp.setProperty("weight.1", "1");

        // Il parser si aspetta Long.parseLong() su "1,2,3" se splittato male, 
        // o fallisce la validazione dei server ID, lanciando una ConfigException
        Exception exception = assertThrows(ConfigException.class, () -> {
            new QuorumHierarchical(qp);
        });

        assertNotNull(exception.getMessage(), "L'eccezione dovrebbe contenere un messaggio di errore");
    }

    @Test
    @DisplayName("Test fallimento con un server assegnato a molteplici gruppi")
    public void testServerInMultipleGroupsThrowsException() {
        Properties qp = new Properties();
        qp.setProperty("group.1", "1:2:3");
        // Errore: Il server 2 è già nel gruppo 1
        qp.setProperty("group.2", "4:5:2"); 

        assertThrows(ConfigException.class, () -> {
            new QuorumHierarchical(qp);
        }, "Dovrebbe lanciare ConfigException se un server appartiene a più di un gruppo");
    }

    @Test
    @DisplayName("Test configurazione senza gruppi definiti")
    public void testNoGroupsDefined() {
        Properties qp = new Properties();
        qp.setProperty("weight.1", "1");
        qp.setProperty("weight.2", "2");

        // Una topologia gerarchica senza gruppi non è valida
        assertThrows(ConfigException.class, () -> {
            new QuorumHierarchical(qp);
        }, "Dovrebbe lanciare ConfigException se nessun gruppo è configurato");
    }
    
    @Test
    @DisplayName("Test configurazione con ID server non numerico")
    public void testNonNumericServerIdThrowsException() {
        Properties qp = new Properties();
        // Configurazione errata: ID server alfabetico
        qp.setProperty("group.1", "1:A:3");

        assertThrows(ConfigException.class, () -> {
            new QuorumHierarchical(qp);
        }, "Dovrebbe lanciare ConfigException se l'ID del server nel gruppo non è un numero valido");
    }
}
