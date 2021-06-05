/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.dto.request;

import java.math.BigDecimal;

public class WagerWinRequestDTO {

  private String transactionId;
  private BigDecimal amount;
  private String promoCode;

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getPromoCode() {
    return promoCode;
  }

  public void setPromoCode(String promoCode) {
    this.promoCode = promoCode;
  }
}
