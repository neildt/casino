/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.dto.request;

public class TransactionHistoryRequestDto {
  private String username;
  private String secret;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
