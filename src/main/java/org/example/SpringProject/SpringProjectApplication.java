package org.example.SpringProject;

import org.example.SpringProject.configuration.Config;
import org.example.SpringProject.model.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class SpringProjectApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        RestTemplate restTemplate = context.getBean(RestTemplate.class);

        String url = "http://94.198.50.185:7081/api/users";

        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {
                }
        );
        List<User> allUsers = responseEntity.getBody();
        String sessionId = responseEntity.getHeaders().getFirst("Set-Cookie");
        System.out.println("session ID: " + sessionId);
        System.out.println("all users: " + allUsers);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        User newUser = new User(3L, "James", "Brown", (byte) 25);
        HttpEntity<User> requestEntity = new HttpEntity<>(newUser, headers);

        System.out.println("send request to save user: " + newUser);
        String firstPartCode = null;

        try {
            ResponseEntity<String> saveResponse = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            firstPartCode = saveResponse.getBody();
            System.out.println("1 part of code: " + firstPartCode);
        } catch (HttpClientErrorException e) {
            System.out.println("error response: " + e.getResponseBodyAsString());
            throw e;
        }

        User updatedUser = new User(3L, "Thomas", "Shelby", (byte) 30); // Обновляем данные пользователя
        HttpEntity<User> updateRequestEntity = new HttpEntity<>(updatedUser, headers);

        System.out.println("send request to update user: " + updatedUser);

        ResponseEntity<String> updateResponse = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                updateRequestEntity,
                String.class
        );
        String secondPartCode = updateResponse.getBody();
        System.out.println("2 part of code: " + secondPartCode);

        HttpEntity<String> deleteRequestEntity = new HttpEntity<>(headers);

        System.out.println("send request to delete user with id: 3");

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                url + "/3",
                HttpMethod.DELETE,
                deleteRequestEntity,
                String.class
        );
        String thirdPartCode = deleteResponse.getBody();
        System.out.println("3 part of code: " + thirdPartCode);

        String finalCode = firstPartCode + secondPartCode + thirdPartCode;
        System.out.println("final code: " + finalCode);
    }
}