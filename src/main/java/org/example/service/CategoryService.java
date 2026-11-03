package org.example.service;

import org.example.entity.Category;
import org.example.util.DynamoDbUtil;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryService {

    //test
    private final DynamoDbUtil dynamoDbUtil;

    public CategoryService() {
        this.dynamoDbUtil = new DynamoDbUtil("ItemCategories", Region.US_EAST_1);
    }

    public List<Category> getAllCategories() {

        List<Map<String, Object>> rawCategories = dynamoDbUtil.getAllItems();


        return convertToCategories(rawCategories);
    }

    public List<Category> convertToCategories(List<Map<String, Object>> rawCategories) {
        List<Category> categories = new ArrayList<>();

        for (Map<String, Object> rawCategory: rawCategories) {
            Category category = new Category();

            // Extract values from the raw item map and set them to the Item object

            // Category ID - Assuming CAtegoryId is an Integer
            if (rawCategory.containsKey("id") && rawCategory.get("id") != null) {
                category.setId((rawCategory.get("id").toString()));  // Convert String to Integer
            }

            if (rawCategory.containsKey("name") && rawCategory.get("name") != null) {
                category.setName((rawCategory.get("name").toString()));  // Convert String to Integer
            }


            categories.add(category);
        }

        // Log the items for debugging
        System.out.println("categories:"+categories);

        return  categories;
    }


    private static class CategoriesNotFoundException extends RuntimeException {

        public CategoriesNotFoundException(String message) {
            super(message);

        }
}

    public String updateCategory(Category category) {
        // Validate input
        if (category == null) {
            throw new IllegalArgumentException(" category cannot be null or empty.");
        }

        Map<String, Object> existingCategory= dynamoDbUtil.readItem(String.valueOf(category.getId()));
        if(existingCategory== null || existingCategory.isEmpty()){
           // throw new CategoryService.CategoriesNotFoundException("Category with id"+ category.getId() + "is not found");
        return null;
        }

        try {
            Map<String, AttributeValue> input = new HashMap<>();
            input.put("id", AttributeValue.builder().s(category.getId()).build());
            input.put("name", AttributeValue.builder().s(category.getName()).build());

            dynamoDbUtil.writeItem(input);
            System.out.println("Category successfully written to DynamoDB: " + input);
            return category.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error writing Category to DynamoDB: " + e.getMessage(), e);
        }
    }

   /** public String createCategory(Category categoryValue) {
        // Validate input
        if (categoryValue == null) {
            throw new IllegalArgumentException(" categoryValue cannot be null or empty.");
        }

        try {
            Map<String, AttributeValue> input = new HashMap<>();
            String nextCategoryId = dynamoDbUtil.getNextCategoryId();
            input.put("id", AttributeValue.builder().s(nextCategoryId).build());
            input.put("name", AttributeValue.builder().s(categoryValue.getName()).build());

            dynamoDbUtil.writeItem(input);
            System.out.println("Category successfully written to DynamoDB: " + input);
            return nextCategoryId;
        } catch (Exception e) {
            throw new RuntimeException("Error writing Category to DynamoDB: " + e.getMessage(), e);
        }
    }**/

   public String createCategory(Category categoryValue) {
       // Validate input
       if (categoryValue == null) {
           throw new IllegalArgumentException("categoryValue cannot be null.");
       }

       try {
           Map<String, AttributeValue> input = new HashMap<>();
           String nextCategoryId = dynamoDbUtil.getNextCategoryId();

           // Add only allowed fields to the input
           input.put("id", AttributeValue.builder().s(nextCategoryId).build());
           input.put("name", AttributeValue.builder().s(categoryValue.getName()).build());

           dynamoDbUtil.writeItem(input);
           System.out.println("Category successfully written to DynamoDB: " + input);
           return nextCategoryId;
       } catch (Exception e) {
           throw new RuntimeException("Error writing Category to DynamoDB: " + e.getMessage(), e);
       }
   }


    public String deleteCategoryById(String id) {
        // Validate input
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty.");
        }

        try {
            // Perform delete operation using DynamoDB utility class
            String deletedCategory = dynamoDbUtil.deleteCategory(id);

            if (deletedCategory != null) {
                System.out.println("Category successfully deleted from DynamoDB: " + id);
                return id;
            } else {
                System.out.println("Category with ID " + id + " not found.");
                return null; // Indicating category was not found
            }
        } catch (Exception e) {
            // Log and rethrow the exception with additional context
            String errorMessage = "Error deleting Category from DynamoDB: " + e.getMessage();
            System.err.println(errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

}
