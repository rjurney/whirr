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

/**
 * Created with IntelliJ IDEA.
 * User: rjurney
 * Date: 7/17/13
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class DruidConfigurationBuilder {
    public static Configuration buildDruidConfig(String path, ClusterSpec clusterSpec, Cluster cluster)
            throws ConfigurationException, IOException {
        Configuration config = buildDruidConfiguration(clusterSpec, cluster,
                new PropertiesConfiguration(DruidConfigurationBuilder.class.getResource("/" + DruidConstants.FILE_DRUID_DEFAULT_PROPERTIES)));
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

        Cluster.Instance broker = cluster.getInstanceMatching(
                role(DruidBrokerClusterActionHandler.ROLE));
        String masterHostName = broker.getPublicHostName();

        config.setProperty("druid.zk.service.host", ZooKeeperCluster.getHosts(cluster));

        return config;
    }
}
