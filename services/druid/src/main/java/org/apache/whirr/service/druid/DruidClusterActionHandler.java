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


import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.whirr.RolePredicates;
import org.apache.whirr.Cluster.Instance;

import org.apache.whirr.Cluster;
import org.apache.whirr.service.ClusterActionEvent;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.FirewallManager;
import org.apache.whirr.service.zookeeper.ZooKeeperCluster;

import static org.apache.whirr.RolePredicates.role;
import static org.jclouds.scriptbuilder.domain.Statements.call;

public abstract class DruidClusterActionHandler extends ClusterActionHandlerSupport {

    public static final String ROLE = "druid-invalid";
    public static final Integer PORT = 8080;

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
                quorum,
                PORT.toString()
        ));
    }

    protected synchronized Configuration getConfiguration(ClusterSpec clusterSpec) throws IOException {
        return getConfiguration(clusterSpec, DruidConstants.FILE_DRUID_DEFAULT_PROPERTIES);
    }

    protected String getInstallFunction(Configuration config) {
        return getInstallFunction(config, "druid", DruidConstants.FUNCTION_INSTALL);
    }

    protected String getConfigureFunction(Configuration config) {
        return getConfigureFunction(config, "druid", DruidConstants.FUNCTION_CONFIGURE);
    }

    @Override
    protected void beforeStart(ClusterActionEvent event) throws IOException {
        addStatement(event, call("start_druid"));
    }

    @Override
    protected void beforeStop(ClusterActionEvent event) throws IOException {
        addStatement(event, call("stop_druid"));
    }

    @Override
    protected void beforeCleanup(ClusterActionEvent event) throws IOException {
        addStatement(event, call("cleanup_druid"));
    }
}