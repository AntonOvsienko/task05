package com.task05;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@DynamoDbTriggerEventSource(targetTable = "Events", batchSize = 10)
@LambdaHandler(lambdaName = "api_handler",
        roleName = "api_handler-role",
        aliasName = "target_table"
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String TABLE_NAME = "Events";

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = input.getBody();

        try {
            String eventId = UUID.randomUUID().toString();
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createdAt = formatter.format(date);
            AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.defaultClient();
            DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            String principalId = jsonNode.get("principalId").toString();
            String body = jsonNode.get("content").toString();
            Item eventItem = new Item().withPrimaryKey("id", eventId)
                    .withNumber("principalId", Integer.parseInt(principalId))
                    .withString("createdAt", createdAt)
                    .withString("body", body);
            Table eventsTable = dynamoDB.getTable(TABLE_NAME);
            PutItemOutcome outcome = eventsTable.putItem(eventItem);
            response.setStatusCode(201);
            response.setBody(String.format("{\"id\": \"%s\", \"principalId\": %d, \"createdAt\": \"%s\", \"body\": %s}",
                    eventId, Integer.parseInt(principalId), createdAt, body));
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("Error: " + e);
        }

        return response;
    }
}
