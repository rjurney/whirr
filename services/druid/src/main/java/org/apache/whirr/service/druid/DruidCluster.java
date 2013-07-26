package org.apache.whirr.service.druid;

import org.apache.whirr.Cluster;
import org.apache.whirr.RolePredicates;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created with IntelliJ IDEA.
 * User: rjurney
 * Date: 7/17/13
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DruidCluster {

    public static InetAddress getBrokerPublicAddress(Cluster cluster) throws IOException {
        return cluster.getInstanceMatching(
                RolePredicates.role(DruidBrokerClusterActionHandler.ROLE))
                .getPublicAddress();
    }
}
