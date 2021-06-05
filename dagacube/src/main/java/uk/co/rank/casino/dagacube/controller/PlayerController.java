/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.rank.casino.dagacube.dto.request.TransactionHistoryRequestDto;
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
   * @return BigDecimal - The current player balance
   */
  @GetMapping(value = "/player/{playerId}/balance")
  public PlayerBalanceResponseDto getPlayerBalance(@PathVariable("playerId") long playerId) {
    BigDecimal playerBalance = playerService.getPlayerBalance(playerId);
    return new PlayerBalanceResponseDto(playerBalance);
  }

  @PostMapping(value = "/player/{playerId}/wager")
  public ResponseEntity<Object> playerWager(@PathVariable("playerId") String playerId) {
    //TODO: Implement
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping(value = "/player/{playerId}/win")
  public ResponseEntity<Object> playerWin(@PathVariable("playerId") String playerId) {
    //TODO: Implement
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping(value = "/transaction/history")
  public ResponseEntity<TransactionHistoryResponseDto> transactionHistory(TransactionHistoryRequestDto requestDto) {
    //TODO: Implement
    return ResponseEntity.status(HttpStatus.OK).body(new TransactionHistoryResponseDto());
  }
}
