package org.apache.zookeeper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import javax.security.auth.login.LoginException;
import javax.security.sasl.SaslException;
import org.apache.jute.BinaryInputArchive;
import org.apache.jute.BinaryOutputArchive;
import org.apache.jute.Record;
import org.apache.zookeeper.AsyncCallback.ACLCallback;
import org.apache.zookeeper.AsyncCallback.AllChildrenNumberCallback;
import org.apache.zookeeper.AsyncCallback.Children2Callback;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.Create2Callback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.EphemeralsCallback;
import org.apache.zookeeper.AsyncCallback.MultiCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.OpResult.ErrorResult;
import org.apache.zookeeper.Watcher.Event;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.OpCode;
import org.apache.zookeeper.ZooKeeper.States;
import org.apache.zookeeper.ZooKeeper.WatchRegistration;
import org.apache.zookeeper.client.FourLetterWordMain;
import org.apache.zookeeper.client.HostProvider;
import org.apache.zookeeper.client.ZKClientConfig;
import org.apache.zookeeper.client.ZooKeeperSaslClient;
import org.apache.zookeeper.common.Time;
import org.apache.zookeeper.common.X509Exception;
import org.apache.zookeeper.proto.AuthPacket;
import org.apache.zookeeper.proto.ConnectRequest;
import org.apache.zookeeper.proto.Create2Response;
import org.apache.zookeeper.proto.CreateResponse;
import org.apache.zookeeper.proto.ExistsResponse;
import org.apache.zookeeper.proto.GetACLResponse;
import org.apache.zookeeper.proto.GetAllChildrenNumberResponse;
import org.apache.zookeeper.proto.GetChildren2Response;
import org.apache.zookeeper.proto.GetChildrenResponse;
import org.apache.zookeeper.proto.GetDataResponse;
import org.apache.zookeeper.proto.GetEphemeralsResponse;
import org.apache.zookeeper.proto.GetSASLRequest;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.proto.RequestHeader;
import org.apache.zookeeper.proto.SetACLResponse;
import org.apache.zookeeper.proto.SetDataResponse;
import org.apache.zookeeper.proto.SetWatches;
import org.apache.zookeeper.proto.SetWatches2;
import org.apache.zookeeper.proto.WatcherEvent;
import org.apache.zookeeper.server.ByteBufferInputStream;
import org.apache.zookeeper.server.ZooKeeperThread;
import org.apache.zookeeper.server.ZooTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ClientCnxn_C4 {

    private static final Logger LOG = LoggerFactory.getLogger(ClientCnxn_C4.class);

    private static final int SET_WATCHES_MAX_LENGTH = 128 * 1024;

    public static final int NOTIFICATION_XID = -1;
    public static final int PING_XID = -2;
    public static final int AUTHPACKET_XID = -4;
    public static final int SET_WATCHES_XID = -8;

    static class AuthData {
        String scheme;
        byte[] data;

        AuthData(String scheme, byte[] data) {
            this.scheme = scheme;
            this.data = data != null ? Arrays.copyOf(data, data.length) : null;
        }
    }

    private final CopyOnWriteArraySet<AuthData> authInfo = new CopyOnWriteArraySet<>();
    private final Queue<Packet> pendingQueue = new ArrayDeque<>();
    private final LinkedBlockingDeque<Packet> outgoingQueue = new LinkedBlockingDeque<>();

    private int connectTimeout;
    private volatile int negotiatedSessionTimeout;
    private int readTimeout;
    private int expirationTimeout;
    private final int sessionTimeout;
    private final long newSessionTimeout;
    private final ZKWatchManager watchManager;
    private long sessionId;
    private byte[] sessionPasswd;
    private boolean readOnly;
    final SendThread sendThread;
    final EventThread eventThread;
    private volatile boolean closing = false;
    private final HostProvider hostProvider;
    volatile boolean seenRwServerBefore = false;
    private final ZKClientConfig clientConfig;
    private long requestTimeout;
    private volatile long lastZxid;
    protected int xid = 1;
    volatile States state = States.NOT_CONNECTED;

    ZKWatchManager getWatcherManager() {
        return watchManager;
    }

    public long getSessionId() {
        return sessionId;
    }

    public byte[] getSessionPasswd() {
        return sessionPasswd != null ? Arrays.copyOf(sessionPasswd, sessionPasswd.length) : null;
    }

    public int getSessionTimeout() {
        return negotiatedSessionTimeout;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        SocketAddress local = sendThread.getLocalSocketAddress();
        SocketAddress remote = sendThread.getRemoteSocketAddress();
        sb.append("sessionid:0x").append(Long.toHexString(getSessionId()))
          .append(" local:").append(local)
          .append(" remoteserver:").append(remote)
          .append(" lastZxid:").append(lastZxid)
          .append(" xid:").append(xid)
          .append(" sent:").append(sendThread.getSentCount())
          .append(" recv:").append(sendThread.getRecvCount())
          .append(" queuedpkts:").append(outgoingQueue.size())
          .append(" pendingresp:").append(pendingQueue.size())
          .append(" queuedevents:").append(eventThread.waitingEvents.size());
        return sb.toString();
    }

    static class Packet {
        RequestHeader requestHeader;
        ReplyHeader replyHeader;
        Record request;
        Record response;
        ByteBuffer bb;
        String clientPath;
        String serverPath;
        boolean finished;
        AsyncCallback cb;
        Object ctx;
        WatchRegistration watchRegistration;
        WatchDeregistration watchDeregistration;

        Packet(
            RequestHeader requestHeader,
            ReplyHeader replyHeader,
            Record request,
            Record response,
            WatchRegistration watchRegistration
        ) {
            this.requestHeader = requestHeader;
            this.replyHeader = replyHeader;
            this.request = request;
            this.response = response;
            this.watchRegistration = watchRegistration;
        }

        public void createBB() {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BinaryOutputArchive boa = BinaryOutputArchive.getArchive(baos);
                boa.writeInt(-1, "len"); 
                if (requestHeader != null) {
                    requestHeader.serialize(boa, "header");
                }
                if (request instanceof ConnectRequest) {
                    request.serialize(boa, "connect");
                } else if (request != null) {
                    request.serialize(boa, "request");
                }
                baos.close();
                this.bb = ByteBuffer.wrap(baos.toByteArray());
                this.bb.putInt(this.bb.capacity() - 4);
                this.bb.rewind();
            } catch (IOException e) {
                LOG.warn("Unexpected exception", e);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("clientPath:" + clientPath);
            sb.append(" serverPath:" + serverPath);
            sb.append(" finished:" + finished);
            sb.append(" header:: " + requestHeader);
            sb.append(" replyHeader:: " + replyHeader);
            sb.append(" request:: " + request);
            sb.append(" response:: " + response);
            return sb.toString().replaceAll("\r*\n+", " ");
        }
    }

    public ClientCnxn_C4(
        HostProvider hostProvider,
        int sessionTimeout,
        ZKClientConfig clientConfig,
        Watcher defaultWatcher,
        ClientCnxnSocket clientCnxnSocket,
        boolean canBeReadOnly
    ) throws IOException {
        this(
            hostProvider,
            sessionTimeout,
            clientConfig,
            defaultWatcher,
            clientCnxnSocket,
            0,
            new byte[16],
            canBeReadOnly);
    }

    public ClientCnxn_C4(
        HostProvider hostProvider,
        int sessionTimeout,
        ZKClientConfig clientConfig,
        Watcher defaultWatcher,
        ClientCnxnSocket clientCnxnSocket,
        long sessionId,
        byte[] sessionPasswd,
        boolean canBeReadOnly
    ) throws IOException {
        this(hostProvider, sessionTimeout, Long.MAX_VALUE, clientConfig, defaultWatcher, clientCnxnSocket, sessionId, sessionPasswd, canBeReadOnly);
    }

    public ClientCnxn_C4(
            HostProvider hostProvider,
            int sessionTimeout,
            long newSessionTimeout,
            ZKClientConfig clientConfig,
            Watcher defaultWatcher,
            ClientCnxnSocket clientCnxnSocket,
            long sessionId,
            byte[] sessionPasswd,
            boolean canBeReadOnly
    ) throws IOException {
        this.hostProvider = hostProvider;
        this.sessionTimeout = sessionTimeout;
        this.clientConfig = clientConfig;
        this.sessionId = sessionId;
        this.sessionPasswd = sessionPasswd != null ? Arrays.copyOf(sessionPasswd, sessionPasswd.length) : null;
        this.readOnly = canBeReadOnly;

        this.watchManager = new ZKWatchManager(
                clientConfig.getBoolean(ZKClientConfig.DISABLE_AUTO_WATCH_RESET),
                defaultWatcher);

        this.connectTimeout = sessionTimeout / hostProvider.size();
        this.readTimeout = sessionTimeout * 2 / 3;
        this.expirationTimeout = sessionTimeout * 4 / 3;
        this.newSessionTimeout = newSessionTimeout == 0 ? expirationTimeout : newSessionTimeout;

        this.sendThread = new SendThread(clientCnxnSocket);
        this.eventThread = new EventThread();
        initRequestTimeout();
    }

    public void start() {
        sendThread.start();
        eventThread.start();
    }

    private Object eventOfDeath = new Object();

    private static class WatcherSetEventPair {
        private final Set<Watcher> watchers;
        private final WatchedEvent event;

        public WatcherSetEventPair(Set<Watcher> watchers, WatchedEvent event) {
            this.watchers = watchers;
            this.event = event;
        }
    }

    private static String makeThreadName(String suffix) {
        String name = Thread.currentThread().getName().replaceAll("-EventThread", "");
        return name + suffix;
    }

    public static boolean isInEventThread() {
        return Thread.currentThread() instanceof EventThread;
    }

    class EventThread extends ZooKeeperThread {

        private final LinkedBlockingQueue<Object> waitingEvents = new LinkedBlockingQueue<>();
        private volatile KeeperState sessionState = KeeperState.Disconnected;
        private volatile boolean wasKilled = false;
        private volatile boolean isRunning = false;

        EventThread() {
            super(makeThreadName("-EventThread"));
            setDaemon(true);
        }

        public void queueEvent(WatchedEvent event) {
            queueEvent(event, null);
        }

        private void queueEvent(WatchedEvent event, Set<Watcher> materializedWatchers) {
            if (event.getType() == EventType.None && sessionState == event.getState()) {
                return;
            }
            sessionState = event.getState();
            final Set<Watcher> watchers;
            if (materializedWatchers == null) {
                watchers = watchManager.materialize(event.getState(), event.getType(), event.getPath());
            } else {
                watchers = new HashSet<>(materializedWatchers);
            }
            WatcherSetEventPair pair = new WatcherSetEventPair(watchers, event);
            waitingEvents.add(pair);
        }

        public void queueCallback(AsyncCallback cb, int rc, String path, Object ctx) {
            waitingEvents.add(new LocalCallback(cb, rc, path, ctx));
        }

        @SuppressFBWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")
        public void queuePacket(Packet packet) {
            if (wasKilled) {
                synchronized (waitingEvents) {
                    if (isRunning) {
                        waitingEvents.add(packet);
                    } else {
                        processEvent(packet);
                    }
                }
            } else {
                waitingEvents.add(packet);
            }
        }

        public void queueEventOfDeath() {
            waitingEvents.add(eventOfDeath);
        }

        @Override
        @SuppressFBWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")
        public void run() {
            try {
                isRunning = true;
                while (true) {
                    Object event = waitingEvents.take();
                    if (event == eventOfDeath) {
                        wasKilled = true;
                    } else {
                        processEvent(event);
                    }
                    if (wasKilled) {
                        synchronized (waitingEvents) {
                            if (waitingEvents.isEmpty()) {
                                isRunning = false;
                                break;
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                LOG.error("Event thread exiting due to interruption", e);
            }

            if (LOG.isInfoEnabled()) {
                LOG.info("EventThread shut down for session: 0x{}", Long.toHexString(getSessionId()));
            }
        }

        private void processEvent(Object event) {
            try {
                if (event instanceof WatcherSetEventPair) {
                    processWatcherSetEventPair((WatcherSetEventPair) event);
                } else if (event instanceof LocalCallback) {
                    processLocalCallback((LocalCallback) event);
                } else {
                    processPacket((Packet) event);
                }
            } catch (Exception e) {
                LOG.error("Unexpected exception", e);
            }
        }

        private void processWatcherSetEventPair(WatcherSetEventPair pair) {
            for (Watcher watcher : pair.watchers) {
                try {
                    watcher.process(pair.event);
                } catch (Exception e) {
                    LOG.error("Error while calling watcher.", e);
                }
            }
        }

        private void processLocalCallback(LocalCallback lcb) {
            if (lcb.cb instanceof StatCallback) {
                ((StatCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, null);
            } else if (lcb.cb instanceof DataCallback) {
                ((DataCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, null, null);
            } else if (lcb.cb instanceof ACLCallback) {
                ((ACLCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, null, null);
            } else if (lcb.cb instanceof ChildrenCallback) {
                ((ChildrenCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, null);
            } else if (lcb.cb instanceof Children2Callback) {
                ((Children2Callback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, null, null);
            } else if (lcb.cb instanceof StringCallback) {
                ((StringCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, null);
            } else if (lcb.cb instanceof AsyncCallback.EphemeralsCallback) {
                ((AsyncCallback.EphemeralsCallback) lcb.cb).processResult(lcb.rc, lcb.ctx, null);
            } else if (lcb.cb instanceof AsyncCallback.AllChildrenNumberCallback) {
                ((AsyncCallback.AllChildrenNumberCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, -1);
            } else if (lcb.cb instanceof AsyncCallback.MultiCallback) {
                ((AsyncCallback.MultiCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx, Collections.emptyList());
            } else {
                ((VoidCallback) lcb.cb).processResult(lcb.rc, lcb.path, lcb.ctx);
            }
        }

        private void processPacket(Packet p) {
            int rc = p.replyHeader.getErr() != 0 ? p.replyHeader.getErr() : 0;
            String clientPath = p.clientPath;
            
            if (p.cb == null) {
                LOG.warn("Somehow a null cb got to EventThread!");
            } else if (p.response instanceof ExistsResponse
                       || p.response instanceof SetDataResponse
                       || p.response instanceof SetACLResponse) {
                processStatCallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof GetDataResponse) {
                processGetDataCallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof GetACLResponse) {
                processGetACLCallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof GetChildrenResponse) {
                processGetChildrenCallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof GetAllChildrenNumberResponse) {
                processGetAllChildrenNumberCallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof GetChildren2Response) {
                processGetChildren2CallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof CreateResponse) {
                processCreateCallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof Create2Response) {
                processCreate2CallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof MultiResponse) {
                processMultiCallbackPacket(p, rc, clientPath);
            } else if (p.response instanceof GetEphemeralsResponse) {
                processGetEphemeralsCallbackPacket(p, rc);
            } else if (p.cb instanceof VoidCallback) {
                VoidCallback cb = (VoidCallback) p.cb;
                cb.processResult(rc, clientPath, p.ctx);
            }
        }

        private void processStatCallbackPacket(Packet p, int rc, String clientPath) {
            StatCallback cb = (StatCallback) p.cb;
            if (rc == Code.OK.intValue()) {
                if (p.response instanceof ExistsResponse) {
                    cb.processResult(rc, clientPath, p.ctx, ((ExistsResponse) p.response).getStat());
                } else if (p.response instanceof SetDataResponse) {
                    cb.processResult(rc, clientPath, p.ctx, ((SetDataResponse) p.response).getStat());
                } else if (p.response instanceof SetACLResponse) {
                    cb.processResult(rc, clientPath, p.ctx, ((SetACLResponse) p.response).getStat());
                }
            } else {
                cb.processResult(rc, clientPath, p.ctx, null);
            }
        }

        private void processGetDataCallbackPacket(Packet p, int rc, String clientPath) {
            DataCallback cb = (DataCallback) p.cb;
            GetDataResponse rsp = (GetDataResponse) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, clientPath, p.ctx, rsp.getData(), rsp.getStat());
            } else {
                cb.processResult(rc, clientPath, p.ctx, null, null);
            }
        }

        private void processGetACLCallbackPacket(Packet p, int rc, String clientPath) {
            ACLCallback cb = (ACLCallback) p.cb;
            GetACLResponse rsp = (GetACLResponse) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, clientPath, p.ctx, rsp.getAcl(), rsp.getStat());
            } else {
                cb.processResult(rc, clientPath, p.ctx, null, null);
            }
        }

        private void processGetChildrenCallbackPacket(Packet p, int rc, String clientPath) {
            ChildrenCallback cb = (ChildrenCallback) p.cb;
            GetChildrenResponse rsp = (GetChildrenResponse) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, clientPath, p.ctx, rsp.getChildren());
            } else {
                cb.processResult(rc, clientPath, p.ctx, null);
            }
        }

        private void processGetAllChildrenNumberCallbackPacket(Packet p, int rc, String clientPath) {
            AllChildrenNumberCallback cb = (AllChildrenNumberCallback) p.cb;
            GetAllChildrenNumberResponse rsp = (GetAllChildrenNumberResponse) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, clientPath, p.ctx, rsp.getTotalNumber());
            } else {
                cb.processResult(rc, clientPath, p.ctx, -1);
            }
        }

        private void processGetChildren2CallbackPacket(Packet p, int rc, String clientPath) {
            Children2Callback cb = (Children2Callback) p.cb;
            GetChildren2Response rsp = (GetChildren2Response) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, clientPath, p.ctx, rsp.getChildren(), rsp.getStat());
            } else {
                cb.processResult(rc, clientPath, p.ctx, null, null);
            }
        }

        private void processCreateCallbackPacket(Packet p, int rc, String clientPath) {
            StringCallback cb = (StringCallback) p.cb;
            CreateResponse rsp = (CreateResponse) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, clientPath, p.ctx, rsp.getPath());
            } else {
                cb.processResult(rc, clientPath, p.ctx, null);
            }
        }

        private void processCreate2CallbackPacket(Packet p, int rc, String clientPath) {
            Create2Callback cb = (Create2Callback) p.cb;
            Create2Response rsp = (Create2Response) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, clientPath, p.ctx, rsp.getPath(), rsp.getStat());
            } else {
                cb.processResult(rc, clientPath, p.ctx, null, null);
            }
        }

        private void processMultiCallbackPacket(Packet p, int rc, String clientPath) {
            MultiCallback cb = (MultiCallback) p.cb;
            MultiResponse rsp = (MultiResponse) p.response;
            if (rc == Code.OK.intValue()) {
                List<OpResult> results = rsp.getResultList();
                int newRc = rc;
                for (OpResult result : results) {
                    if (result instanceof ErrorResult
                        && KeeperException.Code.OK.intValue() != (newRc = ((ErrorResult) result).getErr())) {
                        break;
                    }
                }
                cb.processResult(newRc, clientPath, p.ctx, results);
            } else {
                cb.processResult(rc, clientPath, p.ctx, null);
            }
        }

        private void processGetEphemeralsCallbackPacket(Packet p, int rc) {
            EphemeralsCallback cb = (EphemeralsCallback) p.cb;
            GetEphemeralsResponse rsp = (GetEphemeralsResponse) p.response;
            if (rc == Code.OK.intValue()) {
                cb.processResult(rc, p.ctx, rsp.getEphemerals());
            } else {
                cb.processResult(rc, p.ctx, null);
            }
        }
    }

    protected void finishPacket(Packet p) {
        int err = p.replyHeader.getErr();
        if (p.watchRegistration != null) {
            p.watchRegistration.register(err);
        }
        if (p.watchDeregistration != null) {
            unregisterWatches(p, err);
        }

        if (p.cb == null) {
            synchronized (p) {
                p.finished = true;
                p.notifyAll();
            }
        } else {
            p.finished = true;
            eventThread.queuePacket(p);
        }
    }

    private void unregisterWatches(Packet p, int err) {
        Map<EventType, Set<Watcher>> materializedWatchers = null;
        try {
            materializedWatchers = p.watchDeregistration.unregister(err);
            for (Entry<EventType, Set<Watcher>> entry : materializedWatchers.entrySet()) {
                Set<Watcher> watchers = entry.getValue();
                if (watchers.size() > 0) {
                    queueEvent(p.watchDeregistration.getServerPath(), err, watchers, entry.getKey());
                    p.replyHeader.setErr(Code.OK.intValue());
                }
            }
        } catch (KeeperException.NoWatcherException nwe) {
            p.replyHeader.setErr(nwe.code().intValue());
        } catch (KeeperException ke) {
            p.replyHeader.setErr(ke.code().intValue());
        }
    }

    void queueEvent(String serverPath, int err, Set<Watcher> materializedWatchers, EventType eventType) {
        KeeperState sessionState = KeeperState.SyncConnected;
        if (KeeperException.Code.SESSIONEXPIRED.intValue() == err
            || KeeperException.Code.CONNECTIONLOSS.intValue() == err) {
            sessionState = Event.KeeperState.Disconnected;
        }
        WatchedEvent event = new WatchedEvent(eventType, sessionState, serverPath);
        eventThread.queueEvent(event, materializedWatchers);
    }

    void queueCallback(AsyncCallback cb, int rc, String path, Object ctx) {
        eventThread.queueCallback(cb, rc, path, ctx);
    }

    protected void onConnecting(InetSocketAddress addr) {
    }

    private void conLossPacket(Packet p) {
        if (p.replyHeader == null) {
            return;
        }
        switch (state) {
        case AUTH_FAILED:
            p.replyHeader.setErr(KeeperException.Code.AUTHFAILED.intValue());
            break;
        case CLOSED:
            p.replyHeader.setErr(KeeperException.Code.SESSIONEXPIRED.intValue());
            break;
        default:
            p.replyHeader.setErr(KeeperException.Code.CONNECTIONLOSS.intValue());
        }
        finishPacket(p);
    }

    public long getLastZxid() {
        return lastZxid;
    }

    static class EndOfStreamException extends IOException {
        private static final long serialVersionUID = -5438877188796231422L;
        public EndOfStreamException(String msg) {
            super(msg);
        }
        @Override
        public String toString() {
            return "EndOfStreamException: " + getMessage();
        }
    }

    private static class ConnectionTimeoutException extends IOException {
        public ConnectionTimeoutException(String message) {
            super(message);
        }
    }

    private static class SessionTimeoutException extends IOException {
        private static final long serialVersionUID = 824482094072071178L;
        public SessionTimeoutException(String msg) {
            super(msg);
        }
    }

    private static class SessionExpiredException extends IOException {
        private static final long serialVersionUID = -1388816932076193249L;
        public SessionExpiredException(String msg) {
            super(msg);
        }
    }

    private static class RWServerFoundException extends IOException {
        private static final long serialVersionUID = 90431199887158758L;
        public RWServerFoundException(String msg) {
            super(msg);
        }
    }

    class SendThread extends ZooKeeperThread {

        private long lastPingSentNs;
        private final ClientCnxnSocket clientCnxnSocket;
        private boolean isFirstConnect = true;
        private volatile ZooKeeperSaslClient zooKeeperSaslClient;
        private final AtomicReference<Login> loginRef = new AtomicReference<>();
        private InetSocketAddress rwServerAddress = null;
        private static final int minPingRwTimeout = 100;
        private static final int maxPingRwTimeout = 60000;
        private int pingRwTimeout = minPingRwTimeout;
        private boolean saslLoginFailed = false;

        SendThread(ClientCnxnSocket clientCnxnSocket) throws IOException {
            super(makeThreadName("-SendThread()"));
            this.clientCnxnSocket = clientCnxnSocket;
            changeZkState(States.CONNECTING);
            setDaemon(true);
        }

        ClientCnxnSocket getClientCnxnSocket() {
            return clientCnxnSocket;
        }

        SocketAddress getLocalSocketAddress() { return clientCnxnSocket.getLocalSocketAddress(); }
        SocketAddress getRemoteSocketAddress() { return clientCnxnSocket.getRemoteSocketAddress(); }
        long getSentCount() { return clientCnxnSocket.getSentCount(); }
        long getRecvCount() { return clientCnxnSocket.getRecvCount(); }
        void saslCompleted() { clientCnxnSocket.saslCompleted(); }

        void readResponse(ByteBuffer incomingBuffer) throws IOException {
            ByteBufferInputStream bbis = new ByteBufferInputStream(incomingBuffer);
            BinaryInputArchive bbia = BinaryInputArchive.getArchive(bbis);
            ReplyHeader replyHdr = new ReplyHeader();
            replyHdr.deserialize(bbia, "header");

            if (processSpecialResponse(replyHdr, bbia)) {
                return;
            }

            if (processTunnelAuth(bbia)) {
                return;
            }

            Packet packet;
            synchronized (pendingQueue) {
                if (pendingQueue.size() == 0) {
                    throw new IOException("Nothing in the queue, but got " + replyHdr.getXid());
                }
                packet = pendingQueue.remove();
            }

            processPacketResponse(replyHdr, bbia, packet);
        }

        private boolean processTunnelAuth(BinaryInputArchive bbia) throws IOException {
            if (tunnelAuthInProgress()) {
                GetSASLRequest request = new GetSASLRequest();
                request.deserialize(bbia, "token");
                zooKeeperSaslClient.respondToServer(request.getToken(), ClientCnxn_C4.this);
                return true;
            }
            return false;
        }

        private void processPacketResponse(ReplyHeader replyHdr, BinaryInputArchive bbia, Packet packet) throws IOException {
            try {
                if (packet.requestHeader.getXid() != replyHdr.getXid()) {
                    packet.replyHeader.setErr(KeeperException.Code.CONNECTIONLOSS.intValue());
                    throw new IOException("Xid out of order. Got Xid " + replyHdr.getXid()
                                          + " with err " + replyHdr.getErr()
                                          + " expected Xid " + packet.requestHeader.getXid()
                                          + " for a packet with details: " + packet);
                }

                packet.replyHeader.setXid(replyHdr.getXid());
                packet.replyHeader.setErr(replyHdr.getErr());
                packet.replyHeader.setZxid(replyHdr.getZxid());
                if (replyHdr.getZxid() > 0) {
                    lastZxid = replyHdr.getZxid();
                }
                if (packet.response != null && replyHdr.getErr() == 0) {
                    packet.response.deserialize(bbia, "response");
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Reading reply session id: 0x{}, packet:: {}", Long.toHexString(sessionId), packet);
                }
            } finally {
                finishPacket(packet);
            }
        }

        private boolean processSpecialResponse(ReplyHeader replyHdr, BinaryInputArchive bbia) throws IOException {
            switch (replyHdr.getXid()) {
                case PING_XID:
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Got ping response for session id: 0x{} after {}ms.",
                            Long.toHexString(sessionId),
                            ((System.nanoTime() - lastPingSentNs) / 1000000));
                    }
                    return true;
                case AUTHPACKET_XID:
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Got auth session id: 0x{}", Long.toHexString(sessionId));
                    }
                    if (replyHdr.getErr() == KeeperException.Code.AUTHFAILED.intValue()) {
                        changeZkState(States.AUTH_FAILED);
                        eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None,
                            Watcher.Event.KeeperState.AuthFailed, null));
                        eventThread.queueEventOfDeath();
                    }
                    return true;
                case NOTIFICATION_XID:
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Got notification session id: 0x{}", Long.toHexString(sessionId));
                    }
                    WatcherEvent event = new WatcherEvent();
                    event.deserialize(bbia, "response");
                    WatchedEvent we = new WatchedEvent(event, replyHdr.getZxid());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Got {} for session id 0x{}", we, Long.toHexString(sessionId));
                    }
                    eventThread.queueEvent(we);
                    return true;
                default:
                    return false;
            }
        }

        synchronized ZooKeeper.States getZkState() {
            return state;
        }

        final synchronized void changeZkState(ZooKeeper.States newState) throws IOException {
            if (!state.isAlive() && newState == States.CONNECTING) {
                throw new IOException(
                        "Connection has already been closed and reconnection is not allowed");
            }
            state = newState;
        }

        void primeConnection() throws IOException {
            if (LOG.isInfoEnabled()) {
                LOG.info(
                    "Socket connection established, initiating session, client: {}, server: {}",
                    clientCnxnSocket.getLocalSocketAddress(),
                    clientCnxnSocket.getRemoteSocketAddress());
            }
            isFirstConnect = false;
            long sessId = seenRwServerBefore ? sessionId : 0;
            ConnectRequest conReq = new ConnectRequest(0, lastZxid, sessionTimeout, sessId, sessionPasswd, readOnly);
            
            primeConnectionSetWatches();
            primeConnectionAuthInfo();

            outgoingQueue.addFirst(new Packet(null, null, conReq, null, null));
            clientCnxnSocket.connectionPrimed();
            
            if (LOG.isDebugEnabled()) {
                LOG.debug("Session establishment request sent on {}", clientCnxnSocket.getRemoteSocketAddress());
            }
        }

        private void primeConnectionAuthInfo() {
            for (AuthData id : authInfo) {
                outgoingQueue.addFirst(
                    new Packet(
                        new RequestHeader(ClientCnxn_C4.AUTHPACKET_XID, OpCode.auth),
                        null,
                        new AuthPacket(0, id.scheme, id.data),
                        null,
                        null));
            }
        }

        private void primeConnectionSetWatches() {
            if (clientConfig.getBoolean(ZKClientConfig.DISABLE_AUTO_WATCH_RESET)) {
                return;
            }
            
            List<String> dataWatches = watchManager.getDataWatchList();
            List<String> existWatches = watchManager.getExistWatchList();
            List<String> childWatches = watchManager.getChildWatchList();
            List<String> persistentWatches = watchManager.getPersistentWatchList();
            List<String> persistentRecursiveWatches = watchManager.getPersistentRecursiveWatchList();
            
            if (hasWatches(dataWatches, existWatches, childWatches, persistentWatches, persistentRecursiveWatches)) {
                Iterator<String> dataWatchesIter = dataWatches.iterator();
                Iterator<String> existWatchesIter = existWatches.iterator();
                Iterator<String> childWatchesIter = childWatches.iterator();
                Iterator<String> persistentWatchesIter = persistentWatches.iterator();
                Iterator<String> persistentRecursiveWatchesIter = persistentRecursiveWatches.iterator();
                long setWatchesLastZxid = lastZxid;

                while (hasMoreWatches(dataWatchesIter, existWatchesIter, childWatchesIter, persistentWatchesIter, persistentRecursiveWatchesIter)) {
                    buildAndQueueWatchPacket(dataWatchesIter, existWatchesIter, childWatchesIter, persistentWatchesIter, persistentRecursiveWatchesIter, setWatchesLastZxid);
                }
            }
        }

        private boolean hasWatches(List<String> d, List<String> e, List<String> c, List<String> p, List<String> pr) {
            return !d.isEmpty() || !e.isEmpty() || !c.isEmpty() || !p.isEmpty() || !pr.isEmpty();
        }

        private boolean hasMoreWatches(Iterator<String> d, Iterator<String> e, Iterator<String> c, Iterator<String> p, Iterator<String> pr) {
            return d.hasNext() || e.hasNext() || c.hasNext() || p.hasNext() || pr.hasNext();
        }

        private void buildAndQueueWatchPacket(
                Iterator<String> dataWatchesIter,
                Iterator<String> existWatchesIter,
                Iterator<String> childWatchesIter,
                Iterator<String> persistentWatchesIter,
                Iterator<String> persistentRecursiveWatchesIter,
                long setWatchesLastZxid) {
            
            List<String> dataWatchesBatch = new ArrayList<>();
            List<String> existWatchesBatch = new ArrayList<>();
            List<String> childWatchesBatch = new ArrayList<>();
            List<String> persistentWatchesBatch = new ArrayList<>();
            List<String> persistentRecursiveWatchesBatch = new ArrayList<>();
            int batchLength = 0;

            while (batchLength < SET_WATCHES_MAX_LENGTH) {
                final String watch;
                if (dataWatchesIter.hasNext()) {
                    watch = dataWatchesIter.next();
                    dataWatchesBatch.add(watch);
                } else if (existWatchesIter.hasNext()) {
                    watch = existWatchesIter.next();
                    existWatchesBatch.add(watch);
                } else if (childWatchesIter.hasNext()) {
                    watch = childWatchesIter.next();
                    childWatchesBatch.add(watch);
                } else if (persistentWatchesIter.hasNext()) {
                    watch = persistentWatchesIter.next();
                    persistentWatchesBatch.add(watch);
                } else if (persistentRecursiveWatchesIter.hasNext()) {
                    watch = persistentRecursiveWatchesIter.next();
                    persistentRecursiveWatchesBatch.add(watch);
                } else {
                    break;
                }
                batchLength += watch.length();
            }

            Record record;
            int opcode;
            if (persistentWatchesBatch.isEmpty() && persistentRecursiveWatchesBatch.isEmpty()) {
                record = new SetWatches(setWatchesLastZxid, dataWatchesBatch, existWatchesBatch, childWatchesBatch);
                opcode = OpCode.setWatches;
            } else {
                record = new SetWatches2(setWatchesLastZxid, dataWatchesBatch, existWatchesBatch,
                        childWatchesBatch, persistentWatchesBatch, persistentRecursiveWatchesBatch);
                opcode = OpCode.setWatches2;
            }
            RequestHeader header = new RequestHeader(ClientCnxn_C4.SET_WATCHES_XID, opcode);
            Packet packet = new Packet(header, new ReplyHeader(), record, null, null);
            outgoingQueue.addFirst(packet);
        }

        private void sendPing() {
            lastPingSentNs = System.nanoTime();
            RequestHeader h = new RequestHeader(ClientCnxn_C4.PING_XID, OpCode.ping);
            queuePacket(h, null, null, null, null, null, null, null, null);
        }

        private void startConnect(InetSocketAddress addr) throws IOException {
            saslLoginFailed = false;
            if (!isFirstConnect) {
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(1000));
                } catch (InterruptedException e) {
                    LOG.warn("Unexpected exception", e);
                }
            }
            changeZkState(States.CONNECTING);
            String hostPort = addr.getHostString() + ":" + addr.getPort();
            MDC.put("myid", hostPort);
            setName(getName().replaceAll("\\(.*\\)", "(" + hostPort + ")"));
            
            initSaslClient(addr);
            logStartConnect(addr);
            clientCnxnSocket.connect(addr);
        }

        private void initSaslClient(InetSocketAddress addr) {
            if (clientConfig.isSaslClientEnabled()) {
                try {
                    zooKeeperSaslClient = new ZooKeeperSaslClient(
                        SaslServerPrincipal.getServerPrincipal(addr, clientConfig), clientConfig, loginRef);
                } catch (LoginException e) {
                    LOG.warn(
                        "SASL configuration failed. "
                            + "Will continue connection to Zookeeper server without "
                            + "SASL authentication, if Zookeeper server allows it.", e);
                    eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.AuthFailed, null));
                    saslLoginFailed = true;
                }
            }
        }

        private void logStartConnect(InetSocketAddress addr) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Opening socket connection to server {}.", addr);
                if (zooKeeperSaslClient != null) {
                    LOG.info("SASL config status: {}", zooKeeperSaslClient.getConfigStatus());
                }
            }
        }

        @Override
        @SuppressFBWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")
        public void run() {
            clientCnxnSocket.introduce(this, sessionId, outgoingQueue);
            clientCnxnSocket.updateNow();
            clientCnxnSocket.updateLastSendAndHeard();
            
            long lastPingRwServer = Time.currentElapsedTime();
            InetSocketAddress serverAddress = null;
            
            while (state.isAlive()) {
                try {
                    serverAddress = performConnectionIfNeeded(serverAddress);
                    if (closing || !state.isAlive()) {
                        break;
                    }

                    int to = calculateTimeoutAndHandleSasl();
                    checkSessionExpirations(to);
                    
                    to = handlePing(to);
                    to = handleReadOnlyPing(to, lastPingRwServer);
                    lastPingRwServer = updateLastPingRwServer(lastPingRwServer);

                    clientCnxnSocket.doTransport(to, pendingQueue, ClientCnxn_C4.this);
                } catch (Exception e) {
                    handleRunException(serverAddress, e);
                }
            }

            performSendThreadCleanup();
        }

        private InetSocketAddress performConnectionIfNeeded(InetSocketAddress serverAddress) throws IOException {
            if (!clientCnxnSocket.isConnected() && !closing) {
                return performReconnection();
            }
            return serverAddress;
        }

        private void handleRunException(InetSocketAddress serverAddress, Exception e) {
            if (closing) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("An exception was thrown while closing send thread for session 0x{}.",
                        Long.toHexString(getSessionId()), e);
                }
            } else {
                LOG.warn("Session 0x{} for server {}, Closing socket connection. "
                        + "Attempting reconnect except it is a SessionExpiredException or SessionTimeoutException.",
                    Long.toHexString(getSessionId()), serverAddress, e);
                cleanAndNotifyState();
            }
        }

        private long updateLastPingRwServer(long lastPingRwServer) {
            if (state == States.CONNECTEDREADONLY) {
                long now = Time.currentElapsedTime();
                if (now - lastPingRwServer >= pingRwTimeout) {
                    return now;
                }
            }
            return lastPingRwServer;
        }

        private InetSocketAddress performReconnection() throws IOException {
            InetSocketAddress serverAddress;
            if (rwServerAddress != null) {
                serverAddress = rwServerAddress;
                rwServerAddress = null;
            } else {
                serverAddress = hostProvider.next(1000);
            }
            onConnecting(serverAddress);
            startConnect(serverAddress);
            clientCnxnSocket.updateNow();
            clientCnxnSocket.updateLastSend();
            return serverAddress;
        }

        private int calculateTimeoutAndHandleSasl() throws IOException {
            int to;
            if (state.isConnected()) {
                handleSaslState();
                to = readTimeout - clientCnxnSocket.getIdleRecv();
            } else {
                to = connectTimeout - clientCnxnSocket.getIdleSend();
            }
            return to;
        }

        private void handleSaslState() throws IOException {
            if (zooKeeperSaslClient != null) {
                boolean sendAuthEvent = false;
                if (zooKeeperSaslClient.getSaslState() == ZooKeeperSaslClient.SaslState.INITIAL) {
                    try {
                        zooKeeperSaslClient.initialize(ClientCnxn_C4.this);
                    } catch (SaslException e) {
                        LOG.error("SASL authentication with Zookeeper Quorum member failed.", e);
                        changeZkState(States.AUTH_FAILED);
                        sendAuthEvent = true;
                    }
                }
                KeeperState authState = zooKeeperSaslClient.getKeeperState();
                if (authState != null) {
                    if (authState == KeeperState.AuthFailed) {
                        changeZkState(States.AUTH_FAILED);
                        sendAuthEvent = true;
                    } else {
                        if (authState == KeeperState.SaslAuthenticated) {
                            sendAuthEvent = true;
                        }
                    }
                }
                if (sendAuthEvent) {
                    eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, authState, null));
                    if (state == States.AUTH_FAILED) {
                        eventThread.queueEventOfDeath();
                    }
                }
            }
        }

        private void checkSessionExpirations(int to) throws IOException {
            long expiration;
            if (sessionId == 0) {
                expiration = newSessionTimeout - clientCnxnSocket.getIdleRecv();
            } else {
                expiration = expirationTimeout - clientCnxnSocket.getIdleRecv();
            }
            if (expiration <= 0) {
                String warnInfo = String.format(
                    "Client session timed out, have not heard from server in %dms for session id 0x%s",
                    clientCnxnSocket.getIdleRecv(),
                    Long.toHexString(sessionId));
                LOG.warn(warnInfo);
                changeZkState(States.CLOSED);
                throw new SessionTimeoutException(warnInfo);
            } else if (to <= 0) {
                String warnInfo = String.format(
                    "Client connection timed out, have not heard from server in %dms for session id 0x%s",
                    clientCnxnSocket.getIdleRecv(),
                    Long.toHexString(sessionId));
                throw new ConnectionTimeoutException(warnInfo);
            }
        }

        private int handlePing(int to) {
            final int MAX_SEND_PING_INTERVAL = 10000;
            if (state.isConnected()) {
                int timeToNextPing = readTimeout / 2
                                     - clientCnxnSocket.getIdleSend()
                                     - ((clientCnxnSocket.getIdleSend() > 1000) ? 1000 : 0);
                if (timeToNextPing <= 0 || clientCnxnSocket.getIdleSend() > MAX_SEND_PING_INTERVAL) {
                    sendPing();
                    clientCnxnSocket.updateLastSend();
                } else {
                    if (timeToNextPing < to) {
                        to = timeToNextPing;
                    }
                }
            }
            return to;
        }

        private int handleReadOnlyPing(int to, long lastPingRwServer) throws RWServerFoundException {
            if (state == States.CONNECTEDREADONLY) {
                long now = Time.currentElapsedTime();
                int idlePingRwServer = (int) (now - lastPingRwServer);
                if (idlePingRwServer >= pingRwTimeout) {
                    pingRwTimeout = Math.min(2 * pingRwTimeout, maxPingRwTimeout);
                    pingRwServer();
                }
                to = Math.min(to, pingRwTimeout - idlePingRwServer);
            }
            return to;
        }

        private void performSendThreadCleanup() {
            synchronized (outgoingQueue) {
                cleanup();
            }
            clientCnxnSocket.close();
            if (state.isAlive()) {
                eventThread.queueEvent(new WatchedEvent(Event.EventType.None, Event.KeeperState.Disconnected, null));
            }
            if (closing) {
                eventThread.queueEvent(new WatchedEvent(Event.EventType.None, KeeperState.Closed, null));
            } else if (state == States.CLOSED) {
                eventThread.queueEvent(new WatchedEvent(Event.EventType.None, KeeperState.Expired, null));
            }
            eventThread.queueEventOfDeath();

            Login l = loginRef.getAndSet(null);
            if (l != null) {
                l.shutdown();
            }
            ZooTrace.logTraceMessage(
                LOG,
                ZooTrace.getTextTraceLevel(),
                "SendThread exited loop for session: 0x" + Long.toHexString(getSessionId()));
        }

        private void abortConnection() {
            try {
                clientCnxnSocket.testableCloseSocket();
            } catch (IOException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Fail to close ongoing socket", e);
                }
            }
        }

        private void cleanAndNotifyState() {
            cleanup();
            if (state.isAlive()) {
                eventThread.queueEvent(new WatchedEvent(Event.EventType.None, Event.KeeperState.Disconnected, null));
            }
            clientCnxnSocket.updateNow();
        }

        private void pingRwServer() throws RWServerFoundException {
            String result = null;
            InetSocketAddress addr = hostProvider.next(0);

            if (LOG.isInfoEnabled()) {
                LOG.info("Checking server {} for being r/w. Timeout {}", addr, pingRwTimeout);
            }
            try {
                result = FourLetterWordMain.send4LetterWord(addr.getHostString(), addr.getPort(), "isro", clientConfig, 1000);
            } catch (ConnectException e) {
            } catch (IOException | X509Exception.SSLContextException e) {
                LOG.warn("Exception while seeking for r/w server.", e);
            }

            if ("rw\n".equals(result)) {
                pingRwTimeout = minPingRwTimeout;
                rwServerAddress = addr;
                throw new RWServerFoundException("Majority server found at "
                                                 + addr.getHostString() + ":" + addr.getPort());
            }
        }

        private void cleanup() {
            clientCnxnSocket.cleanup();
            synchronized (pendingQueue) {
                for (Packet p : pendingQueue) {
                    conLossPacket(p);
                }
                pendingQueue.clear();
            }
            Iterator<Packet> iter = outgoingQueue.iterator();
            while (iter.hasNext()) {
                Packet p = iter.next();
                conLossPacket(p);
                iter.remove();
            }
        }

        void onConnected(
            int _negotiatedSessionTimeout,
            long _sessionId,
            byte[] _sessionPasswd,
            boolean isRO) throws IOException {
            negotiatedSessionTimeout = _negotiatedSessionTimeout;
            if (negotiatedSessionTimeout <= 0) {
                changeZkState(States.CLOSED);

                eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, Watcher.Event.KeeperState.Expired, null));
                eventThread.queueEventOfDeath();

                String warnInfo = String.format(
                    "Unable to reconnect to ZooKeeper service, session 0x%s has expired",
                    Long.toHexString(sessionId));
                LOG.warn(warnInfo);
                throw new SessionExpiredException(warnInfo);
            }

            if (!readOnly && isRO) {
                LOG.error("Read/write client got connected to read-only server");
            }

            readTimeout = negotiatedSessionTimeout * 2 / 3;
            expirationTimeout = negotiatedSessionTimeout * 4 / 3;
            connectTimeout = negotiatedSessionTimeout / hostProvider.size();
            hostProvider.onConnected();
            sessionId = _sessionId;
            sessionPasswd = _sessionPasswd != null ? Arrays.copyOf(_sessionPasswd, _sessionPasswd.length) : null;
            changeZkState(isRO ? States.CONNECTEDREADONLY : States.CONNECTED);
            seenRwServerBefore |= !isRO;
            if (LOG.isInfoEnabled()) {
                LOG.info(
                    "Session establishment complete on server {}, session id = 0x{}, negotiated timeout = {}{}",
                    clientCnxnSocket.getRemoteSocketAddress(),
                    Long.toHexString(sessionId),
                    negotiatedSessionTimeout,
                    (isRO ? " (READ-ONLY mode)" : ""));
            }
            KeeperState eventState = isRO ? KeeperState.ConnectedReadOnly : KeeperState.SyncConnected;
            eventThread.queueEvent(new WatchedEvent(Watcher.Event.EventType.None, eventState, null));
        }

        void close() {
            try {
                changeZkState(States.CLOSED);
            } catch (IOException e) {
                LOG.warn("Connection close fails when migrates state from {} to CLOSED", getZkState());
            }
            clientCnxnSocket.onClosing();
        }

        void testableCloseSocket() throws IOException {
            clientCnxnSocket.testableCloseSocket();
        }

        public boolean tunnelAuthInProgress() {
            if (!clientConfig.isSaslClientEnabled()) {
                return false;
            }
            if (saslLoginFailed) {
                return false;
            }
            if (zooKeeperSaslClient == null) {
                return true;
            }
            return zooKeeperSaslClient.clientTunneledAuthenticationInProgress();
        }

        public void sendPacket(Packet p) throws IOException {
            clientCnxnSocket.sendPacket(p);
        }

        public ZooKeeperSaslClient getZooKeeperSaslClient() {
            return zooKeeperSaslClient;
        }

        Login getLogin() {
            return loginRef.get();
        }
    }

    public void disconnect() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Disconnecting client for session: 0x{}", Long.toHexString(getSessionId()));
        }
        sendThread.close();
        try {
            sendThread.join();
        } catch (InterruptedException ex) {
            LOG.warn("Got interrupted while waiting for the sender thread to close", ex);
        }
        eventThread.queueEventOfDeath();
    }

    public void close() throws IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Closing client for session: 0x{}", Long.toHexString(getSessionId()));
        }
        try {
            RequestHeader h = new RequestHeader();
            h.setType(ZooDefs.OpCode.closeSession);
            submitRequest(h, null, null, null);
        } catch (InterruptedException e) {
        } finally {
            disconnect();
        }
    }

    public synchronized int getXid() {
        if (xid == Integer.MAX_VALUE) {
            xid = 1;
        }
        return xid++;
    }

    public ReplyHeader submitRequest(
        RequestHeader h,
        Record request,
        Record response,
        WatchRegistration watchRegistration) throws InterruptedException {
        return submitRequest(h, request, response, watchRegistration, null);
    }

    public ReplyHeader submitRequest(
        RequestHeader h,
        Record request,
        Record response,
        WatchRegistration watchRegistration,
        WatchDeregistration watchDeregistration) throws InterruptedException {
        ReplyHeader r = new ReplyHeader();
        Packet packet = queuePacket(
            h,
            r,
            request,
            response,
            null,
            null,
            null,
            null,
            watchRegistration,
            watchDeregistration);
        synchronized (packet) {
            if (requestTimeout > 0) {
                waitForPacketFinish(r, packet);
            } else {
                while (!packet.finished) {
                    packet.wait();
                }
            }
        }
        if (r.getErr() == Code.REQUESTTIMEOUT.intValue()) {
            sendThread.abortConnection();
        }
        return r;
    }

    private void waitForPacketFinish(ReplyHeader r, Packet packet) throws InterruptedException {
        long remainingTime = requestTimeout;
        while (!packet.finished && remainingTime > 0) {
            long waitStartTime = Time.currentElapsedTime();
            packet.wait(remainingTime);
            remainingTime -= (Time.currentElapsedTime() - waitStartTime);
        }
        if (!packet.finished) {
            LOG.error("Timeout error occurred for the packet '{}'.", packet);
            r.setErr(Code.REQUESTTIMEOUT.intValue());
        }
    }

    public void saslCompleted() {
        sendThread.saslCompleted();
    }

    public void sendPacket(Record request, Record response, AsyncCallback cb, int opCode) throws IOException {
        int localXid = getXid();
        RequestHeader h = new RequestHeader();
        h.setXid(localXid);
        h.setType(opCode);

        ReplyHeader r = new ReplyHeader();
        r.setXid(localXid);

        Packet p = new Packet(h, r, request, response, null);
        p.cb = cb;
        sendThread.sendPacket(p);
    }

    public Packet queuePacket(
        RequestHeader h,
        ReplyHeader r,
        Record request,
        Record response,
        AsyncCallback cb,
        String clientPath,
        String serverPath,
        Object ctx,
        WatchRegistration watchRegistration) {
        return queuePacket(h, r, request, response, cb, clientPath, serverPath, ctx, watchRegistration, null);
    }

    @SuppressFBWarnings("JLM_JSR166_UTILCONCURRENT_MONITORENTER")
    public Packet queuePacket(
        RequestHeader h,
        ReplyHeader r,
        Record request,
        Record response,
        AsyncCallback cb,
        String clientPath,
        String serverPath,
        Object ctx,
        WatchRegistration watchRegistration,
        WatchDeregistration watchDeregistration) {
        
        Packet packet = new Packet(h, r, request, response, watchRegistration);
        packet.cb = cb;
        packet.ctx = ctx;
        packet.clientPath = clientPath;
        packet.serverPath = serverPath;
        packet.watchDeregistration = watchDeregistration;
        
        synchronized (outgoingQueue) {
            if (!state.isAlive() || closing) {
                conLossPacket(packet);
            } else {
                if (h.getType() == OpCode.closeSession) {
                    closing = true;
                }
                outgoingQueue.add(packet);
            }
        }
        sendThread.clientCnxnSocket.packetAdded();
        return packet;
    }

    public void addAuthInfo(String scheme, byte[] auth) {
        if (!state.isAlive()) {
            return;
        }
        authInfo.add(new AuthData(scheme, auth));
        queuePacket(
            new RequestHeader(ClientCnxn_C4.AUTHPACKET_XID, OpCode.auth),
            null,
            new AuthPacket(0, scheme, auth),
            null,
            null,
            null,
            null,
            null,
            null);
    }

    States getState() {
        return state;
    }

    private static class LocalCallback {
        private final AsyncCallback cb;
        private final int rc;
        private final String path;
        private final Object ctx;
        public LocalCallback(AsyncCallback cb, int rc, String path, Object ctx) {
            this.cb = cb;
            this.rc = rc;
            this.path = path;
            this.ctx = ctx;
        }
    }

    private void initRequestTimeout() {
        try {
            requestTimeout = clientConfig.getLong(
                ZKClientConfig.ZOOKEEPER_REQUEST_TIMEOUT,
                ZKClientConfig.ZOOKEEPER_REQUEST_TIMEOUT_DEFAULT);
            if (LOG.isInfoEnabled()) {
                LOG.info(
                    "{} value is {}. feature enabled={}",
                    ZKClientConfig.ZOOKEEPER_REQUEST_TIMEOUT,
                    requestTimeout,
                    requestTimeout > 0);
            }
        } catch (NumberFormatException e) {
            LOG.error(
                "Configured value {} for property {} can not be parsed to long.",
                clientConfig.getProperty(ZKClientConfig.ZOOKEEPER_REQUEST_TIMEOUT),
                ZKClientConfig.ZOOKEEPER_REQUEST_TIMEOUT);
            throw e;
        }
    }

    public ZooKeeperSaslClient getZooKeeperSaslClient() {
        return sendThread.getZooKeeperSaslClient();
    }
}