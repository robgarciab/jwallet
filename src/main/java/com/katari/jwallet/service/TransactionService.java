package com.katari.jwallet.service;

import java.math.BigDecimal;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.katari.jwallet.domain.BankAccount;
import com.katari.jwallet.domain.Transaction;
import com.katari.jwallet.repository.TransactionRepository;

/**
 * Service Implementation for managing Transaction.
 */
@Service
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);
    
    @Inject
    private TransactionRepository transactionRepository;
    
    @Inject
    private BankAccountService bankAccountService;
    
    /**
     * Save a transaction.
     * @return the persisted entity
     */
    public Transaction save(Transaction transaction) {
        return save(transaction, Boolean.FALSE);
    }
    
    /**
     * Save a transaction for a new account
     * @return the persisted entity
     */
    public Transaction save(Transaction transaction, Boolean isNewAccount) {
        log.debug("Request to save Transaction : {}", transaction);
        
        // If account is new -> don't update BankAccount balance
        if (!isNewAccount) {
        	BankAccount account = bankAccountService.findOne(transaction.getBankAccountId());
        	transaction.setBankAccountNumber(account.getNumber());
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            bankAccountService.save(account);
        }
        
        if (transaction.getAmount().compareTo(new BigDecimal(0)) > 0) {
        	transaction.setDescription("Deposit to your account");
        } else {
        	transaction.setDescription("Expense");
        }
        
        Transaction result = transactionRepository.save(transaction);
        return result;
    }
    
    /**
     *  get all the transactions.
     *  @return the list of entities
     */
    public Page<Transaction> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        Page<Transaction> result = transactionRepository.findAll(pageable); 
        return result;
    }
    
    /**
     *  get the transactions for a bankAccount.
     *  @param bankAccountId
     *  @return the list of entities
     */
    public Page<Transaction> findByBankAccountNumber(String bankAccountNumber, Pageable pageable) {
        log.debug("Request to get Transactions by bankAccountNumber");
        Optional<BankAccount> bankAccount = bankAccountService.findOneByNumber(bankAccountNumber);
        if (bankAccount.isPresent()) {
        	Page<Transaction> result = transactionRepository.findByBankAccountId(bankAccount.get().getId(), pageable);
            return result;
        } else {
        	throw new IllegalArgumentException();
        }
    }

    /**
     *  get one transaction by id.
     *  @return the entity
     */
    public Transaction findOne(String id) {
        log.debug("Request to get Transaction : {}", id);
        Transaction transaction = transactionRepository.findOne(id);
        return transaction;
    }

    /**
     *  delete the  transaction by id.
     */
    public void delete(String id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.delete(id);
    }
}
