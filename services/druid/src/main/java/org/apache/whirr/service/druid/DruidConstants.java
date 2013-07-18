package org.apache.whirr.service.druid;

/**
 * Created with IntelliJ IDEA.
 * User: rjurney
 * Date: 7/17/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class DruidConstants {
    public static final String KEY_TARBALL_URL = "whirr.druid.tarball.url";

    public static final String FUNCTION_INSTALL = "install_druid";
    public static final String FUNCTION_CONFIGURE = "configure_druid";

    public static final String PARAM_MASTER = "-m";
    public static final String PARAM_QUORUM = "-q";
    public static final String PARAM_PORT = "-p";
    public static final String PARAM_TARBALL_URL = "-u";

    public static final String PROP_DRUID_ZOOKEEPER_QUORUM = "druid.zookeeper.quorum";
    public static final String PROP_DRUID_ZOOKEEPER_CLIENTPORT = "druid.zookeeper.property.clientPort";

    public static final String FILE_DRUID_SITE_XML = "hbase-site.xml";
    public static final String FILE_DRUID_DEFAULT_PROPERTIES = "whirr-druid-default.properties";

    private DruidConstants() {
    }
}
