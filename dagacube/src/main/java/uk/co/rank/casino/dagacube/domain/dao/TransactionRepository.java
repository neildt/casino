/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.domain.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.rank.casino.dagacube.domain.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
