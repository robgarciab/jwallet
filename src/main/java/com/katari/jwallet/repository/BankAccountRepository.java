package com.katari.jwallet.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.katari.jwallet.domain.BankAccount;

/**
 * Spring Data MongoDB repository for the BankAccount entity.
 */
public interface BankAccountRepository extends MongoRepository<BankAccount,String> {

	Page<BankAccount> findByUserIdAndActive(String userId, Boolean active, Pageable pageable);
	
	Optional<BankAccount> findOneByNumber(String number);
}
