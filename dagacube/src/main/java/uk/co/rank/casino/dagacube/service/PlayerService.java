/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.co.rank.casino.dagacube.controller.Exceptions.AccountNotFoundException;
import uk.co.rank.casino.dagacube.controller.Exceptions.IncorrectSecretException;
import uk.co.rank.casino.dagacube.controller.Exceptions.InsufficientFundsException;
import uk.co.rank.casino.dagacube.controller.Exceptions.PlayerNotFoundException;
import uk.co.rank.casino.dagacube.domain.dao.AccountRepository;
import uk.co.rank.casino.dagacube.domain.dao.PlayerRepository;
import uk.co.rank.casino.dagacube.domain.dao.TransactionRepository;
import uk.co.rank.casino.dagacube.domain.enums.TransactionType;
import uk.co.rank.casino.dagacube.domain.model.Account;
import uk.co.rank.casino.dagacube.domain.model.Player;
import uk.co.rank.casino.dagacube.domain.model.Transaction;
import uk.co.rank.casino.dagacube.dto.request.TransactionHistoryRequestDto;
import uk.co.rank.casino.dagacube.dto.request.WagerWinRequestDto;
import uk.co.rank.casino.dagacube.dto.response.TransactionHistoryResponseDto;
import uk.co.rank.casino.dagacube.dto.response.TransactionResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

  private final PlayerRepository playerRepository;
  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;

  @Value("${transaction.history.secret:swordfish}")
  private String transactionHistorySecret;

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
    Player player = getPlayer(playerId);
    Account account = getAccount(player);
    return account.getBalance();
  }

  /**
   * Add a wager transaction and update player balance
   *
   * @param playerId   - The player ID to add the transaction to
   * @param requestDTO - The {@link WagerWinRequestDto} with the transaction details
   * @return BigDecimal - The current player balance
   */
  public BigDecimal addWager(long playerId, WagerWinRequestDto requestDTO) {
    Player player = getPlayer(playerId);
    BigDecimal playerBalance = player.getAccount().getBalance();

    if (!transactionExists(requestDTO.getTransactionId())) {
      if (StringUtils.isNotBlank(requestDTO.getPromoCode()) && "paper".equalsIgnoreCase(requestDTO.getPromoCode())) {
        player.setFreeWagers(5);
      } else if (player.getFreeWagers() == 0) {
        //Only check balance if the wager is not free
        if (playerBalance.compareTo(requestDTO.getAmount()) < 0) {
          throw new InsufficientFundsException();
        }
      }
      playerBalance = processTransaction(player, requestDTO, TransactionType.WAGER);
    }
    playerRepository.save(player);
    return playerBalance;
  }

  /**
   * Add a win transaction and update player balance
   *
   * @param playerId   - The player ID to add the transaction to
   * @param requestDTO - The {@link WagerWinRequestDto} with the transaction details
   * @return BigDecimal - The current player balance
   */
  public BigDecimal addWin(long playerId, WagerWinRequestDto requestDTO) {
    Player player = getPlayer(playerId);

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
   * @param requestDTO      - The {@link WagerWinRequestDto} with the transaction details
   * @param transactionType - The {@link TransactionType} to add
   * @return BigDecimal - The player's balance after the transaction
   */
  private BigDecimal processTransaction(Player player, WagerWinRequestDto requestDTO, TransactionType transactionType) {

    boolean isfreeWager = false;
    BigDecimal balance = player.getAccount().getBalance();
    switch (transactionType) {
      case WIN:
        balance = balance.add(requestDTO.getAmount());
        break;
      case WAGER:
        isfreeWager = checkFreeWager(player);
        if (!isfreeWager) {
          balance = balance.subtract(requestDTO.getAmount());
        }
        break;
    }
    player.getAccount().setBalance(balance);
    playerRepository.save(player);

    createTransaction(requestDTO, transactionType, player, isfreeWager);

    return balance;
  }

  /**
   * Check if the current wager is free for the player, and decrement if it is
   * @param player - The player to check
   * @return boolean - if the current wager is free
   */
  private boolean checkFreeWager(Player player) {
    boolean isfreeWager = false;
    if (player.getFreeWagers() > 0) {
      player.setFreeWagers(player.getFreeWagers() - 1);
      isfreeWager = true;
    }
    return isfreeWager;
  }

  /**
   * Create a transaction for the provided player
   *
   * @param requestDTO      - The {@link WagerWinRequestDto} with the transaction details
   * @param transactionType - The {@link TransactionType} to add
   * @param player          - The player to which to add the transaction
   */
  private void createTransaction(WagerWinRequestDto requestDTO, TransactionType transactionType, Player player, boolean isfreeWager) {
    Transaction transaction = new Transaction();
    transaction.setId(requestDTO.getTransactionId());
    transaction.setAccount(player.getAccount());
    transaction.setAmount(requestDTO.getAmount());
    transaction.setBalanceAfter(player.getAccount().getBalance());
    transaction.setTime(LocalDateTime.now());
    transaction.setType(transactionType);
    transaction.setFreeWager(isfreeWager);
    transactionRepository.save(transaction);
  }

  /**
   * Get the player by ID
   *
   * @param playerId - The player ID to get
   * @return Player - populated if present, null otherwise
   */
  private Player getPlayer(long playerId) {
    Optional<Player> playerOptional = playerRepository.findById(playerId);
    if (playerOptional.isEmpty()) {
      throw new PlayerNotFoundException();
    }
    return playerOptional.get();
  }

  private Account getAccount(Player player) {
    if (player.getAccount() != null) {
      return player.getAccount();
    } else {
      throw new AccountNotFoundException();
    }
  }


  //TODO: TECH DEBT: This should be in a TransactionService class
  /**
   * Get the transaction history for a player - Limited to 10 transactions
   * @param requestDto - The incoming request for history
   * @return TransactionHistoryResponseDto - The requested transactions
   */
  public TransactionHistoryResponseDto getTransactionHistory(TransactionHistoryRequestDto requestDto) {
    if (!requestDto.getSecret().equalsIgnoreCase(transactionHistorySecret)) {
      throw new IncorrectSecretException();
    }

    Optional<Player> playerOptional = playerRepository.findByUsername(requestDto.getUsername());
    if (playerOptional.isEmpty()) {
      throw new PlayerNotFoundException();
    }

    if (playerOptional.get().getAccount() == null) {
      throw new AccountNotFoundException();
    }

    long accountId = playerOptional.get().getAccount().getId();
    List<Transaction> transactions = transactionRepository.getLatestTransactions(accountId, 10);
    TransactionHistoryResponseDto transactionHistoryResponseDto = new TransactionHistoryResponseDto();

    for (Transaction transaction : transactions) {
      TransactionResponseDto transactionResponseDTO = new TransactionResponseDto();

      transactionResponseDTO.setAmount(transaction.getAmount());
      transactionResponseDTO.setBalanceAfter(transaction.getBalanceAfter());
      transactionResponseDTO.setTime(transaction.getTime());
      transactionResponseDTO.setTransactionId(transaction.getId());
      transactionResponseDTO.setTransactionType(transaction.getType().toString());
      transactionResponseDTO.setFreeWager(transaction.isFreeWager());

      transactionHistoryResponseDto.addTransaction(transactionResponseDTO);
    }

    return transactionHistoryResponseDto;
  }
}
