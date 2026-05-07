# PROCESS NOTES – Modulo Software Testing (ISW2)

> **Scopo:** Raccogliere tutte le scelte progettuali, i concetti chiave e le decisioni prese durante lo svolgimento del progetto.  
> **Progetto:** Apache ZooKeeper  
> **Fonte iniziale:** `1-Panoramica-ST.pdf` – Prof. De Angelis

---

## 1. Concetti fondamentali del corso

Il modulo Software Testing di ISW2 copre i seguenti argomenti (sillabo orientativo):

| # | Argomento | Descrizione |
|---|-----------|-------------|
| 1 | **Intro and General Concepts** | Concetti introduttivi sul software testing |
| 2 | **Test Automation e Continuous Testing** | Automazione dei test e testing continuo nel ciclo di sviluppo |
| 3 | **Unit and Integration Testing** | Test unitari e di integrazione: livelli, scopi, differenze |
| 4 | **Approaches to Test Generation** | Tecniche per la generazione di test (manuali e automatiche) |
| 5 | **Adequacy of the Tests: Control Flow Coverage** | Misure di copertura basate sul flusso di controllo |
| 6 | **Adequacy of the Tests: Mutation Testing** | Mutation testing come metrica di adeguatezza |
| 7 | **Coverage-based Approaches to Test Generation** | Generazione di test guidata da metriche di copertura |

### Libri di riferimento
- A.P. Mathur: *"Foundations of Software Testing, 2/e"*. Pearson, 2013.
- B. Garcia: *"Mastering Software Testing with JUnit 5"*. Packt, 2017.
- P. Tahchiev et al.: *"JUnit in Action"* 2a Ed., Manning, 2010.
- W.E. Lewis: *"Software Testing and Continuous Quality Improvement"*. CRC Press, 2017.
- J. Humble, D. Farley: *"Continuous Delivery"*. Addison-Wesley, 2011.

---

## 2. Struttura del corso ISW2

Il corso è diviso in **due moduli**:

| Modulo | Docente | Peso |
|--------|---------|------|
| **Misurazione e miglioramento della qualità** | Prof. Davide Falessi | 66% |
| **Software Testing** | Prof. Guglielmo De Angelis | 34% |

---

## 3. Scelte progettuali e decisioni

### 3.1 Scelta del progetto
- **Progetto assegnato:** Apache ZooKeeper
- Il progetto è un progetto open-source della Apache Software Foundation con sorgenti su GitHub

### 3.2 Setup dell'ambiente
> **Decisione da documentare:** Quale framework di CI è stato scelto (GitHub Actions vs TravisCI) e perché.

- **CI configurata:** GitHub Actions
- **Motivazione:** GitHub Actions è integrato nativamente su GitHub, offrendo un'ottima integrazione con il fork del progetto. È stata configurata una build semplificata rispetto alla complessa CI originale di Apache ZooKeeper (che impiegava ore e includeva test C++ e check di stile molto stringenti come SpotBugs, Rat e Checkstyle). La nostra CI personalizzata esegue la build Maven (`mvn clean install`) e i test (`mvn test`) saltando esplicitamente i check di stile (`-Dcheckstyle.skip=true -Dspotbugs.skip=true -Drat.skip=true`) e permettendo la build anche in assenza di test in alcuni moduli (`-DfailIfNoTests=false`). Questo evita fallimenti prematuri durante lo sviluppo e la modifica del codice per il corso. È stato utilizzato il caching nativo di `actions/setup-java@v4` per velocizzare le build.
- **Nota:** L'uso di CI ha impatto diretto sulla valutazione finale

### 3.3 Scelta delle 2 classi
> **Decisione da documentare:** Quali classi sono state scelte e il ragionamento dietro la scelta.

- **Classe 1:** _(da compilare)_
- **Classe 2:** _(da compilare)_
- **Criteri di esclusione:**
  - Non scegliere classi i cui test risultino banali (coverage al 100% immediato)
  - La selezione va concordata/verificata con il Prof. Falessi

---

## 4. Concetti chiave: Category Partition Testing

> Tecnica black-box per la definizione manuale dei test.

### Principi guida (dal PDF):
- L'approccio deve essere **black-box**, basato sulle **funzionalità** della classe (esplicite e implicite)
- Le **categorie** si definiscono da:
  1. **Specifiche e documentazione** disponibili (priorità)
  2. Dal codice solo se **strettamente necessario**
