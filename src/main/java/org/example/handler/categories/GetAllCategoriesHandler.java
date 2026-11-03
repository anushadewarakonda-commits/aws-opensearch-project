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
import java.util.List;
import java.util.Map;

public class GetAllCategoriesHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CategoryService categoryService;
    private final Gson gson;

    public GetAllCategoriesHandler() {
        this.categoryService = new CategoryService();
        this.gson = new Gson(); // Initialize Gson once
    }
     public GetAllCategoriesHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
        this.gson = new Gson();
    }


      @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Received input: " + input);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,GET,POST");
        headers.put("Access-Control-Allow-Headers", "Content-Type");
        response.setHeaders(headers);

        try {
            List<Category> categories;
            categories = categoryService.getAllCategories();

            System.out.println("categories2:" + categories);
            if (categories == null || categories.isEmpty()) {
                logger.log("No categories found.");
                response.setStatusCode(404);
                response.setBody("{\"error\":\"No categories found\"}");
                return response;
            }

            Gson gson = new Gson();
            response.setStatusCode(200);
            response.setBody(gson.toJson(categories));
        } catch (Exception e) {
            logger.log("Error while retrieving categories: " + e.getMessage());
            response.setStatusCode(500);
            response.setBody("{\"error\":\"Internal server error\"}");
        }

        return response;
    }
}
