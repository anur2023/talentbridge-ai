package com.talent.backend.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserAccount, String> {

	Optional<UserAccount> findByEmailNormalized(String emailNormalized);

	boolean existsByEmailNormalized(String emailNormalized);
}
