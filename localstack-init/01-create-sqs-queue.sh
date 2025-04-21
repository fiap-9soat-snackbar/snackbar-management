#!/bin/bash
# This script creates the SQS queue in LocalStack during container initialization

echo "Creating SQS queue: snackbar-management-product-events-queue"
awslocal sqs create-queue --queue-name snackbar-management-product-events-queue

echo "Creating SQS DLQ: snackbar-management-product-events-dlq"
awslocal sqs create-queue --queue-name snackbar-management-product-events-dlq

echo "SQS queues created successfully"
awslocal sqs list-queues
