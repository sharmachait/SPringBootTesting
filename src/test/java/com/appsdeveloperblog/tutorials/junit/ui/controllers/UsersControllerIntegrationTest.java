package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=5000","hostname=localhost"})
public class UsersControllerIntegrationTest {
    @Value("${server.port}")
    private int serverPort;
}
