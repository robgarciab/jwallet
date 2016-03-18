package com.katari.jwallet.service;

import static org.assertj.core.api.StrictAssertions.assertThat;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.katari.jwallet.Application;
import com.katari.jwallet.domain.BankAccount;
import com.katari.jwallet.domain.Transaction;

/**
 * Test class for the UserResource REST controller.
 *
 * @see BankAccountService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class BankAccountServiceIntTest {

	@Inject
    private TransactionService transactionService;

    @Inject
    private BankAccountService bankAccountService;

    @Test
    public void assertThatAccountCreationIncludesFirstTransaction() {
        BankAccount bankAccount = getTestBankAccount(null);
        
        bankAccountService.save(bankAccount);

        Page<Transaction> transactions = transactionService.findByBankAccountNumber("192-20945875-0-55", new PageRequest(0, 1));
        
        assertThat(transactions.getSize()).isEqualTo(1);
    }
    
    @Test(expected=DuplicateKeyException.class)
    public void assertThatAccountNumberisUnique() {
        BankAccount bankAccount = getTestBankAccount("192-20945875-0-88");
        bankAccountService.save(bankAccount);
    }
    
    @Test(expected=ConstraintViolationException.class)
    public void assertThatAccountNumberMatchesPattern() {
        BankAccount bankAccount = getTestBankAccount("192-20945875-0-EE");
        bankAccountService.save(bankAccount);
    }
    
    private BankAccount getTestBankAccount(String number) {
    	BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(new BigDecimal(5500));
        if (number != null) {
        	bankAccount.setNumber(number);
        } else {
        	bankAccount.setNumber("192-20945875-0-55");
        }
        bankAccount.setUserId("user-0");
        
        return bankAccount;
    }
}
