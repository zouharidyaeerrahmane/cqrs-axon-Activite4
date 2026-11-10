package com.zouhari.demoescqrsaxon.command.aggregates;

import com.zouhari.demoescqrsaxon.command.commands.AddAccountCommand;
import com.zouhari.demoescqrsaxon.command.commands.CreditAccountCommand;
import com.zouhari.demoescqrsaxon.command.commands.DebitAccountCommand;
import com.zouhari.demoescqrsaxon.command.commands.UpdateAccountStatusCommand;
import com.zouhari.demoescqrsaxon.enums.AccountStatus;
import com.zouhari.demoescqrsaxon.events.AccountCreatedEvent;
import com.zouhari.demoescqrsaxon.events.AccountActivatedEvent;
import com.zouhari.demoescqrsaxon.events.AccountCreditedEvent;
import com.zouhari.demoescqrsaxon.events.AccountDebitedEvent;
import com.zouhari.demoescqrsaxon.events.AccountStatusUpdatedEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
@Getter
@Slf4j
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private AccountStatus status;

    public AccountAggregate() {
        // Required by Axon when rebuilding the aggregate from its events.
    }

    @CommandHandler
    public AccountAggregate(AddAccountCommand command) {
        log.info("AddAccountCommand received: {}", command.getId());
        if (command.getInitialBalance() <= 0 || !Double.isFinite(command.getInitialBalance())) {
            throw new IllegalArgumentException("Initial balance must be positive");
        }
        AggregateLifecycle.apply(new AccountCreatedEvent(command.getId(),
                command.getInitialBalance(), AccountStatus.CREATED, command.getCurrency()));
        AggregateLifecycle.apply(new AccountActivatedEvent(command.getId(), AccountStatus.ACTIVATED));
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("AccountCreatedEvent occurred: {}", event.accountId());
        this.accountId = event.accountId();
        this.balance = event.initialBalance();
        this.status = event.status();
    }

    @CommandHandler
    public void handle(CreditAccountCommand command) {
        log.info("CreditAccountCommand received: {}", command.getId());
        if (status != AccountStatus.ACTIVATED) {
            throw new IllegalStateException("Account " + accountId + " is not activated");
        }
        if (command.getAmount() <= 0 || !Double.isFinite(command.getAmount())) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        AggregateLifecycle.apply(new AccountCreditedEvent(command.getId(), command.getAmount(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountActivatedEvent event) {
        log.info("AccountActivatedEvent occurred: {}", event.accountId());
        this.status = event.status();
    }

    @EventSourcingHandler
    public void on(AccountCreditedEvent event) {
        log.info("AccountCreditedEvent occurred: {}", event.accountId());
        this.balance += event.amount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand command) {
        log.info("DebitAccountCommand received: {}", command.getId());
        if (status != AccountStatus.ACTIVATED) {
            throw new IllegalStateException("Account " + accountId + " is not activated");
        }
        if (command.getAmount() <= 0 || !Double.isFinite(command.getAmount())) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance < command.getAmount()) {
            throw new IllegalStateException("Balance not sufficient");
        }
        AggregateLifecycle.apply(new AccountDebitedEvent(command.getId(), command.getAmount(), command.getCurrency()));
    }

    @EventSourcingHandler
    public void on(AccountDebitedEvent event) {
        log.info("AccountDebitedEvent occurred: {}", event.accountId());
        this.balance -= event.amount();
    }

    @CommandHandler
    public void handle(UpdateAccountStatusCommand command) {
        log.info("UpdateAccountStatusCommand received: {}", command.getId());
        if (command.getStatus() == null) {
            throw new IllegalArgumentException("Status is required");
        }
        if (status == command.getStatus()) {
            throw new IllegalStateException("This account is already in " + status);
        }
        AggregateLifecycle.apply(new AccountStatusUpdatedEvent(command.getId(), command.getStatus()));
    }

    @EventSourcingHandler
    public void on(AccountStatusUpdatedEvent event) {
        log.info("AccountStatusUpdatedEvent occurred: {}", event.accountId());
        this.status = event.status();
    }
}
