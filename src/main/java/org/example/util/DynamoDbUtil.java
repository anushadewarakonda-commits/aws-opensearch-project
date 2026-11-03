package org.example.util;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDbUtil {

    private static final String COUNTER_TABLE_NAME = "OrdersCounter";
    private static String TABLE_NAME = "YourTableName";
    private static Region REGION = Region.US_EAST_1; // Change to your region

    DynamoDbClient dynamoDbClient;

    public DynamoDbUtil(String tableName, Region region){
        TABLE_NAME = tableName;
        REGION = region;
        dynamoDbClient = DynamoDbClient.builder()
                .region(REGION)
                .build();
    }

    public void writeItem(Map<String, AttributeValue> itemValues) {

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME)
                .item(itemValues)
                .build();

        try {
            dynamoDbClient.putItem(request);
            System.out.println("Successfully added item: " + itemValues.get("id"));
        } catch (DynamoDbException e) {
            System.out.println("Failed to put item: " + e.getMessage());
        }
    }


    public String getNextCategoryId() {
        // Define the key for the counter table
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("counter", AttributeValue.builder().n("1").build()); // Partition key

        // Define the update expression to increment the counter
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":start", AttributeValue.builder().n("0").build()); // Default start value
        expressionAttributeValues.put(":increment", AttributeValue.builder().n("1").build()); // Increment value

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .tableName(COUNTER_TABLE_NAME)
                .key(key)
                .updateExpression("SET #val = if_not_exists(#val, :start) + :increment")
                .expressionAttributeNames(Map.of("#val", "value")) // Map for attribute names
                .expressionAttributeValues(expressionAttributeValues) // Map for attribute values
                .returnValues(ReturnValue.UPDATED_NEW) // Return the new value of the counter
                .build();

        // Execute the update and get the new counter value
        try {
            UpdateItemResponse response = dynamoDbClient.updateItem(updateItemRequest);
            String newId = response.attributes().get("value").n();

            System.out.println("Generated Category ID: " + newId);
            return newId;
        } catch (DynamoDbException e) {
            System.err.println("Error updating counter: " + e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> readItem(String id) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", AttributeValue.builder().s(id).build());

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .build();

        try {
            GetItemResponse response = dynamoDbClient.getItem(request);
            if (response.hasItem()) {
                System.out.println("Retrieved item: " + response.item());
                // Convert to simple JSON
                return convertToSimpleJson(response.item());
            } else {
                System.out.println("Item not found.");
            }
        } catch (DynamoDbException e) {
            System.err.println("Failed to get item: " + e.getMessage());
        }
        return null;
    }


    public String  deleteCategory(String id){
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("id", AttributeValue.builder().s(id).build());

            DeleteItemRequest request = DeleteItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(key)
                    .build();

            try {
                DeleteItemResponse  response= dynamoDbClient.deleteItem(request);
                if(response.hasAttributes()){
                    System.out.println("Item with ID '" + id + "' deleted successfully.");
                }
                else{
                    System.out.println("Item with ID '" + id + "' not found.");
                    return null;

                }


            } catch (DynamoDbException e) {
                System.err.println("Failed to delete item with ID '" + id + "': " + e.getMessage());
                return null; // Indicate failure by returning null
            }

            return id;
        }

    public List<Map<String, Object>> getAllItems() {
        List<Map<String, Object>> items = new ArrayList<>();

        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .build();

        try {
            ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
            List<Map<String, AttributeValue>> dynamoItems = scanResponse.items();

            for (Map<String, AttributeValue> dynamoItem : dynamoItems) {
                items.add(convertToSimpleJson(dynamoItem));
            }

            System.out.println("Retrieved " + items.size() + " items from the table.");
        } catch (DynamoDbException e) {
            System.err.println("Failed to retrieve items: " + e.getMessage());
        }

        return items;
    }

    private Map<String, Object> convertToSimpleJson(Map<String, AttributeValue> dynamoDbItem) {
        Map<String, Object> simpleJson = new HashMap<>();
        dynamoDbItem.forEach((key, value) -> {
            if (value.s() != null) {
                simpleJson.put(key, value.s());
            } else if (value.n() != null) {
                simpleJson.put(key, Integer.parseInt(value.n()));
            } else if (value.bool() != null) {
                simpleJson.put(key, value.bool());
            } else if (value.l() != null) {
                simpleJson.put(key, value.l().stream()
                        .map(this::convertAttributeValue)
                        .toList());
            } else if (value.m() != null) {
                simpleJson.put(key, convertToSimpleJson(value.m()));
            }
        });
        return simpleJson;
    }

    private Object convertAttributeValue(AttributeValue value) {
        if (value.s() != null) return value.s();
        if (value.n() != null) return Integer.parseInt(value.n());
        if (value.bool() != null) return value.bool();
        if (value.m() != null) return convertToSimpleJson(value.m());
        if (value.l() != null) return value.l().stream().map(this::convertAttributeValue).toList();
        return null;
    }


}