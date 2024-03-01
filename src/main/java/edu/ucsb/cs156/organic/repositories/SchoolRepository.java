package edu.ucsb.cs156.organic.repositories;

import edu.ucsb.cs156.organic.entities.School;
import edu.ucsb.cs156.organic.entities.Staff;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends CrudRepository<Staff, String> {
    Optional<School> findByName(String name);  
    Iterable<School> findByTermRegex(String termRegex);
    Iterable<School> findByTermDescription(String termDescription);
    Iterable<School> findByTermError(String termError);
    Iterable<School> findByTermRegexAndTermDescription(String termRegex, String termDescription);
    Iterable<School> findByTermRegexAndTermError(String termRegex, String termError);
    Iterable<School> findByTermDescriptionAndTermError(String termDescription, String termError);
}