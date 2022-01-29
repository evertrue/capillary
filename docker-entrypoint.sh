#!/bin/bash

export DATADOG=$(wget -q -O - http://169.254.169.254/latest/meta-data/local-ipv4)

/capillary/capillary-1.2/bin/capillary -Dcapillary.zookeepers=${ZOOKEEPER} -Dcapillary.kafka.zkroot="/kafka" -Dcapillary.storm.zkroot="/storm-kafka" ${DATADOG}
