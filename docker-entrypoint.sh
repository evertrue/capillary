#!/bin/bash

export AWS_METADATA_TOKEN=$(curl -s --request PUT "http://169.254.169.254/latest/api/token" --header "X-aws-ec2-metadata-token-ttl-seconds: 3600")
export DD_AGENT_HOST=$(curl -s http://169.254.169.254/latest/meta-data/local-ipv4 --header "X-aws-ec2-metadata-token: $AWS_METADATA_TOKEN")
export EC2_INSTANCE_ID=$(curl -s http://169.254.169.254/latest/meta-data/instance-id --header "X-aws-ec2-metadata-token: $AWS_METADATA_TOKEN")
export DD_ENV="$APP_ENV"

RUN_CMD="capillary -Dcapillary.zookeepers=${ZOOKEEPER} -Dcapillary.kafka.zkroot=/kafka -Dcapillary.storm.zkroot=/storm-kafka ${DATADOG}"

echo "Git commit: ${GIT_COMMIT}"
echo "Running on EC2 ${EC2_INSTANCE_ID}"
echo "Running: $RUN_CMD"

exec $RUN_CMD
