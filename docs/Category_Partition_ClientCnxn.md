# Category Partition - ClientCnxn

## Descrizione della Classe (Approccio Black-Box)

`ClientCnxn` è il **gestore di connessione** che si interpone tra il client e il cluster ZooKeeper. Il suo compito è garantire che le richieste arrivino a destinazione e che le risposte tornino indietro, nascondendo completamente la complessità di rete, threading e autenticazione.

Vista dall'esterno, le sue responsabilità principali sono:

1. **Inizializzare la sessione**: Il costruttore prepara tutti i parametri di connessione e crea i thread interni (`SendThread`, `EventThread`), senza però avviare la connessione di rete.
2. **Gestire l'identità (Autenticazione)**: `addAuthInfo` accoda le credenziali da presentare al server.
3. **Spedire richieste**: `submitRequest` e `queuePacket` gestiscono l'invio ordinato dei pacchetti.
4. **Chiudere la sessione**: `close` invia la disconnessione al server e termina i thread.

> **Nota Architetturale — chroot non è gestito qui:**
> La traduzione dei percorsi tramite *chroot* (es. `/utenti` → `/miaApp/utenti`) avviene nella classe `ZooKeeper` tramite `prependChroot()`, **prima** che la richiesta venga consegnata a `ClientCnxn`. Quest'ultima riceve già i `Packet` con `clientPath` e `serverPath` già calcolati. Il parametro `chrootPath` non esiste nel costruttore di `ClientCnxn`.

---

Questo documento applica la **procedura sistematica Category Partition in 4+1 step**:
1. Identificare i domini di input
2. Identificare le classi di equivalenza (con Boundary Value Analysis)
3. Combinare le classi di equivalenza
4. Generare la suite di test finale (con Esito e Motivazione)
5. Codice Java (Step 5)

---

## API Pubblica Analizzata

| # | Metodo | Firma |
| :--- | :--- | :--- |
| 1 | Costruttore principale | `ClientCnxn(HostProvider, int, ZKClientConfig, Watcher, ClientCnxnSocket, boolean)` |
| 2 | Test thread corrente | `public static boolean isInEventThread()` |
| 3 | Generatore XID | `public synchronized int getXid()` |
| 4 | Autenticazione | `public void addAuthInfo(String scheme, byte[] auth)` |
| 5 | Pacchetti (analisi teorica) | `submitRequest(...)` e `close()` |
| 6 | Classe Interna | `static class Packet` — `createBB()` e `toString()` |

---

## 1. Metodo: Costruttore `ClientCnxn(...)`

**Cosa fa (Black-Box):** Inizializza la connessione client-server. Calcola i parametri di timeout derivati (`connectTimeout = sessionTimeout / hostProvider.size()`, `readTimeout = sessionTimeout * 2/3`) e istanzia i thread interni. La connessione di rete reale non viene stabilita qui, ma solo al momento della chiamata a `start()`.

### Step 1 – Identificazione dei Domini di Input (Base Choice Coverage)

Essendo un test Black-Box rigoroso, consideriamo tutti i parametri formali dell'API pubblica (costruttore delegato) come domini indipendenti, senza guardare la loro logica interna.

| # | Parametro | Tipo | Provenienza |
| :--- | :--- | :--- | :--- |
| D1 | `HostProvider hp` | Interfaccia | Parametro formale |
| D2 | `int sessionTimeout` | Primitivo intero | Parametro formale |
| D3 | `ZKClientConfig clientConfig` | Oggetto | Parametro formale |
| D4 | `Watcher defaultWatcher` | Interfaccia | Parametro formale |
| D5 | `ClientCnxnSocket socket` | Classe Astratta | Parametro formale |
| D6 | `long sessionId` | Primitivo long | Parametro formale |
| D7 | `byte[] sessionPasswd` | Array di byte | Parametro formale |
| D8 | `boolean canBeReadOnly` | Primitivo booleano | Parametro formale |

### Step 2 – Classi di Equivalenza e Base Choice

Identifichiamo la "Scelta Base" (Base Choice - **BC**), ovvero il caso nominale di funzionamento tipico, per ogni dominio, e le sue varianti di anomalia o confine.

