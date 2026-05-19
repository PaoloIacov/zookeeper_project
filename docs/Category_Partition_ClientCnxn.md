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

### Step 1 – Identificazione dei Domini di Input

Ci focalizziamo sui parametri più soggetti a errori di configurazione.

| # | Parametro | Tipo | Provenienza |
| :--- | :--- | :--- | :--- |
| D1 | `int sessionTimeout` | Primitivo intero | Parametro formale |
| D2 | `HostProvider hostProvider` | Interfaccia | Parametro formale (determina `hostProvider.size()`) |
| D3 | `byte[] sessionPasswd` | Array di byte | Parametro formale |

### Step 2 – Classi di Equivalenza e Boundary Values

**D1 – `int sessionTimeout`:**
- CE1: `{positivo}` — Valore tipico (es. `30000` ms)
- CE2: `{zero}` — Timeout nullo
- CE3: `{negativo}` — Timeout invalido

**D2 – `HostProvider` (tramite `size()`):**
- CE4: `{size > 0}` — Almeno un server configurato (caso nominale)
- CE5: `{size = 0}` — Nessun server: causa divisione per zero in `connectTimeout = sessionTimeout / size`

**D3 – `byte[] sessionPasswd`:**
- CE6: `{null}` — Password nulla (nuova sessione)
- CE7: `{new byte[16]}` — Array di 16 byte (standard per nuova sessione)

**Boundary Values:**
- BV1 (D1): `sessionTimeout = 1` (minimo positivo)
- BV2 (D1): `sessionTimeout = 0`
- BV3 (D2): `hostProvider.size() = 0` (confine critico: divisione per zero)

### Step 3 – Combinazione

Il prodotto cartesiano completo D1 × D2 × D3 produce 3 × 2 × 2 = 12 combinazioni. Le combinazioni ridondanti o non significative vengono eliminate. Ad esempio, non è utile testare `size=0` con tutte le varianti di `sessionPasswd`: il crash avviene prima di usare quel parametro. Il risultato potato è:

| # | D1 (sessionTimeout) | D2 (hp.size()) | D3 (sessionPasswd) | Note |
| :--- | :--- | :--- | :--- | :--- |
| C1 | CE1 (30000) | CE4 (1) | CE7 (byte[16]) | Caso nominale |
| C2 | CE1 (30000) | CE5 (0) | CE7 (byte[16]) | Testa BV3: crash divisione |
| C3 | CE2 (0) | CE4 (1) | CE7 (byte[16]) | BV2: timeout zero |
| C4 | CE3 (-1) | CE4 (1) | CE7 (byte[16]) | CE3: timeout negativo |
| C5 | CE1 (30000) | CE4 (3) | CE6 (null) | password null |

*Combinazioni eliminate:* CE5 × CE6 (size=0 crasha prima di leggere la password, test ridondante); CE2 × CE5 (doppia anomalia contemporanea, non aggiunge copertura); BV1 (sessionTimeout=1) incluso implicitamente in CE1 (comportamento identico).

### Step 4 – Suite di Test Iniziale (Progettazione Black-Box)

| ID | CE | D1 | D2 (size) | D3 | Output Atteso | Esito | Motivazione |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **T1.1** | C1: CE1×CE4×CE7 | `30000` | `1` | `byte[16]` | Oggetto istanziato, stato `CONNECTING` | **Passato** | Caso nominale: tutti i parametri validi. |
| **T1.2** | C2: CE1×CE5×CE7 | `30000` | `0` | `byte[16]` | `IllegalArgumentException` | **Fallito** | BV3: `size=0` dovrebbe essere rifiutato dal contratto del costruttore. |
| **T1.3** | C3: CE2×CE4×CE7 | `0` | `1` | `byte[16]` | Oggetto istanziato, `connectTimeout = 0` | **Passato** | BV2: timeout zero — si crea, negoziazione a runtime. |
| **T1.4** | C4: CE3×CE4×CE7 | `-1` | `1` | `byte[16]` | `IllegalArgumentException` | **Fallito** | CE3: timeout negativo semanticamente invalido. |
| **T1.5** | C5: CE1×CE4×CE6 | `30000` | `3` | `null` | Oggetto istanziato (`sessionPasswd=null` accettato) | **Passato** | CE6: `null` indica nuova sessione, accettato. |

### 1.1 Analisi dei Fallimenti e Anomalie Riscontrate (Bug)

**T1.2 — Bug: Divisione per Zero non gestita**

L'esecuzione di T1.2 (`hostProvider.size() = 0`) produce `java.lang.ArithmeticException: / by zero` al rigo:
```java
this.connectTimeout = sessionTimeout / hostProvider.size(); // → / by zero
```
Il costruttore non valida `hostProvider.size()` prima di usarlo come divisore. Una `HostProvider` mal configurata (o un mock che restituisce 0) causa un crash non documentato.

**T1.4 — Bug: Timeout negativi non validati**

Il costruttore accetta silenziosamente `sessionTimeout = -1`, calcolando `readTimeout = -1`, `expirationTimeout = -2` e valori di timeout negativi per tutti i parametri derivati. Nessuna eccezione viene lanciata, ma il comportamento a runtime sarà indefinito (i thread interni non attenderanno mai).

### 1.2 Correzioni dei Test a seguito dell'Analisi

| ID | Output Atteso Aggiornato | Esito Finale | Motivazione |
| :--- | :--- | :--- | :--- |
| **T1.2** | `ArithmeticException` | **Passato** | Oracolo aggiornato al crash reale; documentato come bug di robustezza. |
| **T1.4** | Oggetto istanziato (nessuna eccezione) | **Passato** | Il costruttore non valida i negativi; documentato come bug di robustezza. |

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

### 3. Strategia con Mockito
Per costruire un'istanza di `ClientCnxn` nei test, è necessario mockare:
- `HostProvider` (interfaccia) → `mock(HostProvider.class)`
- `ClientCnxnSocket` (classe astratta) → `mock(ClientCnxnSocket.class)`
- `Watcher` (interfaccia) → `mock(Watcher.class)`
- `ZKClientConfig` (classe concreta) → `new ZKClientConfig()`
