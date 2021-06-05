/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.domain.model;

import javax.persistence.*;

/**
 * Entity that houses all the player data
 */
@Entity
@Table(name = "player")
public class Player {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String username;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "account_id", referencedColumnName = "id")
  private Account account;

  public Player() {
  }

  public Player(String username) {
    this.username = username;
  }

  public long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  @Override
  public String toString() {
    return "Player{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", account=" + account +
            '}';
  }
}
