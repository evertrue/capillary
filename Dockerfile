# Build stage
FROM ubuntu:20.04 AS builder

# Set non-interactive mode for debconf
ENV DEBIAN_FRONTEND=noninteractive

# Install build dependencies
RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    build-essential \
    wget \
    unzip \
    git \
    openjdk-8-jdk \
    curl \
    gnupg \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Install SBT 0.13.8
RUN mkdir -p /usr/local/sbt && \
    wget -qO - "https://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/sbt-launch/0.13.8/sbt-launch.jar" > /usr/local/sbt/sbt-launch.jar && \
    echo '#!/bin/bash' > /usr/local/sbt/sbt && \
    echo 'java -Xmx512M -jar /usr/local/sbt/sbt-launch.jar "$@"' >> /usr/local/sbt/sbt && \
    chmod +x /usr/local/sbt/sbt && \
    ln -s /usr/local/sbt/sbt /usr/local/bin/sbt

# Set SBT options
ENV SBT_OPTS="-Xmx2G -XX:+UseG1GC -Dsbt.log.noformat=true"

# Build the application
WORKDIR /capillary
COPY . ./
RUN sbt clean update compile stage

# Runtime stage
FROM ubuntu:20.04

# Set non-interactive mode for debconf
ENV DEBIAN_FRONTEND=noninteractive

# Install runtime dependencies
RUN apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/* && \
    sed -i 's/http:/https:/g' /etc/apt/sources.list && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    openjdk-8-jre-headless \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy the built application from builder
COPY --from=builder /capillary/target/universal/stage /opt/capillary
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Set environment variables
ENV JAVA_OPTS="-Xmx128M"
ENV PATH="/opt/capillary/bin:${PATH}"

# Add metadata
LABEL maintainer="Evertrue"
LABEL version="1.2"
LABEL description="Capillary - Kafka monitoring service"

# Expose the application port
EXPOSE 9000

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:9000/health || exit 1
