function start_druid() {

    # Make logs directory
    mkdir /usr/local/druid-services-0.5.7/logs

    # Run the realtime node
    nohup java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 -Ddruid.realtime.specFile=/usr/local/druid-services-0.5.7/config/realtime/realtime.spec -classpath /usr/local/druid-services-0.5.7/lib/druid-services-0.5.7-selfcontained.jar:/usr/local/druid-services-0.5.7/config/realtime com.metamx.druid.realtime.RealtimeMain 2>&1 > /usr/local/druid-services-0.5.7/logs/realtime.log &

    # And a master node
    nohup java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 -classpath /usr/local/druid-services-0.5.7/lib/druid-services-0.5.7-selfcontained.jar:/usr/local/druid-services-0.5.7/config/master com.metamx.druid.http.MasterMain 2>&1 > /usr/local/druid-services-0.5.7/logs/master.log &

    # And a compute node
    nohup java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 -classpath /usr/local/druid-services-0.5.7/lib/druid-services-0.5.7-selfcontained.jar:/usr/local/druid-services-0.5.7/config/compute com.metamx.druid.http.ComputeMain 2>&1 > /usr/local/druid-services-0.5.7/logs/compute.log &

    # And a broker node
    nohup java -Xmx256m -Duser.timezone=UTC -Dfile.encoding=UTF-8 -classpath /usr/local/druid-services-0.5.7/lib/druid-services-0.5.7-selfcontained.jar:/usr/local/druid-services-0.5.7/config/broker com.metamx.druid.http.BrokerMain 2>&1 > /usr/local/druid-services-0.5.7/logs/broker.log &

}