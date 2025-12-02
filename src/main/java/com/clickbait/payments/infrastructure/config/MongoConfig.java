package com.clickbait.payments.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.clickbait.payments.infrastructure.adapters.out.persistence")
@EnableMongoAuditing
public class MongoConfig {}