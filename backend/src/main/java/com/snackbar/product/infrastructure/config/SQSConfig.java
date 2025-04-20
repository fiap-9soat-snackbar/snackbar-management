package com.snackbar.product.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.snackbar.product.infrastructure.messaging.ProductMessageMapper;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import java.net.URI;

/**
 * Configuration for AWS SQS client.
 * This class sets up the connection to AWS SQS using the AWS SDK.
 */
@Configuration
public class SQSConfig {
    
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    @Value("${aws.endpoint.url:}")
    private String awsEndpointUrl;
    
    @Value("${aws.access.key:}")
    private String awsAccessKey;
    
    @Value("${aws.secret.key:}")
    private String awsSecretKey;
    
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
     * Registers the JavaTimeModule to handle Java 8 date/time types like Instant.
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
     * Creates an SQS client bean for production environment.
     *
     * @return The configured SQS client
     */
    @Bean
    @Profile("prod")
    public SqsClient sqsClient() {
        // Create the SQS client builder
        var builder = SqsClient.builder();
        
        // Set the region
        builder.region(Region.of(awsRegion));
            
        // If endpoint URL is provided, use it (for LocalStack)
        if (awsEndpointUrl != null && !awsEndpointUrl.isEmpty()) {
            builder.endpointOverride(URI.create(awsEndpointUrl));
        }
        
        // If credentials are provided, use them
        if (awsAccessKey != null && !awsAccessKey.isEmpty() && 
            awsSecretKey != null && !awsSecretKey.isEmpty()) {
            builder.credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(awsAccessKey, awsSecretKey)
                )
            );
        }
        
        return builder.build();
    }
}
