package uk.co.rank.casino.dagacube.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

  private String transactionId;
  private BigDecimal amount;
  private String transactionType;
  private LocalDateTime time;

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
}
