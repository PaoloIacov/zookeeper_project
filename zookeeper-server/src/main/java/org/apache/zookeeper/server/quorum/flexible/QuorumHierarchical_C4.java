package org.apache.zookeeper.server.quorum.flexible;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.zookeeper.common.ConfigException;
import org.apache.zookeeper.server.quorum.QuorumPeer.LearnerType;
import org.apache.zookeeper.server.quorum.QuorumPeer.QuorumServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a validator for hierarchical quorums. With this
 * construction, zookeeper servers are split into disjoint groups, and
 * each server has a weight. We obtain a quorum if we get more than half
 * of the total weight of a group for a majority of groups.
 */
public class QuorumHierarchical_C4 implements QuorumVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(QuorumHierarchical_C4.class);

    private final Map<Long, Long> serverWeight = new HashMap<>();
    private final Map<Long, Long> serverGroup = new HashMap<>();
    private final Map<Long, Long> groupWeight = new HashMap<>();

    private int numGroups = 0;

    private final Map<Long, QuorumServer> allMembers = new HashMap<>();
    private final Map<Long, QuorumServer> participatingMembers = new HashMap<>();
    private final Map<Long, QuorumServer> observingMembers = new HashMap<>();

    private long version = 0;

    public int hashCode() {
        assert false : "hashCode not designed";
        return 42; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuorumHierarchical_C4)) {
            return false;
        }
        QuorumHierarchical_C4 qm = (QuorumHierarchical_C4) o;

        if (this.version != qm.getVersion()) {
            return false;
        }

        if (allMembers.size() != qm.getAllMembers().size()
                || serverWeight.size() != qm.serverWeight.size()
                || groupWeight.size() != qm.groupWeight.size()
                || serverGroup.size() != qm.serverGroup.size()) {
            return false;
        }

        return checkMembersEqual(qm) && checkMapsEqual(qm);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private boolean checkMembersEqual(QuorumHierarchical_C4 qm) {
        for (QuorumServer qs : allMembers.values()) {
            QuorumServer qso = qm.getAllMembers().get(qs.id);
            if (qso == null || !qs.equals(qso)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkMapsEqual(QuorumHierarchical_C4 qm) {
        return checkMapEquals(serverWeight, qm.serverWeight)
                && checkMapEquals(groupWeight, qm.groupWeight)
                && checkMapEquals(serverGroup, qm.serverGroup);
    }

    private boolean checkMapEquals(Map<Long, Long> map1, Map<Long, Long> map2) {
        for (Entry<Long, Long> entry : map1.entrySet()) {
            if (!entry.getValue().equals(map2.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    public QuorumHierarchical_C4(String filename) throws ConfigException {
        readConfigFile(filename);
    }

    public QuorumHierarchical_C4(Properties qp) throws ConfigException {
        parse(qp);
        if (LOG.isInfoEnabled()) {
            LOG.info("{}, {}, {}", serverWeight.size(), serverGroup.size(), groupWeight.size());
        }
    }

    public long getWeight(long id) {
        Long weight = serverWeight.get(id);
        return weight == null ? 0L : weight;
    }

    private void readConfigFile(String filename) throws ConfigException {
        File configFile = new File(filename);

        if (LOG.isInfoEnabled()) {
            LOG.info("Reading configuration from: {}", configFile);
        }

        try {
            if (!configFile.exists()) {
                throw new IllegalArgumentException(configFile.toString() + " file is missing");
            }

            Properties cfg = new Properties();
            try (FileInputStream in = new FileInputStream(configFile)) {
                cfg.load(in);
            }
            parse(cfg);
        } catch (IOException | IllegalArgumentException e) {
            throw new ConfigException("Error processing " + filename, e);
        }
    }

    private void parse(Properties quorumProp) throws ConfigException {
        for (Entry<Object, Object> entry : quorumProp.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();

            if (key.startsWith("server.")) {
                parseServer(key, value);
            } else if (key.startsWith("group")) {
                parseGroup(key, value);
            } else if (key.startsWith("weight")) {
                parseWeight(key, value);
            } else if (key.equals("version")) {
                version = Long.parseLong(value, 16);
            }
        }

        verifyParticipantGroups();
        computeGroupWeight();
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void parseServer(String key, String value) throws ConfigException {
        int dot = key.indexOf('.');
        long sid = Long.parseLong(key.substring(dot + 1));
        QuorumServer qs = new QuorumServer(sid, value);
        allMembers.put(sid, qs);
        if (qs.type == LearnerType.PARTICIPANT) {
            participatingMembers.put(sid, qs);
        } else {
            observingMembers.put(sid, qs);
        }
    }

    private void parseGroup(String key, String value) throws ConfigException {
        int dot = key.indexOf('.');
        long gid = Long.parseLong(key.substring(dot + 1));

        numGroups++;

        String[] parts = value.split(":");
        for (String s : parts) {
            long sid = Long.parseLong(s);
            if (serverGroup.containsKey(sid)) {
                throw new ConfigException("Server " + sid + " is in multiple groups");
            } else {
                serverGroup.put(sid, gid);
            }
        }
    }

    private void parseWeight(String key, String value) {
        int dot = key.indexOf('.');
        long sid = Long.parseLong(key.substring(dot + 1));
        serverWeight.put(sid, Long.parseLong(value));
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    private void verifyParticipantGroups() throws ConfigException {
        for (QuorumServer qs : allMembers.values()) {
            Long id = qs.id;
            if (qs.type == LearnerType.PARTICIPANT) {
                if (!serverGroup.containsKey(id)) {
                    throw new ConfigException("Server " + id + " is not in a group");
                }
                serverWeight.putIfAbsent(id, 1L);
            }
        }
    }

    public Map<Long, QuorumServer> getAllMembers() {
        return allMembers;
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    public String toString() {
        StringWriter sw = new StringWriter();

        for (QuorumServer member : getAllMembers().values()) {
            String key = "server." + member.id;
            String value = member.toString();
            sw.append(key).append('=').append(value).append('\n');
        }

        Map<Long, String> groups = new HashMap<>();
        for (Entry<Long, Long> pair : serverGroup.entrySet()) {
            Long sid = pair.getKey();
            Long gid = pair.getValue();
            String str = groups.get(gid);
            if (str == null) {
                str = sid.toString();
            } else {
                str = str.concat(":").concat(sid.toString());
            }
            groups.put(gid, str);
        }

        for (Entry<Long, String> pair : groups.entrySet()) {
            Long gid = pair.getKey();
            String key = "group." + gid.toString();
            String value = pair.getValue();
            sw.append(key).append('=').append(value).append('\n');
        }

        for (Entry<Long, Long> pair : serverWeight.entrySet()) {
            Long sid = pair.getKey();
            String key = "weight." + sid.toString();
            String value = pair.getValue().toString();
            sw.append(key).append('=').append(value).append('\n');
        }

        sw.append("version=").append(Long.toHexString(version));

        return sw.toString();
    }

    private void computeGroupWeight() {
        for (Entry<Long, Long> entry : serverGroup.entrySet()) {
            Long sid = entry.getKey();
            Long gid = entry.getValue();
            if (!groupWeight.containsKey(gid)) {
                groupWeight.put(gid, serverWeight.get(sid));
            } else {
                long totalWeight = serverWeight.get(sid) + groupWeight.get(gid);
                groupWeight.put(gid, totalWeight);
            }
        }

        for (long weight : groupWeight.values()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Group weight: {}", weight);
            }
            if (weight == 0L) {
                numGroups--;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("One zero-weight group: 1, {}", numGroups);
                }
            }
        }
    }

    public boolean containsQuorum(Set<Long> set) {
        Map<Long, Long> expansion = new HashMap<>();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Set size: {}", set.size());
        }
        if (set.isEmpty()) {
            return false;
        }

        for (long sid : set) {
            Long gid = serverGroup.get(sid);
            if (gid == null) {
                continue;
            }
            if (!expansion.containsKey(gid)) {
                expansion.put(gid, serverWeight.get(sid));
            } else {
                long totalWeight = serverWeight.get(sid) + expansion.get(gid);
                expansion.put(gid, totalWeight);
            }
        }

        int majGroupCounter = 0;
        for (Entry<Long, Long> entry : expansion.entrySet()) {
            Long gid = entry.getKey();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Group info: {}, {}, {}", entry.getValue(), gid, groupWeight.get(gid));
            }
            if (entry.getValue() > (groupWeight.get(gid) / 2)) {
                majGroupCounter++;
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Majority group counter: {}, {}", majGroupCounter, numGroups);
        }
        
        if (majGroupCounter > (numGroups / 2)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Positive set size: {}", set.size());
            }
            return true;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Negative set size: {}", set.size());
            }
            return false;
        }
    }

    public Map<Long, QuorumServer> getVotingMembers() {
        return participatingMembers;
    }

    public Map<Long, QuorumServer> getObservingMembers() {
        return observingMembers;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long ver) {
        version = ver;
    }

}