- **D1 (`HostProvider`)**: CE1 `{valido, size > 0}` [**BC**], CE2 `{null}`, CE3 `{size = 0}`
- **D2 (`sessionTimeout`)**: CE1 `{positivo, es. 30000}` [**BC**], CE2 `{0}`, CE3 `{negativo}`, CE4 `{1}` (Boundary Value)
- **D3 (`ZKClientConfig`)**: CE1 `{valido}` [**BC**], CE2 `{null}`
- **D4 (`Watcher`)**: CE1 `{valido}` [**BC**], CE2 `{null}`
- **D5 (`ClientCnxnSocket`)**: CE1 `{valido}` [**BC**], CE2 `{null}`
- **D6 (`sessionId`)**: CE1 `{0}` [**BC**], CE2 `{positivo, riattivazione}`
- **D7 (`sessionPasswd`)**: CE1 `{byte[16]}` [**BC**], CE2 `{null}`
- **D8 (`canBeReadOnly`)**: CE1 `{false}` [**BC**], CE2 `{true}`

### Step 3 – Combinazione (Variazione di Base Choice)

Anziché fare il prodotto cartesiano completo (che darebbe migliaia di test), applichiamo la strategia Base Choice: si crea un test in cui tutti i parametri sono in BC. Successivamente, per ogni dominio, si varia **un solo parametro alla volta** verso le altre sue classi di equivalenza, mantenendo tutti gli altri in BC.

| ID | Variazione | Test Configuration (D1...D8) | Note |
| :--- | :--- | :--- | :--- |
| **T1.1** | Nessuna | `CE1, CE1, CE1, CE1, CE1, CE1, CE1, CE1` | Base Choice (Nominale) |
| **T1.2** | D1 = CE2 | `CE2, CE1, CE1, CE1, CE1, CE1, CE1, CE1` | `HostProvider` nullo |
| **T1.3** | D1 = CE3 | `CE3, CE1, CE1, CE1, CE1, CE1, CE1, CE1` | `HostProvider.size() == 0` |
| **T1.4** | D2 = CE2 | `CE1, CE2, CE1, CE1, CE1, CE1, CE1, CE1` | Timeout nullo |
| **T1.5** | D2 = CE3 | `CE1, CE3, CE1, CE1, CE1, CE1, CE1, CE1` | Timeout negativo |
| **T1.6** | D3 = CE2 | `CE1, CE1, CE2, CE1, CE1, CE1, CE1, CE1` | `clientConfig` nullo |
| **T1.7** | D4 = CE2 | `CE1, CE1, CE1, CE2, CE1, CE1, CE1, CE1` | `defaultWatcher` nullo |
| **T1.8** | D5 = CE2 | `CE1, CE1, CE1, CE1, CE2, CE1, CE1, CE1` | `ClientCnxnSocket` nullo |
| **T1.9** | D6 = CE2 | `CE1, CE1, CE1, CE1, CE1, CE2, CE1, CE1` | `sessionId` positivo |
| **T1.10** | D7 = CE2 | `CE1, CE1, CE1, CE1, CE1, CE1, CE2, CE1` | `sessionPasswd` nullo |
| **T1.11** | D8 = CE2 | `CE1, CE1, CE1, CE1, CE1, CE1, CE1, CE2` | `canBeReadOnly` true |
| **T1.12** | D2 = CE4 | `CE1, CE4, CE1, CE1, CE1, CE1, CE1, CE1` | Timeout = 1 (Boundary Value) |

### Step 4 – Eliminazione Combinazioni Non Ammissibili e Suite di Test Finale

**Nota Metodologica sulla Selezione (Base Choice Coverage):**
Per evitare l'esplosione combinatoria e il mascheramento degli errori (*Single Fault Assumption*), la suite non implementa il prodotto cartesiano completo. Si applica il criterio della **Base Choice Coverage**:
- La combinazione **T1.1** (tutti i parametri nominali) è la **Base Choice**: rappresenta il caso d'uso tipico di un client ZooKeeper che si connette a un cluster già attivo.
- Le combinazioni successive (T1.2–T1.12) variano **una sola dimensione alla volta** rispetto alla Base Choice, permettendo di isolare i comportamenti anomali.

