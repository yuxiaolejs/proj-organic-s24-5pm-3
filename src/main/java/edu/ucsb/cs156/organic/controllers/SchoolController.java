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


    @Operation(summary= "List all schools")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<School> allSchools() {
        Iterable<School> schools = schoolRepository.findAll();
        return schools;
    }

    @Operation(summary= "Get a single school by abbreviation")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public School getById(
        @Parameter(name="abbrev") @RequestParam String abbrev) {
        Optional<School> schoolOptional = schoolRepository.findById(abbrev);
        School school = schoolOptional.orElseThrow(() -> new EntityNotFoundException(School.class, abbrev));
        return school;
    }
}
