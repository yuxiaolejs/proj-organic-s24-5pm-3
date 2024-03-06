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


    // Tests for PUT /api/schools?id=... 

    @WithMockUser(roles = { "INSTRUCTOR", "USER" })
    @Test
    public void an_instructor_user_can_update_a_school_if_they_are_admin() throws Exception {
        // arrange


        School origSchool = School.builder()
                        .abbrev("ucsb")
                        .name("Ubarbara")
                        .termRegex("W24")
                        .termDescription("F24")
                        .termError("error")
                        .build();
        School editedSchool = School.builder()
                        .abbrev("ucsb")
                        .name("UBarbara")
                        .termRegex("M24")
                        .termDescription("S24")
                        .termError("error1")
                        .build();

        String requestBody = mapper.writeValueAsString(editedSchool);

        when(schoolRepository.findById(eq("ucsb"))).thenReturn(Optional.of(origSchool));
        when(schoolRepository.save(eq(origSchool))).thenReturn(origSchool);

        // act
        // get urlTemplate from courseAfter using string interpolation
        // String urlTemplate = String.format(
        //         "/api/schools/update?abbrev=%s&name=%s&termRegex=%s&termDescription=%s&termError=%s",
        //         editedSchool.getAbbrev(), editedSchool.getName(), editedSchool.getTermRegex(), editedSchool.getTermDescription(),
        //         editedSchool.getTermError());
        MvcResult response = mockMvc.perform(
                put("/api/schools/update?abbrev=ucsb")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(schoolRepository, times(1)).findById("ucsb");
        verify(schoolRepository, times(1)).save(editedSchool);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(requestBody, responseString);
    }
        
    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_school_that_does_not_exist() throws Exception {
            // arrange
            School editedSchool = School.builder()
                            .abbrev("ucsb")
                            .name("Ubarbara")
                            .termRegex("W24")
                            .termDescription("F24")
                            .termError("error")
                            .build();

            String requestBody = mapper.writeValueAsString(editedSchool);

            when(schoolRepository.findById(eq("ucsb"))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/schools/update?abbrev=ucsb")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(schoolRepository, times(1)).findById("ucsb");
            Map<String, Object> json = responseToJson(response);
            assertEquals("School with id ucsb not found", json.get("message"));
    }
        
}