- I test vanno **progettati, implementati e documentati** descrivendo il processo

### Decisioni da prendere:
- [ ] Quali categorie sono state identificate per Classe 1?
- [ ] Quali categorie sono state identificate per Classe 2?
- [ ] Quanti test (N) sono stati generati per categoria?
- [ ] Le categorie derivano da documentazione o da codice? Giustificare.

---

## 5. Concetti chiave: Generazione automatica dei test

Il progetto richiede **tre approcci distinti** per la generazione automatica:

### 5.1 Approcci randomici
- **Tool utilizzato:** _(da compilare – es. Randoop, EvoSuite random mode, ecc.)_
- **Configurazione:** _(da compilare)_
- **Numero di test generati:** _(da compilare)_

### 5.2 Interazione con LLM
- **LLM utilizzato:** _(da compilare – es. ChatGPT, Gemini, Claude, ecc.)_
- **Strategia di prompting:**
  - Descrivere ogni tipo di prompt sperimentato
  - Documentare le risposte e i raffinamenti successivi
  - Registrare le differenze tra i test ottenuti con prompt diversi

### 5.3 Approcci guidati da coverage control-flow
- **Tool utilizzato:** _(da compilare – es. EvoSuite, JaCoCo + generazione guidata, ecc.)_
- **Metriche di coverage target:** _(da compilare)_
- **Approccio documentato in dettaglio:** _(da compilare)_

---

## 6. Concetti chiave: Metriche di adeguatezza

> Due metriche di adeguatezza da scegliere (non necessariamente calcolate tramite tool).

### Decisioni da prendere:
- [ ] Quali 2 metriche sono state scelte? Perché?
- [ ] Come sono state calcolate?
- [ ] Quali differenze sono emerse tra i diversi approcci di generazione?
- [ ] Come si è migliorato il valore delle metriche per i test manuali?

### Possibili metriche (da valutare):
- Statement Coverage
- Branch Coverage
- Condition Coverage
- MC/DC Coverage
- Mutation Score
- Path Coverage
- _(altre da definire in base al sillabo del corso)_

---

## 7. Concetti chiave: Mutation Testing

> Applicare mutazioni alle classi target per valutare la robustezza dei test.

### Aspetti da documentare:
- **Tool di mutation utilizzato:** _(da compilare – es. PIT/PITest, ecc.)_
- **Tipi di mutanti generati:** _(da compilare)_
- **Mutation Score ottenuto (prima):** _(da compilare)_
- **Mutation Score ottenuto (dopo miglioramento):** _(da compilare)_
- **Test aggiunti per migliorare la robustezza:** _(da compilare)_
- **Mutanti sopravvissuti e analisi:** _(da compilare)_

---

## 8. Concetti chiave: Stima della Reliability

> Stimare la reliability assumendo profili operazionali con probabilità di utilizzo uniforme.

### Aspetti da documentare:
- **Modello di reliability utilizzato:** _(da compilare)_
- **Profilo operazionale:** probabilità uniforme sull'insieme finale dei test
- **Reliability stimata – Classe 1:** _(da compilare)_
- **Reliability stimata – Classe 2:** _(da compilare)_
- **Test disabilitati per completamento build:** _(elencare e giustificare)_

---

## 9. Concetti chiave: Varianti con LLM

> Sintetizzare varianti delle classi con LLM e confrontare i test.

### Schema del processo (dal PDF, Slide 19):
1. Prendere le classi originali (punto 3.a)
2. Usare un LLM per sintetizzare **varianti funzionalmente equivalenti**
3. Generare automaticamente test per le varianti (approcci del punto 3.c)
4. **Validare:** le funzionalità iniziali sono state mantenute?
5. **Confrontare:** differenze tra i test generati per l'originale e per le varianti

### Decisioni da documentare:
- [ ] Quale LLM è stato usato per generare le varianti?
- [ ] Che tipo di prompt è stato utilizzato?
- [ ] Le varianti mantengono le funzionalità originali? Come è stato verificato?
- [ ] Quali differenze emergono tra i test delle classi originali e delle varianti?

---

## 10. Requisiti del Report finale

