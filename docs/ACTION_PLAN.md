# ACTION PLAN – Modulo Software Testing (ISW2)

> **Corso:** Ingegneria del Software 2 – Software Testing  
> **Docente:** Guglielmo De Angelis (IASI – CNR)  
> **Progetto:** Apache ZooKeeper  
> **Fonte:** `1-Panoramica-ST.pdf`

---

## FASE 0 – Setup dell'ambiente di lavoro

- [x] **0.1** Effettuare un **fork** del repository Apache ZooKeeper su GitHub personale (Completato: il progetto attuale è già il fork)
- [x] **0.2** Configurare un **framework di CI** (GitHub Actions o TravisCI) sul fork (Completato: sovrascritto `ci.yaml` con una versione semplificata per ISW2)
  - L'assenza di CI avrà un **impatto negativo sulla valutazione finale**
- [x] **0.3** Verificare che il progetto compili correttamente (Completato. Il comando consigliato è `mvn clean install -DskipTests -Dcheckstyle.skip=true -Dspotbugs.skip=true -Drat.skip=true`)
- [x] **0.4** Disabilitare (o rimuovere) dalla copia di lavoro **tutti i test nativi** del progetto e mantenere solo quelli che si stanno sviluppando (Completato. Rinominato tutte le cartelle `src/test/java` in `src/test/java_native` per "nascondere" i test a Maven)

---

## FASE 1 – Individuazione delle 2 classi target

> Riferimento: Slide 17 – punto 3.a

- [x] **1.1** Individuare **2 classi** dal progetto Apache ZooKeeper
  - Classi scelte dopo attenta analisi e discussione per evitare banalità e massimizzare l'utilità del Category Partition e Mutation Testing:
    - `org.apache.zookeeper.cli.CreateCommand`
    - `org.apache.zookeeper.server.DigestCalculator`
- [x] **1.2** Documentare le classi scelte nel file `classes.txt` nel formato:
  ```
  <nomePackage>.<nomeSubPackage>.….<nomeClasse>
  ```
  (Completato, creato il file `classes.txt` con le classi in ordine alfabetico)

---

## FASE 2 – Definizione manuale dei test (Category Partition)

> Riferimento: Slide 17 – punto 3.b

- [ ] **2.1** Per ogni classe, adottare un approccio **black-box** basato sulle funzionalità (esplicite e implicite)
- [ ] **2.2** Definire le **categorie** a partire da:
  - Specifiche e documentazione disponibile
  - Solo se strettamente necessario, inferirle dal codice
- [ ] **2.3** Definire manualmente **N test** in base alle categorie individuate
- [ ] **2.4** Progettare, implementare e **documentare il processo** seguito per ogni test

---

## FASE 3 – Generazione automatica dei test

> Riferimento: Slide 17 – punto 3.c

Per ogni classe, generare automaticamente N test tramite **tre approcci distinti**:

### 3.A – Approcci randomici
- [ ] **3.A.1** Generare test con approcci random
- [ ] **3.A.2** Documentare l'approccio utilizzato (tool, parametri, strategia)

### 3.B – Interazione con LLM
- [ ] **3.B.1** Sperimentare **vari tipi di prompt** per la generazione di test
- [ ] **3.B.2** Dettagliare i test ottenuti
- [ ] **3.B.3** Documentare **ogni aspetto** del processo di generazione (prompt usati, risposte, iterazioni, raffinamenti)

### 3.C – Approcci guidati da coverage (control-flow)
- [ ] **3.C.1** Generare test guidati da metriche di coverage control-flow
- [ ] **3.C.2** Documentare in dettaglio l'approccio seguito

---

## FASE 4 – Integrazione dei test nel ciclo di build

> Riferimento: Slide 17 – punto 3.d

- [ ] **4.1** Includere tutti i test generati (manuali + automatici) nel ciclo di **build** del progetto
- [ ] **4.2** Verificare che i test vengano eseguiti correttamente nella pipeline Maven
- [ ] **4.3** Preferibilmente sfruttare le **integrazioni con il framework CI** configurato

---

## FASE 5 – Validazione della qualità dei test: Metriche di adeguatezza

> Riferimento: Slide 18 – punto 4.a

- [ ] **5.1** Scegliere **2 metriche di adeguatezza** per i test
  - Non necessariamente calcolate tramite tool
