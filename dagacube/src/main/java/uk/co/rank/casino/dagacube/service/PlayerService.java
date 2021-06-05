/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.service;

import org.springframework.stereotype.Service;
import uk.co.rank.casino.dagacube.domain.dao.AccountRepository;
import uk.co.rank.casino.dagacube.domain.dao.PlayerRepository;
import uk.co.rank.casino.dagacube.domain.dao.TransactionRepository;
import uk.co.rank.casino.dagacube.domain.model.Player;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  public PlayerService(PlayerRepository playerRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
    this.playerRepository = playerRepository;
    this.accountRepository = accountRepository;
    this.transactionRepository = transactionRepository;
  }

  public BigDecimal getPlayerBalance(long playerId) {
    BigDecimal playerBalance = BigDecimal.ZERO;
    Optional<Player> player = playerRepository.findById(playerId);
    if (player.isPresent())
    {
      playerBalance = player.get().getAccount().getBalance();
    }
    return playerBalance;
  }
}
