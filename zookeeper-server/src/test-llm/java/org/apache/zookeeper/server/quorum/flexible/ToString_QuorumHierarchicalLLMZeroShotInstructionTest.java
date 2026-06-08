package org.apache.zookeeper.server.quorum.flexible;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Suite di test per validare il comportamento del metodo toString() 
 * della classe QuorumHierarchical di Apache ZooKeeper.
 */
@DisplayName("Suite di test per QuorumHierarchical.toString()")
public class ToString_QuorumHierarchicalLLMZeroShotInstructionTest {

    @Test
    @DisplayName("toString() deve restituire una stringa non nulla per una configurazione a singolo gruppo")
    void testToStringSingleGroup() throws Exception {
        // Arrange
        Properties props = new Properties();
        // Regola 1: Delimitatore a due punti (:) per i server ID nel gruppo
        props.setProperty("group.1", "1:2:3");
        
        // Assegnazione dei pesi per coerenza di sistema
        props.setProperty("weight.1", "1");
        props.setProperty("weight.2", "1");
        props.setProperty("weight.3", "1");
        
        // Regola 2: Property server.X obbligatoria per evitare NullPointerException
        props.setProperty("server.1", "localhost:2888:3888");
        props.setProperty("server.2", "localhost:2889:3889");
        props.setProperty("server.3", "localhost:2890:3890");

        QuorumHierarchical sut = new QuorumHierarchical(props);

        // Act
        String result = sut.toString();

        // Assert
        assertNotNull(result, "Il risultato di toString() non deve mai essere nullo");
        assertFalse(result.trim().isEmpty(), "Il risultato di toString() non deve essere una stringa vuota");
    }

    @Test
    @DisplayName("toString() deve generare un output formattato e gestire topologie multi-gruppo")
    void testToStringMultipleGroups() throws Exception {
        // Arrange
        Properties props = new Properties();
        
        // Regola 1: Due gruppi distinti delimitati da due punti
        props.setProperty("group.1", "1:2");
        props.setProperty("group.2", "3:4");
        
        props.setProperty("weight.1", "1");
        props.setProperty("weight.2", "2");
        props.setProperty("weight.3", "1");
        props.setProperty("weight.4", "2");
        
        // Regola 2: Identificativi server.X mappati ad ogni nodo per la stabilità del SUT
        props.setProperty("server.1", "node1:2888:3888");
        props.setProperty("server.2", "node2:2888:3888");
        props.setProperty("server.3", "node3:2888:3888");
        props.setProperty("server.4", "node4:2888:3888");

        QuorumHierarchical sut = new QuorumHierarchical(props);
        
        // Act & Assert
        assertDoesNotThrow(() -> {
            String result = sut.toString();
            assertNotNull(result);
            assertTrue(result.length() > 0, "La lunghezza della rappresentazione stringa deve essere maggiore di 0");
        }, "L'invocazione di toString() non deve sollevare eccezioni in un caso multi-gruppo correttamente validato");
    }

    @Test
    @DisplayName("toString() non deve fallire in caso di configurazione minimale (Singolo Server)")
    void testToStringMinimalConfiguration() throws Exception {
        // Arrange
        Properties props = new Properties();
        // Regola 1: Unico server nel gruppo (non servono separatori ma la logica è rispettata)
        props.setProperty("group.1", "1");
        
        props.setProperty("weight.1", "5"); // Nodo con peso personalizzato
        
        // Regola 2: Definizione esplicita
        props.setProperty("server.1", "localhost:2888:3888");

        QuorumHierarchical sut = new QuorumHierarchical(props);

        // Act & Assert
        String result = assertDoesNotThrow(() -> sut.toString(), 
            "Il metodo toString non deve lanciare eccezioni su configurazioni minimali");
        
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
    }
}
