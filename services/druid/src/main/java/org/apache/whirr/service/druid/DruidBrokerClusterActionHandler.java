package org.apache.whirr.service.druid;

import org.apache.commons.configuration.Configuration;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandler;

import java.io.IOException;

import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.apache.whirr.RolePredicates.role;
import static org.apache.whirr.service.druid.DruidConfigurationBuilder.buildDruidConfig;
import static org.jclouds.scriptbuilder.domain.Statements.call;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.whirr.Cluster;
import org.apache.whirr.Cluster.Instance;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.FirewallManager.Rule;
import org.apache.whirr.service.zookeeper.ZooKeeperCluster;
import org.apache.whirr.template.TemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: rjurney
 * Date: 7/17/13
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class DruidBrokerClusterActionHandler extends DruidClusterActionHandler {

    public static final String ROLE = "druid-broker";

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
                getInstallFunction(getConfiguration(clusterSpec)),
                DruidConstants.PARAM_TARBALL_URL, tarurl)
        );
    }
}
