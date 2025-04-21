package com.snackbar.product.infrastructure.config;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

/**
 * Configuration for AWS SQS.
 */
@Configuration
public class SQSConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SQSConfig.class);
    
    @Value("${aws.region}")
    private String region;
    
    @Value("${aws.endpoint.url:#{null}}")
    private String endpointUrl;
    
    @Value("${aws.access.key:#{null}}")
    private String accessKey;
    
    @Value("${aws.secret.key:#{null}}")
    private String secretKey;
    
    @Value("${dev.aws.region}")
    private String devRegion;
    
    @Value("${dev.aws.endpoint.url}")
    private String devEndpointUrl;
    
    @Value("${dev.aws.access.key}")
    private String devAccessKey;
    
    @Value("${dev.aws.secret.key}")
    private String devSecretKey;
    
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
     * Creates an SQS client for the dev profile (LocalStack).
     * 
     * @return The SQS client
     */
    @Bean
    @Profile("dev")
    public SqsClient sqsClientDev() {
        try {
            logger.info("Creating SQS client for LocalStack in region: {}", devRegion);
            logger.info("Using LocalStack endpoint: {}", devEndpointUrl);
            logger.info("Using LocalStack access key: {}", devAccessKey);
            
            AwsBasicCredentials credentials = AwsBasicCredentials.create(devAccessKey, devSecretKey);
            
            SqsClientBuilder builder = SqsClient.builder()
                .region(Region.of(devRegion))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .endpointOverride(URI.create(devEndpointUrl));
            
            SqsClient client = builder.build();
            logger.info("LocalStack SQS client created successfully: {}", client);
            return client;
        } catch (Exception e) {
            logger.error("Error creating LocalStack SQS client", e);
            throw e;
        }
    }
    
    /**
     * Creates an SQS client for the aws-local profile (real AWS).
     * 
     * @return The SQS client
     */
    @Bean
    @Profile("aws-local")
    public SqsClient sqsClientAwsLocal() {
        try {
            logger.info("Creating SQS client for AWS region: {}", region);
            
            // Force the endpoint to be the standard SQS endpoint for the region
            String sqsEndpoint = "https://sqs." + region + ".amazonaws.com";
            logger.info("Using SQS endpoint: {}", sqsEndpoint);
            
            SqsClientBuilder builder = SqsClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .endpointOverride(URI.create(sqsEndpoint));
            
            SqsClient client = builder.build();
            logger.info("SQS client created successfully: {}", client);
            return client;
        } catch (Exception e) {
            logger.error("Error creating SQS client", e);
            throw e;
        }
    }
}
