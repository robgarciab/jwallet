package com.katari.jwallet.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.katari.jwallet.domain.BankAccount;
import com.katari.jwallet.domain.User;
import com.katari.jwallet.service.BankAccountService;
import com.katari.jwallet.service.UserService;
import com.katari.jwallet.web.rest.util.EntityAction;
import com.katari.jwallet.web.rest.util.HeaderUtil;
import com.katari.jwallet.web.rest.util.PaginationUtil;

/**
 * REST controller for managing BankAccount.
 */
@RestController
@RequestMapping("/api")
public class BankAccountResource {

    private final Logger log = LoggerFactory.getLogger(BankAccountResource.class);
        
    @Inject
    private BankAccountService bankAccountService;
    
    @Inject
    private UserService userService;
    
    /**
     * POST  /bankAccounts -> Create a new bankAccount.
     */
    @RequestMapping(value = "/bankAccounts",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BankAccount> createBankAccount(Principal principal, @RequestBody BankAccount bankAccount) throws URISyntaxException {
        log.debug("REST request to save BankAccount : {}", bankAccount);
        
        if (bankAccount.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bankAccount", "idexists", "A new bankAccount cannot already have an ID")).body(null);
        }
        
        if (principal == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bankAccount", "unexpected", "Contact your administrator")).body(null);
        }
        
        Optional<User> optionalUser = userService.getUserByLogin(principal.getName());
        if (!optionalUser.isPresent()) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("bankAccount", "unexpected", "Contact your administrator")).body(null);
        } else {
        	bankAccount.setUserId(optionalUser.get().getId());
        }
        
        BankAccount result;
        
        try {
            result = bankAccountService.save(bankAccount);
        } catch (DuplicateKeyException ex) {
        	return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(bankAccount.getNumber(), "bankAccount.numberexists", "A bankAccount with the same number alreasy exists")).body(null);
        }
        
        return ResponseEntity.created(new URI("/api/bankAccounts/" + result.getId()))
            .headers(HeaderUtil.createEntityActionAlert("bankAccount", EntityAction.CREATED, result.getNumber()))
            .body(result);
    }

    /**
     * PUT  /bankAccounts -> Updates an existing bankAccount.
     */
    @RequestMapping(value = "/bankAccounts",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BankAccount> updateBankAccount(Principal principal, @RequestBody BankAccount bankAccount) throws URISyntaxException {
        log.debug("REST request to update BankAccount : {}", bankAccount);

        if (bankAccount.getId() == null) {
            return createBankAccount(principal, bankAccount);
        }
        
        BankAccount result = bankAccountService.save(bankAccount);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityActionAlert("bankAccount", EntityAction.UPDATED, bankAccount.getNumber()))
            .body(result);
    }

    /**
     * GET  /bankAccounts -> get all the bankAccounts.
     */
    @RequestMapping(value = "/bankAccounts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BankAccount>> getAllBankAccounts(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of BankAccounts");
        Page<BankAccount> page = bankAccountService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bankAccounts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bankAccounts -> get user bankAccounts.
     */
    @RequestMapping(value = "/{login}/bankAccounts",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<BankAccount>> getBankAccountsForUser(@PathVariable String login, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get BankAccounts for a user");
        Page<BankAccount> page = bankAccountService.findByLogin(login, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bankAccounts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /bankAccounts/:id -> get the "id" bankAccount.
     */
    @RequestMapping(value = "/bankAccounts/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<BankAccount> getBankAccount(@PathVariable String id) {
        log.debug("REST request to get BankAccount : {}", id);
        BankAccount bankAccount = bankAccountService.findOne(id);
        return Optional.ofNullable(bankAccount)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bankAccounts/:id -> delete the "id" bankAccount.
     */
    @RequestMapping(value = "/bankAccounts/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBankAccount(@PathVariable String id) {
        log.debug("REST request to delete BankAccount : {}", id);
        bankAccountService.delete(id);
        return ResponseEntity. ok().headers(HeaderUtil.createEntityActionAlert("bankAccount", EntityAction.DELETED, id.toString())).build();
    }
    
    /**
     * CLOSE  /bankAccounts/:id -> close the "id" bankAccount and transfer the balance to "target" account.
     */
    @RequestMapping(value = "/bankAccounts/{id}/close",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> closeBankAccount(@PathVariable String id, @RequestBody String targetAccountId) {
        log.debug("REST request to delete BankAccount : {}", id);
        BankAccount closedAccount = bankAccountService.close(id, targetAccountId);
        return ResponseEntity. ok().headers(HeaderUtil.createEntityActionAlert("bankAccount", EntityAction.CLOSED, closedAccount.getNumber())).build();
    }
}
