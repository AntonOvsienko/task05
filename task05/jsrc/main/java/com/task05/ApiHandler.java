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

//@DynamoDbTriggerEventSource(targetTable = "Events", batchSize = 10)
@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role"
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final String TABLE_NAME = "Events";

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		response.setBody("It DB OK");

//		try {
//			// Инициализация клиента DynamoDB
//			AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.defaultClient();
//			DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);
//
//			// Получение данных из входного запроса
//			String eventData = input.getBody();
//
//			// Создание нового элемента для таблицы
//			Item eventItem = new Item().withPrimaryKey("eventId", java.util.UUID.randomUUID().toString())
//					.withString("eventData", eventData);
//
//			// Сохранение элемента в таблице DynamoDB
//			Table eventsTable = dynamoDB.getTable(TABLE_NAME);
//			PutItemOutcome outcome = eventsTable.putItem(eventItem);
//
//			// Формирование ответа
//			response.setStatusCode(200);
//			response.setBody("Event created successfully. Event ID: " + eventItem.getString("eventId"));
//		} catch (Exception e) {
//			// Обработка ошибок
//			response.setStatusCode(500);
//			response.setBody("Error: " + e.getMessage());
//		}

		return response;
	}
}
