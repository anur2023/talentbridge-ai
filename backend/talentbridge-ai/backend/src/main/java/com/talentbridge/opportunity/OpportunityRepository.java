package com.talentbridge.opportunity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OpportunityRepository extends MongoRepository<Opportunity, String> {
    Optional<Opportunity> findBySlug(String slug);
    boolean existsBySlug(String slug);
    Page<Opportunity> findByStatus(OpportunityStatus status, Pageable pageable);
    List<Opportunity> findByStatusAndDeadlineBefore(OpportunityStatus status, LocalDate date);
    List<Opportunity> findByStatusAndVerifiedAtBeforeAndStaleFalse(OpportunityStatus status, LocalDate date);
}
