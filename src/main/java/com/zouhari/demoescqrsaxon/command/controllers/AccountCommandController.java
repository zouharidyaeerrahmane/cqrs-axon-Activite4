package com.zouhari.demoescqrsaxon.command.controllers;

import com.zouhari.demoescqrsaxon.command.commands.AddAccountCommand;
import com.zouhari.demoescqrsaxon.command.commands.CreditAccountCommand;
import com.zouhari.demoescqrsaxon.command.commands.DebitAccountCommand;
import com.zouhari.demoescqrsaxon.command.commands.UpdateAccountStatusCommand;
import com.zouhari.demoescqrsaxon.command.dto.AddAccountRequestDTO;
import com.zouhari.demoescqrsaxon.command.dto.CreditAccountRequestDTO;
import com.zouhari.demoescqrsaxon.command.dto.DebitAccountRequestDTO;
import com.zouhari.demoescqrsaxon.command.dto.UpdateAccountStatusRequestDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping("/commands")
public class AccountCommandController {
    private final CommandGateway commandGateway;
    private final EventStore eventStore;

    public AccountCommandController(CommandGateway commandGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
    }

    @PostMapping("/account/add")
    public CompletableFuture<String> addNewAccount(@RequestBody AddAccountRequestDTO request) {
        return commandGateway.send(new AddAccountCommand(
                UUID.randomUUID().toString(), request.initialBalance(), request.currency()));
    }

    @GetMapping("/events/{accountId}")
    public Stream<? extends DomainEventMessage<?>> events(@PathVariable String accountId) {
        return eventStore.readEvents(accountId).asStream();
    }

    @PostMapping("/account/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDTO request) {
        return commandGateway.send(new CreditAccountCommand(
                request.accountId(), request.amount(), request.currency()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }

    @PostMapping("/account/debit")
    public CompletableFuture<String> debitAccount(@RequestBody DebitAccountRequestDTO request) {
        return commandGateway.send(new DebitAccountCommand(
                request.accountId(), request.amount(), request.currency()));
    }

    @PutMapping("/account/updateStatus")
    public CompletableFuture<String> updateStatus(@RequestBody UpdateAccountStatusRequestDTO request) {
        return commandGateway.send(new UpdateAccountStatusCommand(request.accountId(), request.status()));
    }
}
