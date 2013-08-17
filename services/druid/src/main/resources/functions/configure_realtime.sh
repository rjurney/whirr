#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
function configure_realtime() {

  ZOOKEEPER_QUORUM=$1

  cat > /usr/local/druid-services-0.5.7/config/realtime/realtime.spec <<EOF

[{
  "schema" : { "dataSource":"druidtest",
               "aggregators":[ {"type":"count", "name":"impressions"},
                                  {"type":"doubleSum","name":"wp","fieldName":"wp"}],
               "indexGranularity":"minute",
           "shardSpec" : { "type": "none" } },
  "config" : { "maxRowsInMemory" : 500000,
               "intermediatePersistPeriod" : "PT10m" },
  "firehose" : { "type" : "kafka-0.7.2",
                 "consumerProps" : { "zk.connect" : "$ZOOKEEPER_QUORUM",
                                     "zk.connectiontimeout.ms" : "15000",
                                     "zk.sessiontimeout.ms" : "15000",
                                     "zk.synctime.ms" : "5000",
                                     "groupid" : "topic-pixel-local",
                                     "fetch.size" : "1048586",
                                     "autooffset.reset" : "largest",
                                     "autocommit.enable" : "false" },
                 "feed" : "druidtest",
                 "parser" : { "timestampSpec" : { "column" : "utcdt", "format" : "iso" },
                              "data" : { "format" : "json" },
                              "dimensionExclusions" : ["wp"] } },
  "plumber" : { "type" : "realtime",
                "windowPeriod" : "PT10m",
                "segmentGranularity":"hour",
                "basePersistDirectory" : "/tmp/realtime/basePersist",
                "rejectionPolicy": {"type": "messageTime"} }

}]
EOF

}