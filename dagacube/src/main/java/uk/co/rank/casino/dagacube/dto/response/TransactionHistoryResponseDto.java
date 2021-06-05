/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.dto.response;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryResponseDto {
  private List<TransactionResponseDTO> transactions = new ArrayList<>();

  public List<TransactionResponseDTO> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<TransactionResponseDTO> transactions) {
    this.transactions = transactions;
  }

  public void addTransaction(TransactionResponseDTO transaction) {
    transactions.add(transaction);
  }
}
