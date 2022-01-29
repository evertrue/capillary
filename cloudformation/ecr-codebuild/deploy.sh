#!/bin/bash

echo -n "Enter ToolsAccount ProfileName for AWS Cli operations [evertruetools] > "
read ToolsAccountProfile

ToolsAccountProfile=${ToolsAccountProfile:-evertruetools}

StackName=et-capillary
ECRRepositoryName=evertrue/et-capillary
GitHubProjectName=capillary

aws cloudformation deploy --stack-name $StackName --template-file ecr-codebuild.yaml --parameter-overrides ECRRepositoryName=$ECRRepositoryName GitHubProjectName=$GitHubProjectName --profile $ToolsAccountProfile