I test T1.2 e T1.6 non ammettono le variazioni sulle dimensioni successive in quanto l'eccezione viene lanciata prima di leggere gli altri parametri.

| ID | D1 (HostProvider) | D2 (sessionTimeout) | D3 (ZKClientConfig) | D4 (Watcher) | D5 (Socket) | D6 (sessionId) | D7 (sessionPasswd) | D8 (canBeReadOnly) | Output Atteso | Esito | Motivazione |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **T1.1** | Valido (size=1) | 30000 | Valido | Valido | Valido | 0 | byte[16] | false | Oggetto istanziato | **Passato** | Base Choice: tutti i parametri nominali, verificato il caso d'uso tipico. |
| **T1.2** | **null** | 30000 | Valido | Valido | Valido | 0 | byte[16] | false | `NullPointerException` | **Passato** | **BUG:** Il costruttore non valida il null. Il test si aspetta l'NPE per documentare il crash. |
| **T1.3** | **size=0** | 30000 | Valido | Valido | Valido | 0 | byte[16] | false | `ArithmeticException` | **Passato** | **BUG:** Mancata validazione di `size=0`. Il test si aspetta la divisione per zero. |
| **T1.4** | Valido (size=1) | **0** | Valido | Valido | Valido | 0 | byte[16] | false | Oggetto istanziato | **Passato** | **BUG:** Timeout zero non validato. Il test si aspetta l'istanza creata (anziché eccezione). |
| **T1.5** | Valido (size=1) | **-1** | Valido | Valido | Valido | 0 | byte[16] | false | Oggetto istanziato | **Passato** | **BUG:** Timeout negativo non validato. Il test si aspetta l'istanza creata (anziché eccezione). |
| **T1.6** | Valido (size=1) | 30000 | **null** | Valido | Valido | 0 | byte[16] | false | `NullPointerException` | **Passato** | **BUG:** Mancata validazione config. Il test si aspetta l'NPE. |
| **T1.7** | Valido (size=1) | 30000 | Valido | **null** | Valido | 0 | byte[16] | false | Oggetto istanziato | **Passato** | Il watcher null viene accettato; il test si aspetta l'istanza creata. |
| **T1.8** | Valido (size=1) | 30000 | Valido | Valido | **null** | 0 | byte[16] | false | Oggetto istanziato | **Passato** | Il socket null viene accettato; il test si aspetta l'istanza creata. |
| **T1.9** | Valido (size=1) | 30000 | Valido | Valido | Valido | **12345L** | byte[16] | false | Oggetto istanziato | **Passato** | sessionId positivo: ripristino di sessione preesistente. |
| **T1.10** | Valido (size=1) | 30000 | Valido | Valido | Valido | 0 | **null** | false | Oggetto istanziato | **Passato** | sessionPasswd null accettato; il test si aspetta l'istanza creata. |
| **T1.11** | Valido (size=1) | 30000 | Valido | Valido | Valido | 0 | byte[16] | **true** | Oggetto istanziato | **Passato** | canBeReadOnly=true: modalità read-only in caso di partizionamento di rete, supportata. |
| **T1.12** | Valido (size=1) | **1** | Valido | Valido | Valido | 0 | byte[16] | false | Oggetto istanziato | **Passato** | BVA: timeout al valore positivo minimo, `connectTimeout = 1 / 1 = 1 ms`. |

*Nota:* Nel file `ClientCnxnTest.java`, la suite usa gli *esiti osservati* come oracoli per documentare il comportamento reale del software e i bug di robustezza individuati.

### Step 5 – Codice Java

