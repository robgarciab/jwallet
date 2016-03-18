package com.katari.jwallet.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.katari.jwallet.domain.Transaction;
import com.katari.jwallet.service.TransactionService;
import com.katari.jwallet.web.rest.util.EntityAction;
import com.katari.jwallet.web.rest.util.HeaderUtil;
import com.katari.jwallet.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Transaction.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);
        
    @Inject
    private TransactionService transactionService;
    
    /**
     * POST  /transactions -> Create a new transaction.
     */
    @RequestMapping(value = "/transactions",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) throws URISyntaxException {
        log.debug("REST request to save Transaction : {}", transaction);
        if (transaction.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("transaction", "idexists", "A new transaction cannot already have an ID")).body(null);
        }
        Transaction result = transactionService.save(transaction);
        return ResponseEntity.created(new URI("/api/transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityActionAlert("transaction", EntityAction.CREATED, result.getId().toString()))
            .body(result);
    }

    /**
     * GET  /transactions -> get all the transactions.
     */
    @RequestMapping(value = "/transactions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Transaction>> getAllTransactions(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Transactions");
        Page<Transaction> page = transactionService.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transactions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /transactions -> get transactions by Account.
     */
    @RequestMapping(value = "/{number}/transactions",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Transaction>> getTransactionsForBankAccount(@PathVariable String number, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Transactions");
        Page<Transaction> page = transactionService.findByBankAccountNumber(number, pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transactions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /transactions/:id -> get the "id" transaction.
     */
    @RequestMapping(value = "/transactions/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Transaction> getTransaction(@PathVariable String id) {
        log.debug("REST request to get Transaction : {}", id);
        Transaction transaction = transactionService.findOne(id);
        return Optional.ofNullable(transaction)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
