FROM ubuntu:14.04

RUN echo "deb http://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list

RUN apt-get update && apt-get install -y --force-yes build-essential wget unzip git openjdk-7-jdk sbt

RUN git clone https://github.com/evertrue/capillary.git \
  && cd capillary \
  && sbt update \
  && sbt universal:package-zip-tarball \
  && tar -xf target/universal/capillary-1.2.tgz

EXPOSE 9000

CMD /capillary/capillary-1.2/bin/capillary -Dcapillary.zookeepers=${ZOOKEEPER} -Dcapillary.kafka.zkroot="/kafka" -Dcapillary.storm.zkroot="/storm-kafka"