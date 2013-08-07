/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.whirr.service.druid;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.whirr.Cluster;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.FirewallManager;
import org.apache.whirr.service.zookeeper.ZooKeeperCluster;

import java.io.IOException;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

public class DruidRealtimeClusterActionHandler extends DruidClusterActionHandler {
    public static final String ROLE = "druid-realtime";

    @Override
    public String getRole() {
        return ROLE;
    }

    @Override
    protected void beforeBootstrap(ClusterActionEvent event) throws IOException {
        ClusterSpec clusterSpec = event.getClusterSpec();
        Configuration conf = getConfiguration(clusterSpec);

        addStatement(event, call("retry_helpers"));
        addStatement(event, call("install_tarball"));
        addStatement(event, call("configure_hostnames"));

        addStatement(event, call(getInstallFunction(conf, "java", "install_oracle_jdk7")));

        String tarurl = prepareRemoteFileUrl(event,
                getConfiguration(clusterSpec).getString(DruidConstants.KEY_TARBALL_URL));

        addStatement(event, call(
                getInstallFunction(getConfiguration(clusterSpec)), tarurl)
        );
    }

    @Override
    protected void beforeConfigure(ClusterActionEvent event) throws IOException {
        ClusterSpec clusterSpec = event.getClusterSpec();
        Cluster cluster = event.getCluster();
        Configuration conf = getConfiguration(clusterSpec);

        event.getFirewallManager().addRule(
                FirewallManager.Rule.create()
                        .destination(cluster.getInstancesMatching(role(ROLE)))
                        .port(DruidConstants.BROKER_CLIENT_PORT)
        );

        handleFirewallRules(event);

        try {
            Configuration config = DruidConfigurationBuilder.buildDruidConfig("/tmp/broker.properties", clusterSpec, cluster);
        } catch (ConfigurationException e) {
            throw new IOException(e);
        }

        String quorum = ZooKeeperCluster.getHosts(cluster);

        String tarurl = prepareRemoteFileUrl(event,
                conf.getString(DruidConstants.KEY_TARBALL_URL));
        addStatement(event, call("retry_helpers"));
        addStatement(event, call(
                getConfigureFunction(conf),
                ROLE,
                quorum
        ));
    }
}