| Aspetto | Requisito |
|---------|-----------|
| **Lunghezza** | ~12 pagine A4 |
| **Font** | Arial 10 |
| **Interlinea** | Singola |
| **Formato** | PDF (altamente desiderabile) |
| **Contenuto** | Deve coprire entrambe le classi |
| **Stile** | DETTAGLIATO – descrivere e giustificare ogni attività |
| **Figure/Tabelle** | Allegate alla fine, riferite nel testo |
| **Nota** | Figure/tabelle NON devono includere testo esplicativo – la discussione va nel corpo delle 12 pagine |

### Struttura suggerita del report:
1. Introduzione e contesto
2. Scelta delle classi e giustificazione
3. Test manuali (Category Partition) – per ogni classe
4. Test automatici (Random, LLM, Coverage) – per ogni classe
5. Integrazione nel build e CI
6. Validazione: metriche di adeguatezza
7. Validazione: mutation testing
8. Stima della reliability
9. Varianti LLM e confronto
10. Conclusioni e lessons learned

---

## 11. Consegna

| Elemento | Dettaglio |
|----------|-----------|
| **File da consegnare** | Report (PDF) + `classes.txt` |
| **Destinatario** | `guglielmo.deangelis@iasi.cnr.it` |
| **Scadenza** | Pubblicata sul calendario Teams |
| **Repository** | GitHub fork con CI – verrà valutato |
| **Orale** | Presentazione del report + discussione sugli argomenti del corso |

---

## 12. Log delle decisioni

> Questa sezione viene aggiornata man mano che si prendono decisioni progettuali.

| Data | Decisione | Motivazione |
|------|-----------|-------------|
| 2026-05-06 | Scelta GitHub Actions e sostituzione della CI di default di ZooKeeper | La CI di default di Apache ZooKeeper era troppo pesante e prona a falsi positivi (licenze Rat, Checkstyle, SpotBugs) per gli scopi del corso. Abbiamo sostituito `ci.yaml` con una versione che fa solo `mvn install` e `mvn test` ignorando i controlli di stile, velocizzando il processo e prevenendo errori causati dalla manipolazione del codice e dei test per le consegne ISW2. |
| 2026-05-06 | Disabilitazione dei test nativi tramite rinominazione delle cartelle | Invece di eliminare i test (che potrebbe complicare futuri merge o la lettura di esempi di test), abbiamo rinominato tutte le directory `src/test/java` in `src/test/java_native` e ricreato delle `src/test/java` vuote. Questo nasconde i test nativi a Maven (che non li compilerà né li eseguirà), permettendo di eseguire unicamente i test che verranno sviluppati per il progetto. |
| 2026-05-06 | Selezione delle due classi target per il testing: `CreateCommand` e `DigestCalculator` | Scelte basate sull'evitare classi "banali" (con coverage 100% facile) o mostruosamente complesse (`DataTree`). `CreateCommand` si presta perfettamente al Category Partition (logiche condizionali su CLI args e Mocking di ZooKeeper), mentre `DigestCalculator` è ideale per il Mutation Testing (calcoli matematici su hash CRC32 che PITest altera in modo sofisticato). |
| 2026-05-06 | Integrazione Maven Failsafe e `mvn verify` in CI | Come esplicitamente raccomandato, abbiamo separato i test di unità dai test di integrazione. Il `maven-failsafe-plugin` è stato aggiunto al `pom.xml` principale, e la GitHub Action (`ci.yaml`) è stata aggiornata per eseguire `mvn verify` in modo da eseguire ed esportare i risultati sia di Surefire che di Failsafe. |

---

## 13. Problemi incontrati e soluzioni

> Sezione per tracciare i problemi tecnici e concettuali incontrati durante il progetto.

| # | Problema | Soluzione | Status |
|---|----------|-----------|--------|
| 1 | _(da compilare)_ | _(da compilare)_ | ⬜ |

---

## 14. Comunicazioni con i docenti

| Canale | Indirizzo |
|--------|-----------|
| **Email De Angelis** | `guglielmo.deangelis@iasi.cnr.it` |
| **Email Falessi** | `falessi@ing.uniroma2.it` |
| **Teams – Canale Generale** | ISW2 A.A. 2025-26 |
| **Teams – Canale Software Testing** | Per domande specifiche sul modulo |
| **Mailing list ST** | `edbc9edb.uniroma2.onmicrosoft.com@emea.teams.ms` |

> ⚠️ **ATTENZIONE:** Il Prof. De Angelis non legge TEAMS istantaneamente. Comunicare **sempre via email**, preferibilmente mettendo in CC l'indirizzo del canale Software Testing. Email personali solo per richieste strettamente private.
