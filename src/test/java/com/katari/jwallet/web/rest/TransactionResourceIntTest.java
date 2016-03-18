package com.katari.jwallet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.katari.jwallet.Application;
import com.katari.jwallet.domain.BankAccount;
import com.katari.jwallet.domain.Transaction;
import com.katari.jwallet.repository.TransactionRepository;
import com.katari.jwallet.service.BankAccountService;
import com.katari.jwallet.service.TransactionService;


/**
 * Test class for the TransactionResource REST controller.
 *
 * @see TransactionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TransactionResourceIntTest {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);

    @Inject
    private BankAccountService bankAccountService;
    
    @Inject
    private TransactionRepository transactionRepository;

    @Inject
    private TransactionService transactionService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTransactionMockMvc;

    private Transaction transaction;
    
    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TransactionResource transactionResource = new TransactionResource();
        ReflectionTestUtils.setField(transactionResource, "transactionService", transactionService);
        this.restTransactionMockMvc = MockMvcBuilders.standaloneSetup(transactionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        transactionRepository.deleteAll();
        transaction = new Transaction();
        transaction.setAmount(DEFAULT_AMOUNT);
    }

    @Test
    public void createTransaction() throws Exception {
        // Create bank account if not exists
        prepareTransaction(transaction);
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        
        // Create the Transaction
        restTransactionMockMvc.perform(post("/api/transactions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(transaction)))
                .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    public void getAllTransactions() throws Exception {
        // Initialize the database
    	prepareTransaction(transaction);
    	transactionService.save(transaction);

        // Get all the transactions
        restTransactionMockMvc.perform(get("/api/transactions?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId())))
                .andExpect(jsonPath("$.[*].amount").value(hasItem(DEFAULT_AMOUNT.intValue())));
    }

    @Test
    public void getTransaction() throws Exception {
        // Initialize the database
    	prepareTransaction(transaction);
    	transactionService.save(transaction);

        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(transaction.getId()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()));
    }

    @Test
    public void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get("/api/transactions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }
    
    private synchronized void prepareTransaction(Transaction transaction) {
    	Optional<BankAccount> result = bankAccountService.findOneByNumber(TestUtil.BANK_ACCOUNT_NUMBER);
    	BankAccount bankAccount = null;
    	if (result.isPresent()) {
    		bankAccount = result.get();
    	} else {
    		bankAccount = new BankAccount();
    		bankAccount.setNumber(TestUtil.BANK_ACCOUNT_NUMBER);
            bankAccount.setBalance(DEFAULT_BALANCE);
    		bankAccount = bankAccountService.save(bankAccount);
    	}
    	transaction.setBankAccountId(bankAccount.getId());
    }
}
