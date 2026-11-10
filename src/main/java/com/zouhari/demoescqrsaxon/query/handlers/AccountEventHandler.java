package com.zouhari.demoescqrsaxon.query.handlers;

import com.zouhari.demoescqrsaxon.enums.OperationType;
import com.zouhari.demoescqrsaxon.events.AccountActivatedEvent;
import com.zouhari.demoescqrsaxon.events.AccountCreatedEvent;
import com.zouhari.demoescqrsaxon.events.AccountCreditedEvent;
import com.zouhari.demoescqrsaxon.events.AccountDebitedEvent;
import com.zouhari.demoescqrsaxon.events.AccountStatusUpdatedEvent;
import com.zouhari.demoescqrsaxon.query.entities.Account;
import com.zouhari.demoescqrsaxon.query.entities.AccountOperation;
import com.zouhari.demoescqrsaxon.query.repository.AccountRepository;
import com.zouhari.demoescqrsaxon.query.repository.OperationRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.ResetHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ProcessingGroup("accounts")
@Transactional
@Slf4j
public class AccountEventHandler {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public AccountEventHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @EventHandler
    public void on(AccountCreatedEvent event, EventMessage<?> message) {
        log.info("Query side: AccountCreatedEvent received: {}", event.accountId());
        Account account = Account.builder()
                .id(event.accountId())
                .balance(event.initialBalance())
                .status(event.status())
                .currency(event.currency())
                .createdAt(message.getTimestamp())
                .build();
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountActivatedEvent event) {
        log.info("Query side: AccountActivatedEvent received: {}", event.accountId());
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setStatus(event.status());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountStatusUpdatedEvent event) {
        log.info("Query side: AccountStatusUpdatedEvent received: {}", event.accountId());
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        account.setStatus(event.status());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event, EventMessage<?> message) {
        log.info("Query side: AccountDebitedEvent received: {}", event.accountId());
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        AccountOperation operation = AccountOperation.builder()
                .date(message.getTimestamp())
                .amount(event.amount())
                .type(OperationType.DEBIT)
                .currency(event.currency())
                .account(account)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() - event.amount());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event, EventMessage<?> message) {
        log.info("Query side: AccountCreditedEvent received: {}", event.accountId());
        Account account = accountRepository.findById(event.accountId()).orElseThrow();
        AccountOperation operation = AccountOperation.builder()
                .date(message.getTimestamp())
                .amount(event.amount())
                .type(OperationType.CREDIT)
                .currency(event.currency())
                .account(account)
                .build();
        operationRepository.save(operation);
        account.setBalance(account.getBalance() + event.amount());
        accountRepository.save(account);
    }

    @ResetHandler
    public void resetReadModel() {
        // Reset only derived data. Never delete domain_event_entry or the Axon Server event store.
        operationRepository.deleteAllInBatch();
        accountRepository.deleteAllInBatch();
    }
}
