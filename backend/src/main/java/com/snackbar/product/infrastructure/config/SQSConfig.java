package com.snackbar.product.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * Configuration for AWS SQS.
 */
@Configuration
public class SQSConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSConfig.class);
    
    @Value("${aws.region}")
    private String region;
    
    /**
     * Creates an ObjectMapper bean with JavaTimeModule registered.
     * This is necessary for proper serialization/deserialization of Java 8 date/time types.
     * 
     * @return The configured ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    
    /**
     * Creates an SQS client using AWS SDK default credential provider chain.
     * 
     * @return The SQS client
     */
    @Bean
    public SqsClient sqsClient() {
        try {
            logger.info("Creating SQS client for AWS region: {}", region);
            
            SqsClient client = SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
                
            logger.info("SQS client created successfully");
            return client;
        } catch (Exception e) {
            logger.error("Error creating SQS client", e);
            throw e;
        }
    }
}
