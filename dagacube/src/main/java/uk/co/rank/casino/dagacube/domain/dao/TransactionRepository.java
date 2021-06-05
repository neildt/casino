/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.co.rank.casino.dagacube.domain.model.Transaction;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
  @Query(value = "SELECT * FROM TRANSACTION t WHERE ACCOUNT_ID = :accountId ORDER BY TIME DESC LIMIT :limit",
          nativeQuery = true)
  List<Transaction> getLatestTransactions(long accountId, int limit);
}