```java
// T1.1 - Caso nominale: costruzione con parametri validi
@Test
@Timeout(5)
public void Constructor_ValidParams_StateIsConnecting() throws IOException {
    HostProvider hp = mock(HostProvider.class);
    when(hp.size()).thenReturn(1);
    ClientCnxnSocket socket = mock(ClientCnxnSocket.class);
    ClientCnxn cnxn = new ClientCnxn(hp, 30000, new ZKClientConfig(), mock(Watcher.class), socket, false);
    assertEquals(States.CONNECTING, cnxn.state);
}

// T1.2 - BV3: hostProvider.size() = 0 → ArithmeticException
@Test
@Timeout(5)
public void Constructor_HostProviderSizeZero_ThrowsArithmeticException() {
    HostProvider hp = mock(HostProvider.class);
    when(hp.size()).thenReturn(0);
    ClientCnxnSocket socket = mock(ClientCnxnSocket.class);
    assertThrows(ArithmeticException.class, () ->
            new ClientCnxn(hp, 30000, new ZKClientConfig(), mock(Watcher.class), socket, false));
}

// T1.3 - sessionTimeout = 0 → costruzione riuscita
@Test
@Timeout(5)
public void Constructor_SessionTimeoutZero_ObjectCreated() throws IOException {
    HostProvider hp = mock(HostProvider.class);
    when(hp.size()).thenReturn(1);
    ClientCnxn cnxn = new ClientCnxn(hp, 0, new ZKClientConfig(), mock(Watcher.class), mock(ClientCnxnSocket.class), false);
    assertEquals(States.CONNECTING, cnxn.state);
}

// T1.4 - sessionTimeout negativo → nessuna eccezione (bug: mancata validazione)
@Test
@Timeout(5)
public void Constructor_NegativeSessionTimeout_NoExceptionThrown() throws IOException {
    HostProvider hp = mock(HostProvider.class);
    when(hp.size()).thenReturn(1);
    assertDoesNotThrow(() ->
            new ClientCnxn(hp, -1, new ZKClientConfig(), mock(Watcher.class), mock(ClientCnxnSocket.class), false));
}

// T1.5 - sessionPasswd = null → accettato
@Test
@Timeout(5)
public void Constructor_NullSessionPasswd_ObjectCreated() throws IOException {
    HostProvider hp = mock(HostProvider.class);
    when(hp.size()).thenReturn(1);
    ClientCnxn cnxn = new ClientCnxn(hp, 30000, new ZKClientConfig(), mock(Watcher.class),
            mock(ClientCnxnSocket.class), 0L, null, false);
    assertNotNull(cnxn);
}
```

---

## 2. Metodo: `isInEventThread()` (static)

**Cosa fa (Black-Box):** Metodo di utilità per i test interni. Restituisce `true` se il thread corrente è un `EventThread` di ZooKeeper, `false` altrimenti. Utile per rilevare errori di programmazione concorrente.

### Step 1 – Identificazione dei Domini di Input

Nessun parametro. L'unico dominio è lo stato del thread corrente.

### Step 2 – Classi di Equivalenza

- CE1: `{thread_normale}` — Il thread chiamante non è un `EventThread`
- CE2: `{event_thread}` — Il thread chiamante è un `EventThread` (richiede setup avanzato)

### Step 3 – Combinazione

Dominio unico (il tipo di thread); nessun prodotto cartesiano da calcolare. CE2 (`{event_thread}`) non è testabile in isolamento senza avviare thread reali di ZooKeeper: è candidato a test di integrazione.

### Step 4 – Suite di Test Iniziale (Progettazione Black-Box)

| ID | CE | Stato Thread | Output Atteso | Esito | Motivazione |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **T2.1** | CE1 | Thread JUnit (non EventThread) | `false` | **Passato** | Il thread di test non è mai un `EventThread`. Verifica il caso nominale del metodo. |

### Step 5 – Codice Java

```java
// T2.1 - Da un thread normale: deve restituire false
@Test
@Timeout(5)
public void IsInEventThread_FromTestThread_ReturnsFalse() {
    assertFalse(ClientCnxn.isInEventThread());
}
```

---

## 3. Metodo: `getXid()`

**Cosa fa (Black-Box):** Genera identificatori di transazione (`xid`) progressivi e unici. Gestisce il wrap-around: quando raggiunge `Integer.MAX_VALUE`, ricomincia da `1` (saltando `0` e i valori riservati negativi `-1`, `-2`, `-4`, `-8`).

### Step 1 – Identificazione dei Domini di Input

Nessun parametro formale. Il dominio è il valore corrente del campo interno `xid`.

### Step 2 – Classi di Equivalenza e Boundary Values

