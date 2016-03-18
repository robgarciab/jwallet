package com.katari.jwallet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.katari.jwallet.domain.Transaction;

/**
 * Spring Data MongoDB repository for the Transaction entity.
 * Transactions are immutable updates and deletes are not allowed
 */
public interface TransactionRepository extends ImmutableRepository<Transaction,String> {

	Page<Transaction> findByBankAccountId(String bankAccountId, Pageable pageable);
}
