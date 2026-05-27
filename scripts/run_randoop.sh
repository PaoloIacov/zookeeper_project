#!/bin/bash
# ===========================================================================
# Script: run_randoop.sh
# Progetto: Apache ZooKeeper ISW2 - Fase 3.A (Generazione Automatica Test)
# Descrizione: Esegue Randoop v4.3.3 (Feedback-Directed Random Testing)
#              per generare test automatici per le classi target del progetto.
#
# Riferimenti:
#   - Paper: "Feedback-directed Random Test Generation"
#            Pacheco, Lahiri, Ernst, Ball (ICSE 2007)
#   - Tool: Randoop v4.3.3 (docs/tools/randoop-all-4.3.3.jar)
#   - Documentazione: PROCESS_NOTES.md sezione 5.1
#
# Prerequisiti:
#   - Java 11+ installato
#   - Progetto compilato: mvn compile test-compile -pl zookeeper-server -am -DskipTests
#
# Uso:
#   ./scripts/run_randoop.sh [quorum|clientcnxn|all]
#
# Output:
#   - Regression Tests: *RandoopTest*.java (test che PASSANO, catturano comportamento attuale)
#   - Error-Revealing Tests: *ErrorTest*.java (test che FALLISCONO, violano contratti del SUT)
#
# Parametri Randoop utilizzati:
#   --time-limit=180       Tempo di generazione: 180 secondi per classe (default paper: 120s)
#   --output-limit=3000    Massimo numero di test generati per classe
#   --junit-output-dir=... Directory output dei test generati
#   --testclass=...        Classe target + Classe Helper (seed sequences)
#
# Nota su JUnit 4 vs JUnit 5:
#   Randoop genera test in JUnit 4 per design. La compatibilità con il framework
#   JUnit 5 del progetto è garantita da junit-vintage-engine nel pom.xml.
#   Vedi PROCESS_NOTES.md sezione 5.1 per la giustificazione completa.
# ===========================================================================

set -euo pipefail

# Configurazione
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
RANDOOP_JAR="${PROJECT_ROOT}/docs/tools/randoop-all-4.3.3.jar"
CLASSES_DIR="${PROJECT_ROOT}/zookeeper-server/target/classes"
TEST_CLASSES_DIR="${PROJECT_ROOT}/zookeeper-server/target/test-classes"
OUTPUT_DIR="${PROJECT_ROOT}/zookeeper-server/src/test/java"
TIME_LIMIT=180
OUTPUT_LIMIT=3000

# Colori per output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verifica prerequisiti
echo -e "${YELLOW}=== Randoop v4.3.3 - Feedback-Directed Random Test Generation ===${NC}"
echo ""

if [ ! -f "$RANDOOP_JAR" ]; then
    echo -e "${RED}ERRORE: randoop-all-4.3.3.jar non trovato in docs/tools/${NC}"
    exit 1
fi

if [ ! -d "$CLASSES_DIR" ]; then
    echo -e "${RED}ERRORE: Classi non compilate. Eseguire prima:${NC}"
    echo "  mvn compile test-compile -pl zookeeper-server -am -DskipTests"
    exit 1
fi

# Costruisci classpath
echo -e "${YELLOW}[1/3] Costruzione classpath...${NC}"
cd "$PROJECT_ROOT"
DEPS_CP=$(mvn -pl zookeeper-server dependency:build-classpath -Dmdep.outputFile=/dev/stdout -q 2>/dev/null || echo "")
CLASSPATH="${RANDOOP_JAR}:${CLASSES_DIR}:${TEST_CLASSES_DIR}:${DEPS_CP}"
echo -e "${GREEN}  Classpath costruito.${NC}"