**S1 – Valore corrente di `xid`:**
- CE1: `{xid in [1, MAX_VALUE-1]}` — Range normale
- CE2: `{xid == Integer.MAX_VALUE}` — Confine del wrap-around

**Boundary Values:**
- BV1: `xid = 1` (minimo utilizzabile)
- BV2: `xid = Integer.MAX_VALUE` (trigger del wrap)

### Step 3 – Combinazione

Dominio unico (S1); nessun prodotto cartesiano multi-dimensionale. Si scelgono i valori BV1 e BV2 come punti di test, più un caso intermedio per verificare la sequenzialità.

| # | S1 (xid) | Output Atteso |
| :--- | :--- | :--- |
| C1 | CE1, BV1 (`xid = 1`) | Restituisce 1, xid diventa 2 |
| C2 | CE1 (`xid = 5`, tre chiamate) | Restituisce 5, 6, 7 sequenzialmente |
| C3 | CE2, BV2 (`xid = MAX_VALUE`) | Restituisce 1 (wrap-around), xid diventa 2 |

### Step 4 – Suite di Test Iniziale (Progettazione Black-Box)

| ID | CE | S1 (xid iniziale) | Output Atteso | Esito | Motivazione |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **T3.1** | C1: CE1, BV1 | `xid = 1` | Ritorna `1`, xid diventa `2` | **Passato** | BV1: minimo utilizzabile. Verifica il comportamento di incremento nominale. |
| **T3.2** | C2: CE1 | `xid = 5`, tre chiamate | Ritorna `5`, poi `6`, poi `7` | **Passato** | CE1: verifica la sequenzialità nelle chiamate successive. |
| **T3.3** | C3: CE2, BV2 | `xid = Integer.MAX_VALUE` | Ritorna `1` (wrap-around) | **Passato** | BV2: confine critico. Verifica che il wrap-around sia corretto. |

### Step 5 – Codice Java

```java
// T3.1 - Incremento nominale da xid = 1
@Test
@Timeout(5)
public void GetXid_NominalIncrement_ReturnsCurrentAndIncrements() throws IOException {
    ClientCnxn cnxn = buildCnxn(30000, 1);
    cnxn.xid = 1;
    assertEquals(1, cnxn.getXid());
    assertEquals(2, cnxn.xid);
}

// T3.2 - Tre chiamate consecutive
@Test
@Timeout(5)
public void GetXid_ConsecutiveCalls_AreSequential() throws IOException {
    ClientCnxn cnxn = buildCnxn(30000, 1);
    cnxn.xid = 5;
    assertEquals(5, cnxn.getXid());
    assertEquals(6, cnxn.getXid());
    assertEquals(7, cnxn.getXid());
}

// T3.3 - Wrap-around a MAX_VALUE
@Test
@Timeout(5)
public void GetXid_AtMaxValue_WrapsToOne() throws IOException {
    ClientCnxn cnxn = buildCnxn(30000, 1);
    cnxn.xid = Integer.MAX_VALUE;
    assertEquals(1, cnxn.getXid());
}
```

---

## 4. Metodo: `addAuthInfo(String scheme, byte[] auth)`

**Cosa fa (Black-Box):** Accoda credenziali di autenticazione da presentare al server. Se la connessione non è più attiva (`state` non è alive), il metodo ritorna silenziosamente senza fare nulla. Non esegue validazione sui parametri: `null` e array vuoti vengono accettati e passati internamente senza eccezioni.

### Step 1 – Identificazione dei Domini di Input

| # | Parametro | Tipo | Provenienza |
| :--- | :--- | :--- | :--- |
| S1 | Stato connessione | `States` (interno) | Precondizione |
| D1 | `String scheme` | Stringa | Parametro formale |
| D2 | `byte[] auth` | Array di byte | Parametro formale |

### Step 2 – Classi di Equivalenza

**S1 – Stato connessione:**
- CE1: `{stato_alive}` — Connessione attiva (es. `CONNECTING`, `CONNECTED`)
- CE2: `{stato_not_alive}` — Connessione chiusa (`CLOSED`, `AUTH_FAILED`)

