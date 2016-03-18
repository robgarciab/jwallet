package com.katari.jwallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A Transaction.
 */

@Document(collection = "transaction")
public class Transaction implements Serializable {

    @Id
    private String id;

    @Field("amount")
    private BigDecimal amount;

    @Field("date")
    private ZonedDateTime date = ZonedDateTime.now();
    
    @Field("description")
    private String description;
    
    @Field("bank_account_id")
    @Indexed
    private String bankAccountId;
    
    @Field("bank_account_number")
    @Indexed
    private String bankAccountNumber;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public ZonedDateTime getDate() {
		return date;
	}

	public void setDate(ZonedDateTime date) {
		this.date = date;
	}

	public String getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(String bankAccountId) {
		this.bankAccountId = bankAccountId;
	}
	
	public String getBankAccountNumber() {
		return bankAccountNumber;
	}

	public void setBankAccountNumber(String bankAccountNumber) {
		this.bankAccountNumber = bankAccountNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction transaction = (Transaction) o;
        return Objects.equals(id, transaction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + id +
            ", amount='" + amount + "'" +
            '}';
    }
}
