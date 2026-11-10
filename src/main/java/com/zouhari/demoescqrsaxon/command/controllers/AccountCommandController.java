package com.zouhari.demoescqrsaxon.command.controllers;

import com.zouhari.demoescqrsaxon.command.commands.AddAccountCommand;
import com.zouhari.demoescqrsaxon.command.dto.AddAccountRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/commands")
public class AccountCommandController {
    private final CommandGateway commandGateway;

    public AccountCommandController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/account/add")
    public CompletableFuture<String> addNewAccount(@RequestBody AddAccountRequestDTO request) {
        return commandGateway.send(new AddAccountCommand(
                UUID.randomUUID().toString(), request.initialBalance(), request.currency()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