**D1 – `String scheme`:**
- CE3: `{null}` — Schema non fornito
- CE4: `{valid}` — Schema valido (es. `"digest"`)

**D2 – `byte[] auth`:**
- CE5: `{null}` — Array nullo
- CE6: `{populated}` — Array con dati

### Step 3 – Combinazione

Il prodotto cartesiano completo S1 × D1 × D2 produce 2 × 2 × 2 = 8 combinazioni. Vengono potate le combinazioni ridondanti: quando S1=CE2 (stato not alive), il metodo esce subito alla guardia senza nemmeno leggere D1 e D2, quindi testare tutte le varianti di D1 × D2 con stato CLOSED sarebbe ridondante. Si scelgono le combinazioni più significative:

| # | S1 | D1 | D2 | Note |
| :--- | :--- | :--- | :--- | :--- |
| C1 | CE2 (CLOSED) | CE4 (valid) | CE6 (populated) | Testa la guardia `isAlive()` |
| C2 | CE1 (CONNECTING) | CE3 (null) | CE6 (populated) | Testa mancanza validazione su scheme |
| C3 | CE1 (CONNECTING) | CE4 (valid) | CE5 (null) | Testa mancanza validazione su auth |
| C4 | CE1 (CONNECTING) | CE4 (valid) | CE6 (populated) | Caso nominale |

*Combinazioni eliminate:* CE2 × CE3 × ... (stato CLOSED + null params): ridondante, la guardia esce prima di leggere i parametri; CE1 × CE3 × CE5 (null × null): caso patologico, incluso nei test individuali T4.2 e T4.3.

### Step 4 – Suite di Test Iniziale (Progettazione Black-Box)

| ID | CE | S1 | D1 | D2 | Output Atteso | Esito | Motivazione |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **T4.1** | C1: CE2×CE4×CE6 | `CLOSED` | `"digest"` | `{0x01}` | Ritorno silenzioso; `authInfo` invariata | **Passato** | CE2: guardia `if (!state.isAlive()) return;` attiva. |
| **T4.2** | C2: CE1×CE3×CE6 | `CONNECTING` | `null` | `{0x01}` | Entry aggiunta ad `authInfo` (nessuna eccezione) | **Passato** | CE3: nessuna validazione su `scheme`. |
| **T4.3** | C3: CE1×CE4×CE5 | `CONNECTING` | `"digest"` | `null` | Entry aggiunta ad `authInfo` (nessuna eccezione) | **Passato** | CE5: nessuna validazione su `auth`. |
| **T4.4** | C4: CE1×CE4×CE6 | `CONNECTING` | `"digest"` | `{0x01}` | Entry aggiunta; pacchetto accodato | **Passato** | Caso nominale: credenziali valide con connessione attiva. |

### Step 5 – Codice Java

```java
// T4.1 - Stato CLOSED: metodo è no-op
@Test
@Timeout(5)
public void AddAuthInfo_WhenStateClosed_IsNoOp() throws Exception {
    ClientCnxn cnxn = buildCnxn(30000, 1);
    cnxn.state = States.CLOSED;
    cnxn.addAuthInfo("digest", new byte[]{0x01});
    Field authInfoField = ClientCnxn.class.getDeclaredField("authInfo");
    authInfoField.setAccessible(true);
    CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) authInfoField.get(cnxn);
    assertEquals(0, authInfo.size(), "Nessuna entry deve essere aggiunta quando lo stato è CLOSED");
}

// T4.2 - scheme = null: accettato senza eccezione
@Test
@Timeout(5)
public void AddAuthInfo_NullScheme_AcceptedWithoutException() throws Exception {
    ClientCnxn cnxn = buildCnxn(30000, 1);
    assertDoesNotThrow(() -> cnxn.addAuthInfo(null, new byte[]{0x01}));
    Field authInfoField = ClientCnxn.class.getDeclaredField("authInfo");
    authInfoField.setAccessible(true);
    CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) authInfoField.get(cnxn);
    assertEquals(1, authInfo.size());
}

// T4.3 - auth = null: accettato senza eccezione
@Test
@Timeout(5)
public void AddAuthInfo_NullAuth_AcceptedWithoutException() throws Exception {
    ClientCnxn cnxn = buildCnxn(30000, 1);
    assertDoesNotThrow(() -> cnxn.addAuthInfo("digest", null));
}

// T4.4 - Caso nominale: scheme e auth validi
@Test
@Timeout(5)
public void AddAuthInfo_ValidParams_EntryAddedToAuthInfo() throws Exception {
    ClientCnxn cnxn = buildCnxn(30000, 1);
    cnxn.addAuthInfo("digest", new byte[]{0x01, 0x02});
    Field authInfoField = ClientCnxn.class.getDeclaredField("authInfo");
    authInfoField.setAccessible(true);
    CopyOnWriteArraySet<?> authInfo = (CopyOnWriteArraySet<?>) authInfoField.get(cnxn);
    assertEquals(1, authInfo.size(), "L'entry di autenticazione deve essere presente");
}
```

