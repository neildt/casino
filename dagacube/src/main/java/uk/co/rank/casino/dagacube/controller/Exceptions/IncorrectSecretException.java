/*
 * Copyright RANK 2021
 */
package uk.co.rank.casino.dagacube.controller.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Incorrect secret provided")
public class IncorrectSecretException extends RuntimeException {
}