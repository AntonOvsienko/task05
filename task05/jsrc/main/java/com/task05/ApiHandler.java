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
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;

import java.time.Instant;
import java.util.UUID;

@DynamoDbTriggerEventSource(targetTable = "Events", batchSize = 10)
@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role"
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final String TABLE_NAME = "Events";

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		try {
			// Получение данных из входного запроса
			String requestBody = input.getBody();
			int principalId = input.getPathParameters().containsKey("principalId") ? Integer.parseInt(input.getPathParameters().get("principalId")) : -1;

			// Генерация UUID v4 для id события
			String eventId = UUID.randomUUID().toString();

			// Получение текущей временной метки в формате ISO 8601
			String createdAt = Instant.now().toString();

			// Инициализация клиента DynamoDB
			AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.defaultClient();
			DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

			// Создание нового элемента для таблицы
			Item eventItem = new Item().withPrimaryKey("id", eventId)
					.withNumber("principalId", principalId)
					.withString("createdAt", createdAt)
					.withJSON("body", requestBody);

			// Сохранение элемента в таблице DynamoDB
			Table eventsTable = dynamoDB.getTable(TABLE_NAME);
			PutItemOutcome outcome = eventsTable.putItem(eventItem);

			// Формирование ответа
			response.setStatusCode(201);
			response.setBody(String.format("{\"id\": \"%s\", \"principalId\": %d, \"createdAt\": \"%s\", \"body\": %s}",
					eventId, principalId, createdAt, requestBody));
		} catch (Exception e) {
			// Обработка ошибок
			e.printStackTrace(); // Это добавлено для вывода ошибки в логи AWS Lambda
			response.setStatusCode(500);
			response.setBody("Error - exception: " + e.getMessage());
		}

		return response;
	}
}
