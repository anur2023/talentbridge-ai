package com.talentbridge.opportunity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlatformRepository extends MongoRepository<Platform, String> {
    Page<Platform> findAll(Pageable pageable);
}
