package edu.ucsb.cs156.organic.repositories;

import edu.ucsb.cs156.organic.entities.School;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface SchoolRepository extends CrudRepository<School, String> {
    // You can add custom query methods here if needed
    // Optional<School> findById(String id);
}
