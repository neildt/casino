/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.dto.response;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryResponseDto {
  private List<TransactionResponseDto> transactions = new ArrayList<>();

  public List<TransactionResponseDto> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<TransactionResponseDto> transactions) {
    this.transactions = transactions;
  }

  public void addTransaction(TransactionResponseDto transaction) {
    transactions.add(transaction);
  }
}
