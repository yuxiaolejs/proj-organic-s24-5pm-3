package edu.ucsb.cs156.organic.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.awaitility.Awaitility.await;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import edu.ucsb.cs156.organic.entities.Course;
import edu.ucsb.cs156.organic.entities.School;

import edu.ucsb.cs156.organic.entities.Staff;
import edu.ucsb.cs156.organic.entities.User;
import edu.ucsb.cs156.organic.entities.jobs.Job;
import edu.ucsb.cs156.organic.repositories.CourseRepository;
import edu.ucsb.cs156.organic.repositories.StaffRepository;
import edu.ucsb.cs156.organic.repositories.UserRepository;
import edu.ucsb.cs156.organic.repositories.SchoolRepository;

import edu.ucsb.cs156.organic.repositories.jobs.JobsRepository;
import edu.ucsb.cs156.organic.services.jobs.JobService;
import edu.ucsb.cs156.organic.services.CurrentUserService;
import liquibase.pro.packaged.W;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

@Slf4j
@WebMvcTest(controllers = SchoolController.class)
@Import(JobService.class)
@AutoConfigureDataJpa
public class SchoolControllerTests extends ControllerTestCase{
    
    @MockBean
    UserRepository userRepository;

    @MockBean
    SchoolRepository schoolRepository;

    @MockBean
    StaffRepository courseStaffRepository;

    @Autowired
    CurrentUserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/schools/all")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/schools/all")).andExpect(status().is(200));
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
        mockMvc.perform(get("/api/schools").param("abbrev", "1L")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
        School school = School.builder()
                    .abbrev("ucsb")
                    .name("Ubarbara")
                    .termRegex("W24")
                    .termDescription("F24")
                    .termError("error")
                    .build();
        when(schoolRepository.findById(eq("ucsb"))).thenReturn(Optional.of(school));

        MvcResult response = mockMvc.perform(get("/api/schools").param("abbrev", "ucsb")).andExpect(status().isOk())
                .andReturn();

        verify(schoolRepository, times(1)).findById(eq("ucsb"));
        String expectedJson = objectMapper.writeValueAsString(school);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
        when(schoolRepository.findById(eq("umn"))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/schools").param("abbrev", "umn")).andExpect(status().isNotFound())
                .andReturn();

        verify(schoolRepository, times(1)).findById(eq("umn"));
        Map<String, Object> json = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {});
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("School with id umn not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_schools() throws Exception {
        School school1 = School.builder()
                    .abbrev("ucsb")
                    .name("Ubarbara")
                    .termRegex("W24")
                    .termDescription("F24")
                    .termError("error")
                    .build();        
        School school2 = School.builder()
                    .abbrev("umn")
                    .name("mich")
                    .termRegex("W24")
                    .termDescription("M24")
                    .termError("error1")
                    .build();  
        
        List<School> expectedSchools = Arrays.asList(school1, school2);

        when(schoolRepository.findAll()).thenReturn(expectedSchools);

        MvcResult response = mockMvc.perform(get("/api/schools/all")).andExpect(status().isOk()).andReturn();

        verify(schoolRepository, times(1)).findAll();
        String expectedJson = objectMapper.writeValueAsString(expectedSchools);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

}