/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
 *
 * The configuration of quorums uses two parameters: group and weight.
 * Groups are sets of ZooKeeper servers, and we set a group by passing
 * a colon-separated list of server ids. It is also necessary to assign
 * weights to server. Here is an example of a configuration that creates
 * three groups and assigns a weight of 1 to each server:
 *
 * group.1=1:2:3
 * group.2=4:5:6
 * group.3=7:8:9
 *
 * weight.1=1
 * weight.2=1
 * weight.3=1
 * weight.4=1
 * weight.5=1
 * weight.6=1
 * weight.7=1
 * weight.8=1
 * weight.9=1
 *
 * Note that it is still necessary to define peers using the server keyword.
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class QuorumHierarchical_C3 implements QuorumVerifier {

    private static final Logger LOG = LoggerFactory.getLogger(QuorumHierarchical_C3.class);

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
        return 42; // any arbitrary constant will do
    }

    public boolean equals(Object o) {
        if (!(o instanceof QuorumHierarchical_C3)) {
            return false;
        }
        QuorumHierarchical_C3 qm = (QuorumHierarchical_C3) o;
        if (qm.getVersion() == version) {
            return true;
        }
        if (!areSizesEqual(qm)) {
            return false;
        }
        
        return areMembersEqual(qm) 
            && areMapsEqual(serverWeight, qm.serverWeight) 
            && areMapsEqual(groupWeight, qm.groupWeight) 
            && areMapsEqual(serverGroup, qm.serverGroup);
    }

    private boolean areSizesEqual(QuorumHierarchical_C3 qm) {
        return allMembers.size() == qm.getAllMembers().size()
                && serverWeight.size() == qm.serverWeight.size()
                && groupWeight.size() == qm.groupWeight.size()
                && serverGroup.size() == qm.serverGroup.size();
    }

    private boolean areMembersEqual(QuorumHierarchical_C3 qm) {
        for (QuorumServer qs : allMembers.values()) {
            QuorumServer qso = qm.getAllMembers().get(qs.id);
            if (qso == null || !qs.equals(qso)) {
                return false;
            }
        }
        return true;
    }

    private boolean areMapsEqual(Map<Long, Long> map1, Map<Long, Long> map2) {
        for (Entry<Long, Long> entry : map1.entrySet()) {
            if (!entry.getValue().equals(map2.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * This constructor requires the quorum configuration
     * to be declared in a separate file, and it takes the
     * file as an input parameter.
     */
    public QuorumHierarchical_C3(String filename) throws ConfigException {
        readConfigFile(filename);
    }

    /**
     * This constructor takes a set of properties. We use
     * it in the unit test for this feature.
     */
    public QuorumHierarchical_C3(Properties qp) throws ConfigException {
        parse(qp);
        if (LOG.isInfoEnabled()) {
            LOG.info("{}, {}, {}", serverWeight.size(), serverGroup.size(), groupWeight.size());
        }
    }

    /**
     * Returns the weight of a server.
     *
     * @param id
     */
    public long getWeight(long id) {
        return serverWeight.get(id);
    }

    /**
     * Reads a configuration file. Called from the constructor
     * that takes a file as an input.
     */
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

    /**
     * Parse properties if configuration given in a separate file.
     * Assumes that allMembers has been already assigned
     * * @throws ConfigException
     */
    private void parse(Properties quorumProp) throws ConfigException {
        for (Entry<Object, Object> entry : quorumProp.entrySet()) {
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            parseEntry(key, value);
        }

        validateAndAssignWeights();
        computeGroupWeight();
    }

    private void parseEntry(String key, String value) throws ConfigException {
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
                throw new ConfigException("Server " + sid + "is in multiple groups");
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

    private void validateAndAssignWeights() throws ConfigException {
        for (QuorumServer qs : allMembers.values()) {
            Long id = qs.id;
            if (qs.type == LearnerType.PARTICIPANT) {
                if (!serverGroup.containsKey(id)) {
                    throw new ConfigException("Server " + id + "is not in a group");
                }
                if (!serverWeight.containsKey(id)) {
                    serverWeight.put(id, 1L);
                }
            }
        }
    }

    public Map<Long, QuorumServer> getAllMembers() {
        return allMembers;
    }

    public String toString() {
        StringWriter sw = new StringWriter();

        for (QuorumServer member : getAllMembers().values()) {
            String key = "server." + member.id;
            String value = member.toString();
            sw.append(key);
            sw.append('=');
            sw.append(value);
            sw.append('\n');
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
            sw.append(key);
            sw.append('=');
            sw.append(value);
            sw.append('\n');
        }

        for (Entry<Long, Long> pair : serverWeight.entrySet()) {
            Long sid = pair.getKey();
            String key = "weight." + sid.toString();
            String value = pair.getValue().toString();
            sw.append(key);
            sw.append('=');
            sw.append(value);
            sw.append('\n');
        }

        sw.append("version=" + Long.toHexString(version));

        return sw.toString();
    }

    /**
     * This method pre-computes the weights of groups to speed up processing
     * when validating a given set. We compute the weights of groups in
     * different places, so we have a separate method.
     */
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

        /*
         * Do not consider groups with weight zero
         */
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

    /**
     * Verifies if a given set is a quorum.
     */
    public boolean containsQuorum(Set<Long> set) {
        Map<Long, Long> expansion = new HashMap<>();

        /*
         * Adds up weights per group
         */
        if (LOG.isDebugEnabled()) {
            LOG.debug("Set size: {}", set.size());
        }
        if (set.size() == 0) {
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

        /*
         * Check if all groups have majority
         */
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
