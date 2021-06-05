package uk.co.rank.casino.dagacube.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

  private String transactionId;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private String transactionType;
  private LocalDateTime time;
  private boolean freeWager;

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

  public BigDecimal getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter(BigDecimal balanceAfter) {
    this.balanceAfter = balanceAfter;
  }

  public String getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(String transactionType) {
    this.transactionType = transactionType;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public boolean isFreeWager() {
    return freeWager;
  }

  public void setFreeWager(boolean freeWager) {
    this.freeWager = freeWager;
  }

  @Override
  public String toString() {
    return "TransactionResponseDTO{" +
            "transactionId='" + transactionId + '\'' +
            ", amount=" + amount +
            ", balanceAfter=" + balanceAfter +
            ", transactionType='" + transactionType + '\'' +
            ", time=" + time +
            ", freeWager=" + freeWager +
            '}';
  }
}
