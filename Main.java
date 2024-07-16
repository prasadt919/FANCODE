package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class Main {

    // This is the main test method which will be executed
    @Test
    public void findAndPrintUsersInFanCodeCity() {
        // Send a GET request to retrieve users
        Response usersResponse = RestAssured.get("http://jsonplaceholder.typicode.com/users");

        // Check if the response status is not 200 (OK)
        if (usersResponse.getStatusCode() != 200) {
            System.out.println("Failed to retrieve user information");
            return;
        }

        // Extract the users list from the response
        List<Map<String, Object>> users = usersResponse.jsonPath().getList("$");

        // Iterate through each user
        for (Map<String, Object> user : users) {
            // Check if the user is located in FanCode City
            if (isUserInFanCodeCity(user)) {
                // Print the user details
                printUserDetails(user);
            }
        }
    }

    // This method checks if the user is located in FanCode City
    private boolean isUserInFanCodeCity(Map<String, Object> user) {
        Map<String, Object> address = (Map<String, Object>) user.get("address");
        if (address == null) return false;

        Map<String, Object> geo = (Map<String, Object>) address.get("geo");
        if (geo == null) return false;

        String latitudeStr = (String) geo.get("lat");
        String longitudeStr = (String) geo.get("lng");
        if (latitudeStr == null || longitudeStr == null) return false;

        try {
            double latitude = Double.parseDouble(latitudeStr);
            double longitude = Double.parseDouble(longitudeStr);
            return latitude >= -40 && latitude <= 5 && longitude >= 5 && longitude <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // This method prints the user's details and their completed task percentage
    private void printUserDetails(Map<String, Object> user) {
        int userId = (int) user.get("id");
        System.out.println("User ID: " + userId);
        printUserCompletedTaskPercentage(userId);
    }

    // This method prints the percentage of completed tasks for a given user
    private void printUserCompletedTaskPercentage(int userId) {
        Response todosResponse = RestAssured.get("http://jsonplaceholder.typicode.com/todos?userId=" + userId);

        if (todosResponse.getStatusCode() != 200) {
            System.out.println("Failed to retrieve todo tasks for User ID: " + userId);
            return;
        }

        List<Map<String, Object>> todos = todosResponse.jsonPath().getList("$");
        long completedTasks = todos.stream().filter(todo -> (boolean) todo.get("completed")).count();
        double completedTaskPercentage = todos.size() > 0 ? (completedTasks * 100.0) / todos.size() : 0;
        System.out.println("Completed Task Percentage for User ID " + userId + ": " + completedTaskPercentage + "%");
    }
}