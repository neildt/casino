/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.service;

import org.springframework.stereotype.Service;
import uk.co.rank.casino.dagacube.controller.Exceptions.InsufficientFundsException;
import uk.co.rank.casino.dagacube.controller.Exceptions.PlayerNotFoundException;
import uk.co.rank.casino.dagacube.domain.dao.AccountRepository;
import uk.co.rank.casino.dagacube.domain.dao.PlayerRepository;
import uk.co.rank.casino.dagacube.domain.dao.TransactionRepository;
import uk.co.rank.casino.dagacube.domain.enums.TransactionType;
import uk.co.rank.casino.dagacube.domain.model.Player;
import uk.co.rank.casino.dagacube.domain.model.Transaction;
import uk.co.rank.casino.dagacube.dto.request.WagerWinRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

  /**
   * Get a player's current balance
   *
   * @param playerId - The playerId for which to retrieve the balance
   * @return BigDecimal - The current player balance
   */
  public BigDecimal getPlayerBalance(long playerId) {
    BigDecimal playerBalance = null;
    Player player = getPlayer(playerId);
    if (player != null) {
      playerBalance = player.getAccount().getBalance();
    } else {
      throw new PlayerNotFoundException();
    }
    return playerBalance;
  }

  public BigDecimal addWager(long playerId, WagerWinRequestDTO requestDTO) {

    Player player = getPlayer(playerId);
    if (player == null) {
      throw new PlayerNotFoundException();
    }

    BigDecimal playerBalance = player.getAccount().getBalance();
    if (!transactionExists(requestDTO.getTransactionId())) {
      if (playerBalance.compareTo(requestDTO.getAmount()) < 0) {
        throw new InsufficientFundsException();
      }

      playerBalance = processTransaction(player, requestDTO, TransactionType.WAGER);
    }
    return playerBalance;
  }

  public BigDecimal addWin(long playerId, WagerWinRequestDTO requestDTO) {
    Player player = getPlayer(playerId);
    if (player == null) {
      throw new PlayerNotFoundException();
    }

    BigDecimal playerBalance = player.getAccount().getBalance();
    if (!transactionExists(requestDTO.getTransactionId())) {
      playerBalance = processTransaction(player, requestDTO, TransactionType.WIN);
    }
    return playerBalance;
  }

  /**
   * Check if the provided transaction exists in the databaes
   *
   * @param transactionId - The transaction ID to check
   * @return boolean - if the transaction already exists
   */
  private boolean transactionExists(String transactionId) {
    Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
    return optionalTransaction.isPresent();
  }

  /**
   * Process the provided transaction by adding it against a player and updating the player's balance
   *
   * @param player          - The player Id to update
   * @param requestDTO      - The WagerWinRequestDTO with the transaction details
   * @param transactionType - The type of transaction to add
   * @return BigDecimal - The player's balance after the transaction
   */
  private BigDecimal processTransaction(Player player, WagerWinRequestDTO requestDTO, TransactionType transactionType) {

    createTransaction(requestDTO, transactionType, player);

    BigDecimal balance = player.getAccount().getBalance();
    switch (transactionType) {
      case WIN:
        balance = balance.add(requestDTO.getAmount());
        break;
      case WAGER:
        balance = balance.subtract(requestDTO.getAmount());
        break;
    }
    player.getAccount().setBalance(balance);
    playerRepository.save(player);

    return balance;
  }

  /**
   * Create a transaction for the provided player
   *
   * @param requestDTO      - The WagerWinRequestDTO with the transaction details
   * @param transactionType - The type of transaction to add
   * @param player          - The player to which to add the transaction
   */
  private void createTransaction(WagerWinRequestDTO requestDTO, TransactionType transactionType, Player player) {
    Transaction transaction = new Transaction();
    transaction.setId(requestDTO.getTransactionId());
    transaction.setAccount(player.getAccount());
    transaction.setAmount(requestDTO.getAmount());
    transaction.setTime(LocalDateTime.now());
    transaction.setType(transactionType);
    transactionRepository.save(transaction);
  }

  /**
   * Get the player by ID
   *
   * @param playerId - The player ID to get
   * @return Player - populated if present, null otherwise
   */
  private Player getPlayer(long playerId) {
    Player player = null;
    BigDecimal playerBalance = BigDecimal.ZERO;
    Optional<Player> playerOptional = playerRepository.findById(playerId);
    if (playerOptional.isPresent()) {
      player = playerOptional.get();
    }
    return player;
  }
}
