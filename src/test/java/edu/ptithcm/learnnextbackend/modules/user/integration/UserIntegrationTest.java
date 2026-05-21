package edu.ptithcm.learnnextbackend.modules.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.ptithcm.learnnextbackend.modules.user.dto.request.CreateUserRequest; 
import edu.ptithcm.learnnextbackend.modules.user.dto.response.UpdateUserRequest; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import edu.ptithcm.learnnextbackend.config.TestJacksonConfig;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import({ TestJacksonConfig.class})
@ActiveProfiles("test")
public class UserIntegrationTest {
    @Autowired private MockMvc mockMvc; @Autowired private ObjectMapper objectMapper; 
    
    //CREATE SUCCESS 
    @Test
    void should_create_user_successfully() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("user1@gmail.com");
        req.setPassword("123456");
        req.setFullName("User A");
        mockMvc.perform(post("/api/v1/users/new").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(req.getEmail()))
                .andExpect(jsonPath("$.fullName").value(req.getFullName()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    //VALIDATION EMAIL FAIL 
    @Test
    void should_return_400_when_email_invalid() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("invalid");
        req.setPassword("123456");
        req.setFullName("User A");
        mockMvc.perform(post("/api/v1/users/new").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    //GET ALL USERS 
    @Test
    void should_get_all_users() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isOk());
    }

    // FULL FLOW
    @Test
    void full_flow_user_crud() throws Exception { // CREATE
        CreateUserRequest createReq = new CreateUserRequest();
        createReq.setEmail("flow@gmail.com");
        createReq.setPassword("123456");
        createReq.setFullName("Flow User");
        String response = mockMvc
                .perform(post("/api/v1/users/new").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        UUID id = UUID.fromString(com.jayway.jsonpath.JsonPath.read(response, "$.id"));
        // GET BY ID
        mockMvc.perform(get("/api/v1/users/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("flow@gmail.com"));
        // UPDATE
        UpdateUserRequest updateReq = new UpdateUserRequest();
        updateReq.setFullName("Updated Name");
        mockMvc.perform(patch("/api/v1/users/" + id).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq))).andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"));
        // DELETE
        mockMvc.perform(delete("/api/v1/users/" + id)).andExpect(status().isNoContent());
        // GET AGAIN
        mockMvc.perform(get("/api/v1/users/" + id)).andExpect(status().isBadRequest());
    }

    // DUPLICATE EMAIL
    @Test
    void should_return_400_when_email_exists() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setEmail("duplicate@gmail.com");
        req.setPassword("123456");
        req.setFullName("User A");
        // first
        mockMvc.perform(post("/api/v1/users/new").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));
        // second
        mockMvc.perform(post("/api/v1/users/new").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    //empty email
    @Test
    void should_return_400_when_email_blank() throws Exception {
            CreateUserRequest req = new CreateUserRequest();
            req.setEmail("");
            req.setPassword("123456");
            req.setFullName("User A");

            mockMvc.perform(post("/api/v1/users/new")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                            .andExpect(status().isBadRequest());
    }

    //short password
    @Test
    void should_return_400_when_password_too_short() throws Exception {
            CreateUserRequest req = new CreateUserRequest();
            req.setEmail("user@gmail.com");
            req.setPassword("123");
            req.setFullName("User A");

            mockMvc.perform(post("/api/v1/users/new")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                            .andExpect(status().isBadRequest());
    }

    //blank fullname
    @Test
    void should_return_400_when_fullname_blank() throws Exception {
            CreateUserRequest req = new CreateUserRequest();
            req.setEmail("user@gmail.com");
            req.setPassword("123456");
            req.setFullName("");

            mockMvc.perform(post("/api/v1/users/new")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                            .andExpect(status().isBadRequest());
    }

    //update fullname blank
    @Test
    void should_return_400_when_update_fullname_blank() throws Exception {
            UUID id = UUID.randomUUID();

            UpdateUserRequest req = new UpdateUserRequest();
            req.setFullName("");

            mockMvc.perform(patch("/api/v1/users/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                            .andExpect(status().isBadRequest());
    }
}
