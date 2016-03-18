package com.katari.jwallet.service;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.math.BigDecimal;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.katari.jwallet.Application;
import com.katari.jwallet.domain.BankAccount;
import com.katari.jwallet.domain.Transaction;

/**
 * Test class for the UserResource REST controller.
 *
 * @see TransactionService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class TransactionServiceIntTest {

	@Inject
    private BankAccountService bankAccountService;

    @Inject
    private TransactionService transactionService;

    @Test
    public void assertThatTransactionCreationUpdatesAccountBalance() {
    	BankAccount bankAccount = bankAccountService.findOne("bank-account-0");
    	BigDecimal previousBalance = bankAccount.getBalance();
        
    	Transaction transaction = new Transaction();
        transaction.setBankAccountId("bank-account-0");
        transaction.setAmount(new BigDecimal(550));
        transactionService.save(transaction);
        
        bankAccount = bankAccountService.findOne("bank-account-0");
        assertThat(previousBalance.compareTo(bankAccount.getBalance()) < 1).isTrue();
    }
}
