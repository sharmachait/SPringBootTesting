package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsersControllerIntegrationTest {
    @LocalServerPort
    private int serverPort;
    UserDetailsRequestModel userDetailsRequestModel;
    UserDto userDtoMapped;
    private static String token;
    @BeforeEach
    void setUp() {
        userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("John");
        userDetailsRequestModel.setLastName("Doe");
        userDetailsRequestModel.setEmail("john@doe.com");
        userDetailsRequestModel.setPassword("password");
        userDetailsRequestModel.setRepeatPassword("password");
    }
    @Order(1)
    @Test
    @DisplayName("User can be created")
    void testCreateUser_WhenValidDetails_thenReturnUser() throws JsonProcessingException {
        //arrange
        WebClient webClient = WebClient.create();
        //act
        UserRest user = webClient.post()
                .uri("http://localhost:" + serverPort + "/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDetailsRequestModel)
                .retrieve()
                .bodyToMono(UserRest.class)
                .block();
        //assert
        assertNotNull(user, "User object should not be null");
        assertNotNull(user.getUserId());
        assertEquals(userDetailsRequestModel.getFirstName(), user.getFirstName(), "First name should match");
        assertEquals(userDetailsRequestModel.getLastName(), user.getLastName(), "Last name should match");
        assertEquals(userDetailsRequestModel.getEmail(), user.getEmail(), "Email should match");
    }
    @Order(2)
    @Test
    @DisplayName("GET /user requires JWT")
    void testGetUser_whenMissingJwt_returns403() {
        //arrange
        WebClient webClient = WebClient.create();
        //act
        ResponseEntity<Object> response = webClient.get()
                .uri("http://localhost:" + serverPort + "/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse ->
                        Mono.just(ResponseEntity.status(clientResponse.statusCode()).build())
                )
                .block();

        assertEquals(403, response.getStatusCode().value());
    }

    @Order(3)
    @Test
    @DisplayName("login user")
    void testUserLogin_whenValidCredentials_returnsJWT() throws JsonProcessingException, JSONException {
        //arrange
        JSONObject loginCred = new JSONObject();
        loginCred.put("email", "john@doe.com");
        loginCred.put("password", "password");

        WebClient webClient = WebClient.create();
        //act
        ResponseEntity<String> response = webClient.post()
                .uri("http://localhost:" + serverPort + "/users/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginCred.toString())
                .retrieve()
                .toEntity(String.class)
                .block();


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get(SecurityConstants.HEADER_STRING));
        assertFalse(response.getHeaders().get(SecurityConstants.HEADER_STRING).isEmpty());

        String authHeader = response.getHeaders().get(SecurityConstants.HEADER_STRING).get(0);
        assertTrue(authHeader.startsWith("Bearer "));
        this.token = authHeader;
    }

    @Order(4)
    @Test
    @DisplayName("fetch list of users")
    void testFetchUsers_whenJwtTokenProvided_returnsListUsers() throws Exception {
        System.out.println(this.token);
        WebClient webClient = WebClient.create();
        List<UserRest> users = webClient.get()
                .uri("http://localhost:" + serverPort + "/users")
                .accept(MediaType.APPLICATION_JSON)
                .header(SecurityConstants.HEADER_STRING, token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserRest>>() {})
                .block();
        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
}
