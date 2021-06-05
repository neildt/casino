/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.domain.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Entity that holds account data for a player
 */
@Entity
@Table(name = "account")
public class Account {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private BigDecimal balance = BigDecimal.ZERO;

  public Account() {
  }

  public Account(BigDecimal balance) {
    this.balance = balance;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  @Override
  public String toString() {
    return "Account{" +
            "id=" + id +
            ", balance=" + balance +
            '}';
  }
}
