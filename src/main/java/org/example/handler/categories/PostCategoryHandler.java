package org.example.handler.categories;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import org.example.entity.Category;
import org.example.service.CategoryService;

import java.util.HashMap;
import java.util.Map;

public class PostCategoryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

        private CategoryService categoryService;
        private final Gson gson;

        public PostCategoryHandler() {
            this.categoryService = new CategoryService();
            this.gson = new Gson(); // Initialize Gson once
        }

        public PostCategoryHandler(CategoryService categoryService) {
            this.categoryService = categoryService;
            this.gson = new Gson(); // Initialize Gson once
        }


    @Override
        public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
            LambdaLogger logger = context.getLogger();
            logger.log("Received input: " + input);

            try {
                validateRequest(input);

                Category categoryValue = parseCategory(input.getBody());
                System.out.println(categoryValue);
                String categoryId = categoryService.createCategory(categoryValue);

                return createResponse(200,
                        String.format("{\"message\":\"Category successfully created\", \"categoryId\":\"%s\"}", categoryId));
            } catch (IllegalArgumentException e) {
                logger.log("Validation error: " + e.getMessage());
                return createResponse(400, String.format("{\"error\":\"%s\"}", e.getMessage()));
            } catch (Exception e) {
                logger.log("Error: " + e.getMessage());
                return createResponse(500, "{\"error\":\"Internal server error\"}");
            }
        }

        private void validateRequest(APIGatewayProxyRequestEvent input) {
            if (input.getBody() == null || input.getBody().trim().isEmpty()) {
                throw new IllegalArgumentException("Request body is required");
            }
        }

        private Category parseCategory(String body) {
            try {
                Category response=gson.fromJson(body, Category.class);
                System.out.println("converted response"+response.toString());
                return response;
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid request body format: " + e.getMessage());
            }
        }

        private Map<String, String> getDefaultHeaders() {
            Map<String, String> headers = new HashMap<>();
            headers.put("Access-Control-Allow-Origin", "*");
            headers.put("Access-Control-Allow-Methods", "OPTIONS,GET,POST");
            headers.put("Access-Control-Allow-Headers", "Content-Type");
            return headers;
        }

        private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setHeaders(getDefaultHeaders()); // Include default CORS headers
            response.setStatusCode(statusCode);
            response.setBody(body);
            return response;
        }



    }
