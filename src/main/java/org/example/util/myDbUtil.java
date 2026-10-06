package org.example.util;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class myDbUtil {

        private final DynamoDbClient dynamoDb;
        private final String tableName = "myDb";

        public myDbUtil() {
            this.dynamoDb = DynamoDbClient.builder().build();
        }

        public Map<String, Object> getLambda(String id) {

            Map<String, AttributeValue> key = Map.of(
                    "id",
                    AttributeValue.builder()
                            .s(id)
                            .build()
            );

            GetItemResponse response = dynamoDb.getItem(
                    GetItemRequest.builder()
                            .tableName(tableName)
                            .key(key)
                            .build()
            );

            if (response.hasItem()) {
                return convertToSimpleJson(response.item());
            }

            return Map.of(
                    "message", "Item not found",
                    "id", id
            );
        }

        private Map<String, Object> convertToSimpleJson(
                Map<String, AttributeValue> item) {

            Map<String, Object> result = new HashMap<>();

            item.forEach((key, value) -> {
                if (value.s() != null) {
                    result.put(key, value.s());
                } else if (value.n() != null) {
                    result.put(key, value.n());
                } else if (value.bool() != null) {
                    result.put(key, value.bool());
                } else if (value.hasM()) {
                    result.put(key, convertToSimpleJson(value.m()));
                } else if (value.hasL()) {
                    result.put(key, value.l());
                }
            });

            return result;
        }
    }
