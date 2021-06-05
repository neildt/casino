/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.controller.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "Insufficient Funds")
public class InsufficientFundsException extends RuntimeException {
}