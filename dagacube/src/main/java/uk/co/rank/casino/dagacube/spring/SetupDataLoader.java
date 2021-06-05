/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.co.rank.casino.dagacube.domain.dao.PlayerRepository;
import uk.co.rank.casino.dagacube.domain.model.Account;
import uk.co.rank.casino.dagacube.domain.model.Player;

import java.math.BigDecimal;

/**
 * Class used for setting up initial data in the database
 */
@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private boolean alreadySetup = false;

    @Value("${setup.initial.data:false}")
    private boolean setupInitialData;

    private final PlayerRepository playerRepository;


    public SetupDataLoader(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (!setupInitialData || alreadySetup) {
            return;
        }

        logger.info("Setting up initial player data");

        createPlayerIfNotFound("NeilDuToit", new BigDecimal("100.00"));
        createPlayerIfNotFound("PlayerNumber2", new BigDecimal("500.00"));
        alreadySetup = true;
    }

    private void createPlayerIfNotFound(final String userName, final BigDecimal accountBalance) {
        Player player = new Player(userName);
        Account account = new Account(accountBalance);
        player.setAccount(account);
        playerRepository.save(player);
        logger.info("Created player: " + player.toString());
    }
}