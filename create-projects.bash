#!/usr/bin/env bash

mkdir microservices
cd microservices

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-refundstatus-service \
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
--name=turbotax-filinginfo-service \
--package-name=com.intuit.turbotax.filingmetadata \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-filinginfo-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-refundinfo-service \
--package-name=com.intuit.turbotax.aggregator \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-refundinfo-service

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-etarefund-service \
--package-name=com.intuit.turbotax.aieta \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-etarefund-service

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

spring init \
--boot-version=3.5.0 \
--type=gradle-project \
--java-version=24 \
--packaging=jar \
--name=turbotax-domain-model \
--package-name=com.intuit.turbotax.domainmodel \
--groupId=com.intuit.turbotax \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
turbotax-domain-model

cd ..