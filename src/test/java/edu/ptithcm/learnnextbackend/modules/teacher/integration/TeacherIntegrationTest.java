package edu.ptithcm.learnnextbackend.modules.teacher.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import edu.ptithcm.learnnextbackend.config.TestJacksonConfig;

import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ptithcm.learnnextbackend.modules.teacher.dto.request.CreateTeacherRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Import({TestJacksonConfig.class})
@ActiveProfiles("test")
public class TeacherIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    
    //SUCCESS FLOW
    @Test 
    void should_create_teacher_successfully() throws Exception {
        CreateTeacherRequest req = CreateTeacherRequest.builder() 
                .email("teacher1@gmail.com") 
                .password("12345678") 
                .fullName("Teacher A") 
                .avatarUrl("avatar.png") 
                .bio("Hello") 
                .build(); 
        mockMvc.perform(post("/api/v1/teachers/new") 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content(objectMapper.writeValueAsString(req))) 
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.email").value(req.getEmail())) 
                .andExpect(jsonPath("$.fullName").value(req.getFullName())) 
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    //EMAIL EMPTY
    @Test 
    void should_return_400_when_email_is_blank() throws Exception {
        CreateTeacherRequest req = CreateTeacherRequest.builder() 
                .email("") 
                .password("12345678") 
                .fullName("Teacher A") 
                .build(); 
        mockMvc.perform(post("/api/v1/teachers/new") 
                        .contentType(MediaType.APPLICATION_JSON) 
                        .content(objectMapper.writeValueAsString(req))) 
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.errors").exists());
    }

    //DUPLICATE EMAIL
    @Test 
    void should_return_400_when_email_already_exists() throws Exception {
        String email = "duplicate_test@gmail.com"; 
        CreateTeacherRequest req = CreateTeacherRequest.builder() 
                .email(email) 
                .password("12345678") 
                .fullName("Teacher A") 
                .build();
        //first call
                mockMvc.perform(post("/api/v1/teachers/new") 
                .contentType(MediaType.APPLICATION_JSON) 
                .content(objectMapper.writeValueAsString(req))) 
                .andExpect(status().isCreated()); 
        //second call 
        mockMvc.perform(post("/api/v1/teachers/new") 
                .contentType(MediaType.APPLICATION_JSON) 
                .content(objectMapper.writeValueAsString(req))) 
                .andExpect(status().isBadRequest()); 
    }
}