---

## 5. Metodi: `submitRequest(...)` e `close()` — Analisi Teorica

Questi metodi richiedono una connessione di rete reale e thread attivi per funzionare. Nell'ambito del **Black-Box Unit Testing**, non sono testabili in isolamento senza simulare l'intera infrastruttura di rete ZooKeeper.

**Analisi teorica di `submitRequest`:**
- `STATE_CLOSED` → il pacchetto è già morto via `conLossPacket`, il `submitRequest` non deve bloccare indefinitamente.
- `STATE_CONNECTED` → il pacchetto viene accodato e attende la risposta dal server.

**Analisi teorica di `close()`:**
- Su connessione `CLOSED`: ritorno silenzioso (idempotente).
- Su connessione `ALIVE`: invia `closeSession`, poi chiama `disconnect()` che fa join sui thread.

Questi scenari sono candidati per **Integration Tests** (`*IT.java`) che richiedono un server ZooKeeper embedded (`ZooKeeperTestBase`).

---

## 6. Classe Interna: `Packet`

**Cosa fa (Black-Box):** Contenitore immutabile per i dati di una singola richiesta: header, body, callback, e i due percorsi (`clientPath` e `serverPath`). I metodi `createBB()` serializza la richiesta in un `ByteBuffer`; `toString()` produce una stringa di debug (rimuovendo i newline).

### Step 1 – Identificazione dei Domini di Input

**Per `createBB()`:**
- D1: `requestHeader` (null o valorizzato)
- D2: `request` (null, `ConnectRequest`, altro `Record`)

**Per `toString()`:**
- S1: Stato dei campi (valorizzati o null)

### Step 2 – Classi di Equivalenza

**D1:** CE1 `{null}`, CE2 `{valorizzato}`
**D2:** CE3 `{null}`, CE4 `{ConnectRequest}`, CE5 `{altro Record}`
**S1 (toString):** CE6 `{campi con valori}`, CE7 `{campi null}`

### Step 4 – Suite di Test Finale

| ID | Input | Output Atteso | Esito | Motivazione |
| :--- | :--- | :--- | :--- | :--- |
| **T5.1** | `createBB()` con `requestHeader=null`, `request=null` | `bb` non null, capacità ≥ 4 | **Passato** | Anche senza richiesta, viene scritto il prefisso di lunghezza. |
| **T5.2** | `toString()` con `clientPath="/a"`, `serverPath="/b"` | Stringa contiene `clientPath:/a` e `serverPath:/b` | **Passato** | Verifica il formato del log. |
| **T5.3** | `toString()` con `finished=false` | Stringa contiene `finished:false` | **Passato** | Verifica serializzazione del flag. |
| **T5.4** | `toString()` con valori contenenti newline | Stringa NON contiene `\n` | **Passato** | Verifica che i newline vengano rimossi (`replaceAll("\r*\n+", " ")`). |

### Step 5 – Codice Java

