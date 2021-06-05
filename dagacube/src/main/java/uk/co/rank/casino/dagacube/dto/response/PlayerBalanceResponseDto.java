/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.dto.response;

import java.math.BigDecimal;

public class PlayerBalanceResponseDto {
  private BigDecimal balance;

  public PlayerBalanceResponseDto(BigDecimal balance) {
    this.balance = balance;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
