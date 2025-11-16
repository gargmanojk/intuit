#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-refund-status-service \
--package-name=com.intuit.turbotax.refundstatus \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-refund-status-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-filing-metadata-service \
--package-name=com.intuit.turbotax.filingmetadata \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-filing-metadata-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-refund-status-aggregator-service \
--package-name=com.intuit.turbotax.service.refund.status.aggregator \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-refund-status-aggregator-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-ai-refund-eta-service \
--package-name=com.intuit.turbotax.service.ai.refund.eta \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-ai-refund-eta-service

cd ..

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-refund-service-api \
--package-name=com.intuit.turbotax.api \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-refund-service-api

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-refund-service-util \
--package-name=com.intuit.turbotax.util \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-refund-service-util

cd ..