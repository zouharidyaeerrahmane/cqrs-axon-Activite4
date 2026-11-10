package com.zouhari.demoescqrsaxon.command.aggregates;

import com.zouhari.demoescqrsaxon.command.commands.AddAccountCommand;
import com.zouhari.demoescqrsaxon.enums.AccountStatus;
import com.zouhari.demoescqrsaxon.events.AccountCreatedEvent;
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
    }

    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        log.info("AccountCreatedEvent occurred: {}", event.accountId());
        this.accountId = event.accountId();
        this.balance = event.initialBalance();
        this.status = event.status();
    }
}
