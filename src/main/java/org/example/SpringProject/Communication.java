package org.example.SpringProject;

import org.example.SpringProject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class Communication {
    private final RestTemplate restTemplate;
    private final String url = "http://94.198.50.185:7081/api/users/";

    @Autowired
    public Communication(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<User> getAllUsers() {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return responseEntity.getBody();
    }

    public void saveUser(User user) {
        if (user.getId() == 0) {
            restTemplate.postForEntity(url, user, String.class);
        } else {
            restTemplate.put(url, user);
        }
    }

    public void deleteUser(Long id) {
        restTemplate.delete(url + id);
    }
}