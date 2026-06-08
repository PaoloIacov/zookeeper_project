package org.apache.zookeeper.server.quorum.flexible;

import org.apache.zookeeper.common.ConfigException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Suite di test per il costruttore della classe QuorumHierarchical di Apache ZooKeeper.
 *
 * Iterazione V4 (Bug-Aware): aggiunta delle properties server.X per evitare
 * la NullPointerException in computeGroupWeight(), bug scoperto durante
 * la generazione dei test per getWeight (cfr. QH_ZeroShot.md, Iterazione 2).
 */
public class Constructor_File_QuorumHierarchicalLLMZeroShotInstructionTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Dovrebbe inizializzare correttamente i gruppi e i pesi da un file valido")
    public void testCostruttoreConFileValido() throws Exception {
        // Arrange: Creazione di un file di properties valido
        File configFile = tempDir.resolve("valid_quorum.cfg").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            // NOTA DOMINIO: Utilizziamo esplicitamente i due punti ':' come delimitatore.
            writer.write("group.1=1:2:3\n");
            writer.write("group.2=4:5:6\n");
            // REGOLA AGGIUNTIVA (V4 Bug-Aware): definire server.X per ogni server
            // dichiarato nei gruppi, altrimenti computeGroupWeight() lancia NPE.
            for (int i = 1; i <= 6; i++) {
                writer.write("server." + i + "=localhost:" + (2780 + i * 10) + ":" + (3780 + i * 10) + "\n");
            }
            writer.write("weight.1=2\n");
            writer.write("weight.2=1\n");
            writer.write("weight.3=1\n");
            writer.write("weight.4=1\n");
            writer.write("weight.5=1\n");
            writer.write("weight.6=1\n");
        }

        // Act
        QuorumHierarchical quorum = new QuorumHierarchical(configFile.getAbsolutePath());

        // Assert
        assertEquals(2, quorum.getWeight(1), "Il peso del server 1 dovrebbe essere 2");
        assertEquals(1, quorum.getWeight(2), "Il peso del server 2 dovrebbe essere 1");
        assertEquals(1, quorum.getWeight(6), "Il peso del server 6 dovrebbe essere 1");
    }

    @Test
    @DisplayName("Dovrebbe lanciare ConfigException se il file di configurazione non esiste")
    public void testCostruttoreConFileInesistente() {
        // Arrange
        String invalidPath = tempDir.resolve("non_esistente.cfg").toFile().getAbsolutePath();

        // Act & Assert
        assertThrows(ConfigException.class, () -> new QuorumHierarchical(invalidPath),
                "L'inizializzazione con un file mancante deve lanciare ConfigException");
    }

    @Test
    @DisplayName("Dovrebbe lanciare ConfigException se si utilizza una virgola anziché i due punti come delimitatore nel gruppo")
    public void testCostruttoreConDelimitatoreVirgolaErrato() throws IOException {
        // Arrange: Creiamo un file dove usiamo la virgola ',' infrangendo la regola di dominio
        File configFile = tempDir.resolve("invalid_delimiter_quorum.cfg").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            // Errore intenzionale per testare la validazione del parser
            writer.write("group.1=1,2,3\n");
            writer.write("weight.1=1\n");
            writer.write("weight.2=1\n");
            writer.write("weight.3=1\n");
        }

        // Act & Assert
        assertThrows(ConfigException.class, () -> new QuorumHierarchical(configFile.getAbsolutePath()),
                "Il costruttore deve fallire se la stringa del gruppo contiene virgole anziché i due punti");
    }

    @Test
    @DisplayName("Dovrebbe lanciare ConfigException se l'ID del server non è un numero valido (es. carattere testuale)")
    public void testCostruttoreConServerIdNonNumerico() throws IOException {
        // Arrange
        File configFile = tempDir.resolve("invalid_id_quorum.cfg").toFile();
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write("group.1=1:A:3\n"); // 'A' non è un Long valido
            writer.write("weight.1=1\n");
        }

        // Act & Assert
        assertThrows(ConfigException.class, () -> new QuorumHierarchical(configFile.getAbsolutePath()),
                "Il parser deve lanciare ConfigException se fallisce il parsing in Long dell'ID del server");
    }
}
