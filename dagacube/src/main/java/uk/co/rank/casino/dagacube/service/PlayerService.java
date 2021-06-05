/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.service;

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
import uk.co.rank.casino.dagacube.dto.request.WagerWinRequestDTO;
import uk.co.rank.casino.dagacube.dto.response.TransactionHistoryResponseDto;
import uk.co.rank.casino.dagacube.dto.response.TransactionResponseDTO;

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
   * @param requestDTO - The {@link WagerWinRequestDTO} with the transaction details
   * @return BigDecimal - The current player balance
   */
  public BigDecimal addWager(long playerId, WagerWinRequestDTO requestDTO) {

    Player player = getPlayer(playerId);

    BigDecimal playerBalance = player.getAccount().getBalance();
    if (!transactionExists(requestDTO.getTransactionId())) {
      if (playerBalance.compareTo(requestDTO.getAmount()) < 0) {
        throw new InsufficientFundsException();
      }

      playerBalance = processTransaction(player, requestDTO, TransactionType.WAGER);
    }
    return playerBalance;
  }

  /**
   * Add a win transaction and update player balance
   *
   * @param playerId   - The player ID to add the transaction to
   * @param requestDTO - The {@link WagerWinRequestDTO} with the transaction details
   * @return BigDecimal - The current player balance
   */
  public BigDecimal addWin(long playerId, WagerWinRequestDTO requestDTO) {
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
   * @param requestDTO      - The {@link WagerWinRequestDTO} with the transaction details
   * @param transactionType - The {@link TransactionType} to add
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
   * @param requestDTO      - The {@link WagerWinRequestDTO} with the transaction details
   * @param transactionType - The {@link TransactionType} to add
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
    Optional<Player> playerOptional = playerRepository.findById(playerId);
    if (playerOptional.isEmpty()) {
      throw new PlayerNotFoundException();
    }
    return playerOptional.get();
  }

  private Account getAccount(Player player) {
    Account account = null;
    if (player.getAccount() != null) {
      account = player.getAccount();
    } else {
      throw new AccountNotFoundException();
    }
    return account;
  }

  public TransactionHistoryResponseDto getTransactionHistory(TransactionHistoryRequestDto requestDto) {
    if (!requestDto.getSecret().equalsIgnoreCase(transactionHistorySecret))
    {
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
      TransactionResponseDTO transactionResponseDTO = new TransactionResponseDTO();

      transactionResponseDTO.setAmount(transaction.getAmount());
      transactionResponseDTO.setTime(transaction.getTime());
      transactionResponseDTO.setTransactionId(transaction.getId());
      transactionResponseDTO.setTransactionType(transaction.getType().toString());

      transactionHistoryResponseDto.addTransaction(transactionResponseDTO);
    }

    return transactionHistoryResponseDto;
  }
}
