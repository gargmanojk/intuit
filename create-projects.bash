#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=refund-status-service \
--package-name=com.intuit.turbotax.refund.status \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
refund-status-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=filing-metadata-service \
--package-name=com.intuit.turbotax.filing.metadata \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
filing-metadata-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=refund-status-aggregator-service \
--package-name=com.intuit.turbotax.refund.status.aggregator \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
refund-status-aggregator-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=ai-refund-eta-service \
--package-name=com.intuit.turbotax.ai.refund.eta \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
ai-refund-eta-service

cd ..