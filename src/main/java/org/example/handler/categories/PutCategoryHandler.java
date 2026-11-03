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

public class PutCategoryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

        private CategoryService categoryService;
        private final Gson gson;

        public PutCategoryHandler() {
            this.categoryService = new CategoryService();
            this.gson = new Gson(); // Initialize Gson once
        }

        public PutCategoryHandler(CategoryService categoryService) {
            this.categoryService = categoryService;
            this.gson = new Gson(); // Initialize Gson once
        }

        @Override
        public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
            LambdaLogger logger = context.getLogger();
            logger.log("Received input: " + input);

            try {
                validateRequest(input);
                Category categoryRequest = parseCategory(input.getBody());
                String id = input.getPathParameters().get("id");

                categoryRequest.setId(id);
                String responseCategoryId = categoryService.updateCategory(categoryRequest);
if(responseCategoryId != null) {
    return createResponse(200,
            String.format("{\"message\":\"Category successfully updated\", \"categoryId\":\"%s\"}", responseCategoryId));
} else{
                return createResponse(404, String.format("{\"error\":\"Category with ID %s not found\"}", id));
            }
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
            return gson.fromJson(body, Category.class);
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