```java
// T5.1 - createBB con header e request null: ByteBuffer viene comunque creato
@Test
@Timeout(5)
public void Packet_CreateBB_WithNullHeaderAndRequest_BufferNotNull() {
    ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
    p.createBB();
    assertNotNull(p.bb, "Il ByteBuffer deve essere creato anche senza request");
}

// T5.2 - toString: contiene clientPath e serverPath
@Test
@Timeout(5)
public void Packet_ToString_ContainsClientAndServerPath() {
    ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
    p.clientPath = "/utenti";
    p.serverPath = "/miaApp/utenti";
    String result = p.toString();
    assertTrue(result.contains("clientPath:/utenti"));
    assertTrue(result.contains("serverPath:/miaApp/utenti"));
}

// T5.3 - toString: contiene finished
@Test
@Timeout(5)
public void Packet_ToString_ContainsFinishedFlag() {
    ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
    p.finished = false;
    assertTrue(p.toString().contains("finished:false"));
}

// T5.4 - toString: rimuove i newline
@Test
@Timeout(5)
public void Packet_ToString_StripsNewlines() {
    ClientCnxn.Packet p = new ClientCnxn.Packet(null, new ReplyHeader(), null, null, null);
    p.clientPath = "/a\nb";
    String result = p.toString();
    assertFalse(result.contains("\n"), "I newline devono essere rimossi da toString()");
}
```

---

## 📝 Note Metodologiche sulle Esclusioni dall'Analisi

### 1. Esclusione dei Pure Getters
`getSessionId()`, `getSessionPasswd()`, `getSessionTimeout()`, `getLastZxid()` sono accessori senza logica computazionale. Il loro corretto funzionamento è verificato implicitamente dai test del costruttore.

### 2. Esclusione dei Metodi Network-Bound
`submitRequest()`, `close()`, `disconnect()`, `start()` richiedono thread attivi e connessione reale. Sono candidati per Integration Tests, non per Unit Tests con Category Partition.

### 3. Strategia con Mockito (Scelte Concettuali)

L'uso di Mockito in un test di natura Black-Box (Category Partition) rappresenta un'eccezione motivata dall'alto accoppiamento infrastrutturale della classe.

**Perché usiamo Mockito?**
Se usassimo istanze reali per i parametri del costruttore di `ClientCnxn`, finiremmo per fare *Integration Testing*: il codice proverebbe ad aprire socket di rete reali, avviare thread e cercare un server ZooKeeper, portando a test lenti, instabili (flaky) e dipendenti dall'ambiente. Il nostro obiettivo nel *Unit Testing* è isolare `ClientCnxn` per testare solo la sua logica interna (es. calcolo dei timeout, gestione dello stato, validazione input).

**Come lo utilizziamo (Stubbing vs Mocking):**
In questa suite, usiamo Mockito quasi esclusivamente per fare **Stubbing** (fornire risposte predefinite) e non per fare *Mocking* (verificare interazioni comportamentali tramite `verify()`). 

Nello specifico, per costruire un'istanza reale di `ClientCnxn` nei test, passiamo:
- `HostProvider` (interfaccia): Usiamo `mock(HostProvider.class)` per controllarne il comportamento. In particolare, facciamo stubbing di `when(hp.size()).thenReturn(...)` per guidare il partizionamento del dominio e testare il calcolo di `connectTimeout`. Questo ci ha permesso di isolare il bug della divisione per zero.
- `ClientCnxnSocket` (classe astratta): Usiamo `mock(ClientCnxnSocket.class)` per disinnescare la logica di basso livello (NIO/Netty). Non vogliamo che provi a inviare byte reali.
- `Watcher` (interfaccia): Forniamo un dummy `mock(Watcher.class)` solo per soddisfare la firma del costruttore, poiché i nostri test non innescano eventi di watch.
- `ZKClientConfig` (classe concreta): Usiamo un'istanza reale `new ZKClientConfig()` perché è un semplice contenitore di configurazioni in-memory, innocuo da istanziare.

**Uso della Reflection come Oracolo Black-Box:**
Dal momento che stiamo testando metodi (come `addAuthInfo`) che non restituiscono valori ma alterano lo stato interno dell'oggetto (es. accodano elementi nella coda `authInfo`), e non potendo verificare interazioni di rete, utilizziamo la Java Reflection (`Field.setAccessible(true)`) nei test JUnit. Questo ci funge da "oracolo", permettendoci di ispezionare le variabili package-private (come `state`, `xid`, `authInfo`) per validare che l'operazione di input (la "causa") abbia prodotto il corretto mutamento di stato (l'"effetto").
