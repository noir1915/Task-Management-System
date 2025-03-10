package com.example.Task_Management_System;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(profiles = {"test"})
@SpringBootTest(properties = {
        "command.line.runner.enabled=false"})  // exclude DataLoader
class TmsApplicationTests {

    @Test
    void contextLoads() {
    }

}
