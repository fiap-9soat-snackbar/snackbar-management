#!/bin/bash
# This script creates the SQS queue in LocalStack during container initialization

echo "Creating SQS queue: product-events"
awslocal sqs create-queue --queue-name product-events

echo "SQS queue created successfully"
awslocal sqs list-queues
