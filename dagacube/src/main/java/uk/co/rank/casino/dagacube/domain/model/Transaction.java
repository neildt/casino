/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uk.co.rank.casino.dagacube.domain.converter.LocalDateTimeConverter;
import uk.co.rank.casino.dagacube.domain.enums.TransactionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity that holds transaction data for an account
 */
@Entity
@Table(name = "transaction")
public class Transaction {
  @Id
  @Column(unique = true, nullable = false)
  private String id;

  @ManyToOne
  @JsonIgnoreProperties(value = "transactions", allowSetters = true)
  private Account account;

  @Enumerated(EnumType.STRING)
  private TransactionType type;

  private BigDecimal amount;

  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime time;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public TransactionType getType() {
    return type;
  }

  public void setType(TransactionType type) {
    this.type = type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  @Override
  public String toString() {
    return "Transaction{" +
            "id='" + id + '\'' +
            ", account=" + account +
            ", type='" + type + '\'' +
            ", amount=" + amount +
            ", time=" + time +
            '}';
  }
}
