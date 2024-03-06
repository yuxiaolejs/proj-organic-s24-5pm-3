package edu.ucsb.cs156.organic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.organic.errors.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;
import javax.transaction.Transactional;
import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ucsb.cs156.organic.entities.School;
import edu.ucsb.cs156.organic.entities.User;
import edu.ucsb.cs156.organic.repositories.SchoolRepository;
import edu.ucsb.cs156.organic.repositories.UserRepository;


@Tag(name = "school")
@RequestMapping("/api/schools")
@RestController
@Slf4j // what does this do
public class SchoolController extends ApiController{
    @Autowired
    SchoolRepository schoolRepository;

    @Autowired
    ObjectMapper mapper;
    
    @Autowired
    UserRepository userRepository;



    @Operation(summary= "Create a new school")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public School postSchool(
        @Parameter(name="abbrev") @RequestParam String abbrev,
        @Parameter(name="name") @RequestParam String name,
        @Parameter(name="termRegex") @RequestParam String termRegex,
        @Parameter(name="termDescription") @RequestParam String termDescription,
        @Parameter(name="termError") @RequestParam String termError)
        {

        School school = School.builder().build();
        school.setAbbrev(abbrev);
        school.setName(name);
        school.setTermRegex(termRegex);
        school.setTermDescription(termDescription);
        school.setTermError(termError);

        School savedSchool = schoolRepository.save(school);

        return savedSchool;
    }




    @Operation(summary= "Delete a school")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteSchool(
            @Parameter(name="abbrev") @RequestParam String abbrev) {
                School school = schoolRepository.findById(abbrev)
                .orElseThrow(() -> new EntityNotFoundException(School.class, abbrev));

        schoolRepository.delete(school);
        return genericMessage("School with id %s deleted".formatted(abbrev));
    }



    @Operation(summary = "Update information for a school")
    // allow for roles of ADMIN or INSTRUCTOR but only if the user is a staff member
    // for the course
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @PutMapping("/update")
    public School updateSchool(
            @Parameter(name = "abbrev") @RequestParam String abbrev,
            @RequestBody @Valid School incoming) {

                School school = schoolRepository.findById(abbrev)
                .orElseThrow(() -> new EntityNotFoundException(School.class, abbrev));

        school.setName(incoming.getName());
        school.setTermRegex(incoming.getTermRegex());
        school.setTermDescription(incoming.getTermDescription());
        school.setTermError(incoming.getTermError());

        schoolRepository.save(school);

        log.info("school={}", school);

        return school;
    }

}
