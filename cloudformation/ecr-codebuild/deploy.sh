#!/bin/bash

echo -n "Enter ToolsAccount ProfileName for AWS Cli operations [evertruetools] > "
read ToolsAccountProfile

ToolsAccountProfile=${ToolsAccountProfile:-evertruetools}

StackName=et-capillary
ECRRepositoryName=evertrue/et-capillary
ECRRepositoryDatadogName=evertrue/et-capillary-datadog
GitHubProjectName=capillary

aws cloudformation deploy --stack-name $StackName --template-file ecr-codebuild.yaml --parameter-overrides ECRRepositoryName=$ECRRepositoryName ECRRepositoryDatadogName=$ECRRepositoryDatadogName GitHubProjectName=$GitHubProjectName --profile $ToolsAccountProfile