# Funzione per eseguire Randoop su una classe
run_randoop_for_class() {
    local TARGET_CLASS=$1
    local HELPER_CLASS=$2
    local REGRESSION_NAME=$3
    local ERROR_NAME=$4
    local PACKAGE_NAME=$5
    
    echo ""
    echo -e "${YELLOW}[2/3] Esecuzione Randoop per ${TARGET_CLASS}...${NC}"
    echo "  Target class:      ${TARGET_CLASS}"
    echo "  Helper class:      ${HELPER_CLASS}"
    echo "  Time limit:        ${TIME_LIMIT}s"
    echo "  Output limit:      ${OUTPUT_LIMIT} test"
    echo "  Regression output: ${REGRESSION_NAME}*.java"
    echo "  Error output:      ${ERROR_NAME}*.java"
    echo ""
    
    java -cp "$CLASSPATH" \
        randoop.main.Main gentests \
        --testclass="${TARGET_CLASS}" \
        --testclass="${HELPER_CLASS}" \
        --time-limit="${TIME_LIMIT}" \
        --output-limit="${OUTPUT_LIMIT}" \
        --junit-output-dir="${OUTPUT_DIR}" \
        --regression-test-basename="${REGRESSION_NAME}" \
        --error-test-basename="${ERROR_NAME}" \
        --junit-package-name="${PACKAGE_NAME}" \
        2>&1 | tee "${PROJECT_ROOT}/logs/randoop_${REGRESSION_NAME}_$(date +%Y%m%d_%H%M%S).log"
    
    echo ""
    echo -e "${GREEN}[3/3] Generazione completata per ${TARGET_CLASS}${NC}"
    echo "  File generati:"
    find "${OUTPUT_DIR}" -name "${REGRESSION_NAME}*.java" -o -name "${ERROR_NAME}*.java" 2>/dev/null | while read f; do
        echo "    - $(basename "$f") ($(wc -l < "$f") righe)"
    done
}

# Crea directory logs se non esiste
mkdir -p "${PROJECT_ROOT}/logs"

# Parsing argomento
TARGET=${1:-all}

case "$TARGET" in
    quorum)
        run_randoop_for_class \
            "org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical" \
            "org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper" \
            "QuorumHierarchicalRandoopTest" \
            "ErrorTest" \
            "org.apache.zookeeper.server.quorum.flexible"
        ;;
    clientcnxn)
        run_randoop_for_class \
            "org.apache.zookeeper.ClientCnxn" \
            "org.apache.zookeeper.RandoopClientCnxnHelper" \
            "ClientCnxnRandoopTest" \
            "ClientCnxnErrorTest" \
            "org.apache.zookeeper"
        ;;
    all)
        run_randoop_for_class \
            "org.apache.zookeeper.server.quorum.flexible.QuorumHierarchical" \
            "org.apache.zookeeper.server.quorum.flexible.RandoopQuorumHelper" \
            "QuorumHierarchicalRandoopTest" \
            "ErrorTest" \
            "org.apache.zookeeper.server.quorum.flexible"
        
        run_randoop_for_class \
            "org.apache.zookeeper.ClientCnxn" \
            "org.apache.zookeeper.RandoopClientCnxnHelper" \
            "ClientCnxnRandoopTest" \
            "ClientCnxnErrorTest" \
            "org.apache.zookeeper"
        ;;
    *)
        echo "Uso: $0 [quorum|clientcnxn|all]"
        echo "  quorum     - Genera test per QuorumHierarchical"
        echo "  clientcnxn - Genera test per ClientCnxn"
        echo "  all        - Genera test per entrambe le classi (default)"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}=== Randoop completato ===${NC}"
echo ""
echo "Prossimi passi:"
echo "  1. Verificare la compilazione: mvn compile test-compile -pl zookeeper-server -DskipTests"
echo "  2. Eseguire i regression test: mvn test -pl zookeeper-server -Dtest='*RandoopTest*'"
echo "  3. Gli ErrorTest sono esclusi dal build (vedi pom.xml esclusioni Surefire)"
echo "  4. Documentare i risultati in PROCESS_NOTES.md"
