package com.katari.jwallet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.katari.jwallet.Application;
import com.katari.jwallet.domain.BankAccount;
import com.katari.jwallet.repository.BankAccountRepository;
import com.katari.jwallet.service.BankAccountService;
import com.katari.jwallet.service.UserService;


/**
 * Test class for the BankAccountResource REST controller.
 *
 * @see BankAccountResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class BankAccountResourceIntTest {


    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal(1);
    private static final BigDecimal UPDATED_BALANCE = new BigDecimal(2);

    @Inject
    private BankAccountRepository bankAccountRepository;

    @Inject
    private BankAccountService bankAccountService;

    @Inject
    private UserService userService;
    
    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBankAccountMockMvc;

    private BankAccount bankAccount;
    
    private Principal principal;

    @PostConstruct
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        BankAccountResource bankAccountResource = new BankAccountResource();
        ReflectionTestUtils.setField(bankAccountResource, "bankAccountService", bankAccountService);
        ReflectionTestUtils.setField(bankAccountResource, "userService", userService);
        this.restBankAccountMockMvc = MockMvcBuilders.standaloneSetup(bankAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        bankAccountRepository.deleteAll();
        bankAccount = new BankAccount();
        bankAccount.setNumber(TestUtil.BANK_ACCOUNT_NUMBER);
        bankAccount.setBalance(DEFAULT_BALANCE);
        principal = new Principal() {
            @Override
            public String getName() {
                return "user";
            }
        };
    }

    @Test
    public void createBankAccount() throws Exception {
        int databaseSizeBeforeCreate = bankAccountRepository.findAll().size();

        // Create the BankAccount

        restBankAccountMockMvc.perform(post("/api/bankAccounts")
        		.principal(principal)
        		.contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
        		.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeCreate + 1);
        BankAccount testBankAccount = bankAccounts.get(bankAccounts.size() - 1);
        assertThat(testBankAccount.getBalance()).isEqualTo(DEFAULT_BALANCE);
    }

    @Test
    public void getAllBankAccounts() throws Exception {
        // Initialize the database
        bankAccountRepository.save(bankAccount);

        // Get all the bankAccounts
        restBankAccountMockMvc.perform(get("/api/bankAccounts?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(bankAccount.getId())))
                .andExpect(jsonPath("$.[*].balance").value(hasItem(DEFAULT_BALANCE.intValue())));
    }

    @Test
    public void getBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.save(bankAccount);

        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bankAccounts/{id}", bankAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(bankAccount.getId()))
            .andExpect(jsonPath("$.balance").value(DEFAULT_BALANCE.intValue()));
    }

    @Test
    public void getNonExistingBankAccount() throws Exception {
        // Get the bankAccount
        restBankAccountMockMvc.perform(get("/api/bankAccounts/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.save(bankAccount);

		int databaseSizeBeforeUpdate = bankAccountRepository.findAll().size();

        // Update the bankAccount
        bankAccount.setBalance(UPDATED_BALANCE);

        restBankAccountMockMvc.perform(put("/api/bankAccounts")
        		.principal(principal)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(bankAccount)))
        		.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        // Validate the BankAccount in the database
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeUpdate);
        BankAccount testBankAccount = bankAccounts.get(bankAccounts.size() - 1);
        assertThat(testBankAccount.getBalance()).isEqualTo(UPDATED_BALANCE);
    }

    @Test
    public void deleteBankAccount() throws Exception {
        // Initialize the database
        bankAccountRepository.save(bankAccount);

		int databaseSizeBeforeDelete = bankAccountRepository.findAll().size();

        // Get the bankAccount
        restBankAccountMockMvc.perform(delete("/api/bankAccounts/{id}", bankAccount.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        assertThat(bankAccounts).hasSize(databaseSizeBeforeDelete - 1);
    }
}
