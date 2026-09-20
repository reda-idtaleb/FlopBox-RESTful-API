package com.flopbox;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class AuthenticationIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("flopbox")
            .withUsername("flopbox")
            .withPassword("flopbox");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("app.security.jwt.secret", () -> "ZGV2ZWxvcG1lbnQtand0LXNlY3JldC1tdXN0LWJlLTMyYg==");
        registry.add("app.security.jwt.issuer", () -> "flopbox-tests");
    }

    @Test
    void registrationIssuesTokenAndProtectsServerRoutes() throws Exception {
        String response = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"integration-user\",\"password\":\"a-secure-test-password\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = com.jayway.jsonpath.JsonPath.read(response, "$.accessToken");

        mockMvc.perform(get("/api/v1/servers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/v1/servers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateUsernameReturnsConflict() throws Exception {
        String request = "{\"username\":\"duplicate-user\",\"password\":\"a-secure-test-password\"}";

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("https://flopbox.dev/problems/conflict"));
    }
}
