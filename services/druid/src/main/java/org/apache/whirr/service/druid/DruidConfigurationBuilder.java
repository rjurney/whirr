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

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.whirr.Cluster;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.service.zookeeper.ZooKeeperCluster;
import org.jclouds.scriptbuilder.domain.Statement;

import java.io.IOException;

import static org.apache.whirr.RolePredicates.role;

public class DruidConfigurationBuilder {
    public static Configuration buildDruidConfig(String path, ClusterSpec clusterSpec, Cluster cluster)
            throws ConfigurationException, IOException {
        PropertiesConfiguration propsConfig = new PropertiesConfiguration(DruidConfigurationBuilder.class.getResource(
            "/" + DruidConstants.FILE_DRUID_DEFAULT_PROPERTIES)
        );
        Configuration config = buildDruidConfiguration(clusterSpec, cluster, propsConfig);
        return config;
    }

    private static Configuration build(ClusterSpec clusterSpec, Cluster cluster, Configuration defaults, String prefix)
            throws ConfigurationException {
        CompositeConfiguration config = new CompositeConfiguration();
        Configuration sub = clusterSpec.getConfigurationForKeysWithPrefix(prefix);
        config.addConfiguration(sub.subset(prefix)); // remove prefix
        config.addConfiguration(defaults.subset(prefix));
        return config;
    }

    static Configuration buildDruidConfiguration(ClusterSpec clusterSpec, Cluster cluster, Configuration defaults)
            throws ConfigurationException, IOException {
        Configuration config = build(clusterSpec, cluster, defaults, "whirr");

        config.setProperty("druid.zk.service.host", ZooKeeperCluster.getHosts(cluster));

        return config;
    }
}
