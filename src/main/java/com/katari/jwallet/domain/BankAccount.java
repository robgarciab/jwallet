package com.katari.jwallet.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A BankAccount.
 */

@Document(collection = "bank_account")
public class BankAccount implements Serializable {

    @Id
    private String id;

    @Field("number")
    @Pattern(regexp="\\d{3}-\\d{8}-\\d-\\d{2}")
    @Indexed(unique = true)
    private String number;
    
    @Field("balance")
    @DecimalMin("0.00")
    private BigDecimal balance;
    
    @Field("active")
    @NotNull
    private Boolean active = true;
    
    @Field("jhi_user_id")
    @Indexed
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BankAccount bankAccount = (BankAccount) o;
        return Objects.equals(id, bankAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
            "id=" + id +
            ", balance='" + balance + "'" +
            '}';
    }
}
