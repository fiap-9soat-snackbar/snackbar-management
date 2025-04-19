package com.snackbar.product.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snackbar.product.infrastructure.messaging.ProductMessageMapper;

/**
 * Configuration for AWS SQS client.
 * This class sets up the connection to AWS SQS using the AWS SDK.
 */
@Configuration
public class SQSConfig {
    
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    /**
     * Creates a ProductMessageMapper bean.
     *
     * @return The configured ProductMessageMapper
     */
    @Bean
    public ProductMessageMapper productMessageMapper() {
        return new ProductMessageMapper();
    }
    
    /**
     * Creates an ObjectMapper bean for JSON serialization/deserialization.
     *
     * @return The configured ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
