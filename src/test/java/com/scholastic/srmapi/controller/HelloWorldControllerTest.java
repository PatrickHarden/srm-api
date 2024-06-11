package com.scholastic.srmapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HelloWorldControllerTest {

    private HelloWorldController controller = new HelloWorldController();

    @Test
    @DisplayName("It should return `Hello, World!`")
    void helloWorld() {
        assertEquals("Hello, World!", controller.helloWorld());
    }

}