- [ ] **5.2** Calcolare il valore delle metriche per i test generati
- [ ] **5.3** **Comparare** i valori ottenuti tra i diversi approcci di generazione
- [ ] **5.4** **Argomentare** le differenze riscontrate
- [ ] **5.5** Valutare la **rilevanza e significatività** dei test rispetto alle funzionalità attese dalla classe/metodo
- [ ] **5.6** Per i test manuali: **aumentare il valore** di entrambe le metriche migliorando l'insieme di test o la loro implementazione

---

## FASE 6 – Validazione della qualità dei test: Mutation Testing

> Riferimento: Slide 18 – punto 4.b

- [ ] **6.1** Applicare **mutazioni** sulle classi (e sui metodi) considerate
- [ ] **6.2** Valutare come i test implementati **reagiscono** alle mutazioni
- [ ] **6.3** Aggiungere nuovi test per **migliorare la robustezza**
- [ ] **6.4** Per i test manuali: aggiungere nuovi test per **aumentare l'adeguatezza**

---

## FASE 7 – Stima della Reliability

> Riferimento: Slide 18 – punto 4.c

- [ ] **7.1** Per ognuna delle 2 classi, **stimare la reliability** assumendo:
  - Profili operazionali con **probabilità di utilizzo uniforme**
  - Rispetto all'insieme **finale** di test (dopo le evoluzioni delle Fasi 5 e 6)
- [ ] **7.2** Se necessario per il completamento del build, **disabilitare** eventuali test che falliscono

---

## FASE 8 – Sintesi di varianti con LLM e confronto

> Riferimento: Slide 19 – punto 5

- [ ] **8.1** Utilizzare un LLM per **sintetizzare varianti** delle 2 classi originali (come descritto nello schema delle slides)
- [ ] **8.2** Generare automaticamente nuovi test per le varianti (usando gli approcci della Fase 3.C)
- [ ] **8.3** **Validare** se le funzionalità iniziali delle varianti sono state mantenute
- [ ] **8.4** **Confrontare e analizzare** in dettaglio le differenze tra i test generati automaticamente per le classi originali e le varianti

---

## FASE 9 – Redazione del Report

> Riferimento: Slide 20 – punto 6

- [ ] **9.1** Redigere un report **dettagliato** che descriva tutte le attività svolte per entrambe le classi
- [ ] **9.2** Il report deve **descrivere e giustificare**:
  - Cosa è stato fatto
  - In che contesto
  - Quali problemi si cercavano di risolvere
  - La metodologia seguita
  - I risultati ottenuti
- [ ] **9.3** Requisiti di formato:
  - **~12 pagine** A4, interlinea singola, carattere **Arial 10**
  - Non esagerare con titoli, margini e fronzoli tipografici
  - Figure, tabelle e listati di codice: **allegati alla fine**, riferiti nel testo
  - Figure/tabelle/listati **NON devono includere testo esplicativo** – la discussione va nelle 12 pagine
  - Formato **PDF** altamente desiderabile

---

## FASE 10 – Consegna e Discussione

> Riferimento: Slide 20 – punti 7, 8, 9

- [ ] **10.1** Creare il file `classes.txt` con i nomi delle classi in ordine alfabetico
- [ ] **10.2** Inviare report e file via **email** a `guglielmo.deangelis@iasi.cnr.it` entro le scadenze su Teams
- [ ] **10.3** Preparare la **presentazione** del report e la **discussione orale** sugli argomenti del corso
- [ ] **10.4** Assicurarsi che il **repository GitHub** sia accessibile e aggiornato per la valutazione

---

## Riepilogo delle Dipendenze tra Fasi

```
FASE 0 (Setup)
  └── FASE 1 (Scelta classi)
        ├── FASE 2 (Test manuali – Category Partition)
        ├── FASE 3 (Test automatici: Random + LLM + Coverage)
        │     └── FASE 4 (Integrazione nel build/CI)
        │           ├── FASE 5 (Metriche di adeguatezza)
        │           ├── FASE 6 (Mutation Testing)
        │           │     └── FASE 7 (Stima Reliability)
        │           └── FASE 8 (Varianti LLM + Confronto)
        └── FASE 9 (Report)
              └── FASE 10 (Consegna + Discussione orale)
```
