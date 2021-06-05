/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.rank.casino.dagacube.dto.request.TransactionHistoryRequestDto;
import uk.co.rank.casino.dagacube.dto.request.WagerWinRequestDTO;
import uk.co.rank.casino.dagacube.dto.response.PlayerBalanceResponseDto;
import uk.co.rank.casino.dagacube.dto.response.TransactionHistoryResponseDto;
import uk.co.rank.casino.dagacube.service.PlayerService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1")
public class PlayerController {
  private final PlayerService playerService;

  public PlayerController(PlayerService playerService) {
    this.playerService = playerService;
  }

  /**
   * Get a player's current balance
   *
   * @param playerId - The playerId for which to retrieve the balance
   * @return PlayerBalanceResponseDto - DTO containing the current player balance
   */
  @GetMapping(value = "/player/{playerId}/balance")
  public PlayerBalanceResponseDto getPlayerBalance(@PathVariable("playerId") long playerId) {
    BigDecimal playerBalance = playerService.getPlayerBalance(playerId);
    return new PlayerBalanceResponseDto(playerBalance);
  }

  /**
   * Deduct from a player's balance for placing a wager
   *
   * @param playerId - The playerId for which to deduct the value from
   * @return PlayerBalanceResponseDto - The player's new balance after the transaction
   */
  @PostMapping(value = "/player/{playerId}/wager")
  public ResponseEntity<PlayerBalanceResponseDto> addWager(@PathVariable("playerId") long playerId, WagerWinRequestDTO requestDTO) {
    BigDecimal playerBalance = playerService.addWager(playerId, requestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(new PlayerBalanceResponseDto(playerBalance));
  }

  /**
   * Add to a player's balance for winning
   *
   * @param playerId - The playerId for which to add the value to
   * @return PlayerBalanceResponseDto - The player's new balance after the transaction
   */
  @PostMapping(value = "/player/{playerId}/win")
  public ResponseEntity<PlayerBalanceResponseDto> addWin(@PathVariable("playerId") long playerId, WagerWinRequestDTO requestDTO) {
    BigDecimal playerBalance = playerService.addWin(playerId, requestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(new PlayerBalanceResponseDto(playerBalance));
  }

  /**
   * Get the lats 10 transactions for a player
   *
   * @param requestDto - DTO containing the player's username and the secret to access the data
   * @return - TransactionHistoryResponseDto - DTO containing the last requested transactions
   */
  @PostMapping(value = "/transaction/history")
  public ResponseEntity<TransactionHistoryResponseDto> transactionHistory(TransactionHistoryRequestDto requestDto) {
    //TODO: Implement
    return ResponseEntity.status(HttpStatus.OK).body(new TransactionHistoryResponseDto());
  }
}
