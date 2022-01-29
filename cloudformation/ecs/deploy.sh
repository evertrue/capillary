#!/bin/bash

echo -n "Enter environment [stage|prod] > "
read EnvType

Host="capillary.evertrue.com"
ZookeeperHosts="prod-zookeeper-1c.priv.evertrue.com:2181,prod-zookeeper-1d.priv.evertrue.com:2181,prod-zookeeper-1b.priv.evertrue.com:2181"
AccountProfile="evertrue${EnvType}"
StackName="ecs-cluster-${EnvType}-DevOpsServices-Capillary"
Datadog="-Dcapillary.metrics.datadog.apiKey=cd20be2b109bd4eb7413f071afb01a8d"

if [ "$EnvType" = "stage" ]; then
  Host="stage-capillary.evertrue.com"
  ZookeeperHosts="stage-zookeeper-1c.priv.evertrue.com:2181,stage-zookeeper-1d.priv.evertrue.com:2181,stage-zookeeper-1b.priv.evertrue.com:2181"
  Datadog=""
fi

aws cloudformation deploy --stack-name $StackName --template-file ecs-service.yaml --parameter-overrides EnvType=$EnvType Host=$Host ZookeeperHosts=$ZookeeperHosts Datadog=$Datadog --profile $AccountProfile
