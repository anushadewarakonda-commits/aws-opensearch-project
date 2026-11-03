package org.example.handler.categories;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import org.example.service.CategoryService;

import java.util.HashMap;
import java.util.Map;

public class DeleteCategoryHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private CategoryService categoryService;
    private final Gson gson;

    public DeleteCategoryHandler() {
        this.categoryService = new CategoryService();
        this.gson = new Gson(); // Initialize Gson once
    }

    public DeleteCategoryHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
        this.gson = new Gson(); // Initialize Gson once
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Received input: " + input);

        try {
            // Validate path parameter 'id'
            if (input.getPathParameters() == null || !input.getPathParameters().containsKey("id")) {
                throw new IllegalArgumentException("Path parameter 'id' is required");
            }

            String id = input.getPathParameters().get("id");

            // Log the category ID being deleted
            logger.log(String.format("Deleting category with ID: %s", id));

            String responseCategoryId = categoryService.deleteCategoryById(id);
            if (responseCategoryId != null) {
                return createResponse(200,
                        String.format("{\"message\":\"Category Successfully Deleted\", \"categoryId\":\"%s\"}", responseCategoryId));
            } else {
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
