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


// Tests for DELETE /api/schools?id=... 


    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_school() throws Exception {
            // arrange


            School school1 = School.builder()
                            .abbrev("UCSB")
                            .name("University of California Santa Barbara")
                            .termRegex("W")
                            .termDescription("W24")
                            .termError("term error??")
                            .build();
                            

            when(schoolRepository.findById(eq("UCSB"))).thenReturn(Optional.of(school1));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/schools?abbrev=UCSB")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(schoolRepository, times(1)).findById("UCSB");
            verify(schoolRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("School with id UCSB deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_school_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(schoolRepository.findById(eq("UCSB"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/schools?abbrev=UCSB")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(schoolRepository, times(1)).findById("UCSB");
                Map<String, Object> json = responseToJson(response);
                assertEquals("School with id UCSB not found", json.get("message"));
        }


    // Tests for PUT /api/schools?id=... 

//     @WithMockUser(roles = { "ADMIN", "USER" })
//     @Test
//     public void admin_can_edit_an_existing_school() throws Exception {
//             // arrange
    
//             School schoolOrig = School.builder()
//                             .abbrev("filler10")
//                             .name("filler20")
//                             .termRegex("filler30")
//                             .termDescription("filler40")
//                             .termError("filler50")
//                             .build();
                            

//             School schoolEdited = School.builder()
//                             .abbrev("filler1")
//                             .name("filler2")
//                             .termRegex("filler3")
//                             .termDescription("filler4")
//                             .termError("filler5")
//                             .build();

//             String requestBody = mapper.writeValueAsString(schoolEdited);

//             when(schoolRepository.findById(eq(67L))).thenReturn(Optional.of(schoolOrig));

//             // act
//             MvcResult response = mockMvc.perform(
//                             put("/api/schools?id=67")
//                                             .contentType(MediaType.APPLICATION_JSON)
//                                             .characterEncoding("utf-8")
//                                             .content(requestBody)
//                                             .with(csrf()))
//                             .andExpect(status().isOk()).andReturn();

//             // assert
//             verify(schoolRepository, times(1)).findById(67L);  
//             verify(schoolRepository, times(1)).save(schoolEdited); // should be saved with correct user
//             String responseString = response.getResponse().getContentAsString();
//             assertEquals(requestBody, responseString);
//     }

    
//     @WithMockUser(roles = { "ADMIN", "USER" })
//     @Test
//     public void admin_cannot_edit_school_that_does_not_exist() throws Exception {
//             // arrange
//             School editedSchool = School.builder()
//                             .abbrev("filler100")
//                             .name("filler200")
//                             .termRegex("filler300")
//                             .termDescription("filler400")
//                             .termError("filler500")
//                             .build();

//             String requestBody = mapper.writeValueAsString(editedSchool);

//             when(schoolRepository.findById(eq(67L))).thenReturn(Optional.empty());

//             // act
//             MvcResult response = mockMvc.perform(
//                             put("/api/helprequests?id=67")
//                                             .contentType(MediaType.APPLICATION_JSON)
//                                             .characterEncoding("utf-8")
//                                             .content(requestBody)
//                                             .with(csrf()))
//                             .andExpect(status().isNotFound()).andReturn();

//             // assert
//             verify(schoolRepository, times(1)).findById(67L);
//             Map<String, Object> json = responseToJson(response);
//             assertEquals("School with id 67 not found", json.get("message"));

//     }
    
}
