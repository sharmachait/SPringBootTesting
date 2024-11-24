package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

@WebMvcTest(controllers = UsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class UsersControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    UsersService usersService;

    UserDetailsRequestModel userDetailsRequestModel;
    UserDto userDtoMapped;

    @BeforeEach
    void setUp() {
        userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("John");
        userDetailsRequestModel.setLastName("Doe");
        userDetailsRequestModel.setEmail("john@doe.com");
        userDetailsRequestModel.setPassword("password");
        userDetailsRequestModel.setRepeatPassword("password");

        //mocking the service layer
//        UserDto userDto = new UserDto();
//        userDto.setFirstName("John");
//        userDto.setLastName("Doe");
//        userDto.setEmail("john@doe.com");
//        userDto.setUserId(UUID.randomUUID().toString());
    }

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidDetailsProvided_returnsCreatedUserDetails() throws Exception {
        //arrange
        userDtoMapped = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDtoMapped.setUserId(UUID.randomUUID().toString());

        Mockito.when(usersService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDtoMapped);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
        //act
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String response = result.getResponse().getContentAsString();
        // the controller returns a user object serialized into a json object which we can deserialize back to user object
        UserRest user = new ObjectMapper().readValue(response, UserRest.class);

        // assert
        Assertions.assertNotNull(user);
        Assertions.assertEquals(userDetailsRequestModel.getFirstName(), user.getFirstName());
        Assertions.assertEquals(userDetailsRequestModel.getLastName(), user.getLastName());
        Assertions.assertEquals(userDetailsRequestModel.getEmail(), user.getEmail());
        Assertions.assertNotNull(user.getUserId());
        Assertions.assertEquals(userDtoMapped.getUserId(), user.getUserId());
        Assertions.assertNotEquals("", user.getUserId());
        Assertions.assertFalse(user.getUserId().isEmpty());
    }

    @Test
    @DisplayName("User can not be created")
    void testCreateUser_whenInValidDetailsProvided_returns400StatusCode() throws Exception {
        //arrange
        userDetailsRequestModel.setEmail("johndoecom"); // Invalid email
        userDtoMapped = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDtoMapped.setUserId(UUID.randomUUID().toString());

        Mockito.when(usersService.createUser(Mockito.any(UserDto.class)))
                .thenReturn(userDtoMapped);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));
        //act
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String response = result.getResponse().getContentAsString();

        // assert
        Assertions.assertEquals("", response);
        Assertions.assertEquals(400, result.getResponse().getStatus());
    }
}
