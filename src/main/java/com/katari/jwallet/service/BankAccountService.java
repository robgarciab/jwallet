package com.katari.jwallet.service;

import java.util.Optional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.katari.jwallet.domain.BankAccount;
import com.katari.jwallet.domain.Transaction;
import com.katari.jwallet.domain.User;
import com.katari.jwallet.repository.BankAccountRepository;

/**
 * Service Implementation for managing BankAccount.
 */
@Service
public class BankAccountService {

    private final Logger log = LoggerFactory.getLogger(BankAccountService.class);
    
    @Inject
    private BankAccountRepository bankAccountRepository;
    
    @Inject
    private UserService userService;
    
    @Inject
    private TransactionService transactionService;
    
    /**
     * Save a bankAccount.
     * @return the persisted entity
     */
    @Transactional
    public BankAccount save(BankAccount bankAccount) {
        log.debug("Request to save BankAccount : {}", bankAccount);
        // if new account -> create transaction
        Transaction transaction = null;
        if (bankAccount.getId() == null) {
        	transaction = new Transaction();
        	transaction.setAmount(bankAccount.getBalance());
        	transaction.setBankAccountNumber(bankAccount.getNumber());
        }
        
        BankAccount result = bankAccountRepository.save(bankAccount);
        
        if (transaction != null) {
        	transaction.setBankAccountId(bankAccount.getId());
        	transactionService.save(transaction, true);
        }
        return result;
    }

    /**
     *  get all the bankAccounts.
     *  @return the list of entities
     */
    public Page<BankAccount> findAll(Pageable pageable) {
        log.debug("Request to get all BankAccounts");
        Page<BankAccount> result = bankAccountRepository.findAll(pageable); 
        return result;
    }
    
    /**
     *  get the bankAccounts for a user
     *  @param userId
     *  @return the list of entities
     */
    public Page<BankAccount> findByLogin(String login, Pageable pageable) throws IllegalArgumentException {
        log.debug("Request to get BankAccounts for user");
        Optional<User> user = userService.getUserByLogin(login);
        if (user.isPresent()) {
        	Page<BankAccount> result = bankAccountRepository.findByUserIdAndActive(user.get().getId(), true, pageable); 
            return result;
        } else {
        	throw new IllegalArgumentException();
        }
    }

    /**
     *  get one bankAccount by id.
     *  @return the entity
     */
    public BankAccount findOne(String id) {
        log.debug("Request to get BankAccount : {}", id);
        BankAccount bankAccount = bankAccountRepository.findOne(id);
        return bankAccount;
    }
    
    /**
     *  get one bankAccount by id.
     *  @return the entity
     */
    public Optional<BankAccount> findOneByNumber(String number) {
        log.debug("Request to get BankAccount by number: {}", number);
        return bankAccountRepository.findOneByNumber(number);
    }

    /**
     *  delete the  bankAccount by id.
     */
    public void delete(String id) {
        log.debug("Request to delete BankAccount : {}", id);
        bankAccountRepository.delete(id);
    }
    
    /**
     *  close the  bankAccount by id.
     */
    @Transactional
    public BankAccount close(String id, String targetAccountId) {
    	log.debug("Request to close BankAccount : {}", id);
    	BankAccount account = bankAccountRepository.findOne(id);
    	BankAccount targetAccount = bankAccountRepository.findOne(targetAccountId);
    	
    	// Subtracting money from closing account
    	Transaction transaction = new Transaction();
    	transaction.setAmount(account.getBalance());
    	transaction.setBankAccountId(id);
    	transaction.setDescription("Transfering balance to account " + targetAccount.getNumber());
    	transactionService.save(transaction);
    	// Adding money to target account
    	transaction = new Transaction();
    	transaction.setAmount(account.getBalance());
    	transaction.setBankAccountId(targetAccountId);
    	transaction.setDescription("Transfer from account " + account.getNumber());
    	transactionService.save(transaction);
    	
    	targetAccount.setBalance(targetAccount.getBalance().add(account.getBalance()));
    	account.setActive(false);
        bankAccountRepository.save(account);
        bankAccountRepository.save(targetAccount);
        
        return account;
    }
}
