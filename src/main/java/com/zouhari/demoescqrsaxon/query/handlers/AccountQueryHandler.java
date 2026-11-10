package com.zouhari.demoescqrsaxon.query.handlers;

import com.zouhari.demoescqrsaxon.query.dto.AccountStatementResponseDTO;
import com.zouhari.demoescqrsaxon.query.entities.Account;
import com.zouhari.demoescqrsaxon.query.entities.AccountOperation;
import com.zouhari.demoescqrsaxon.query.queries.GetAccountStatementQuery;
import com.zouhari.demoescqrsaxon.query.queries.GetAllAccountsQuery;
import com.zouhari.demoescqrsaxon.query.queries.WatchEventQuery;
import com.zouhari.demoescqrsaxon.query.repository.AccountRepository;
import com.zouhari.demoescqrsaxon.query.repository.OperationRepository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class AccountQueryHandler {
    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    public AccountQueryHandler(AccountRepository accountRepository, OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query) {
        return accountRepository.findAll();
    }

    @QueryHandler
    public AccountStatementResponseDTO on(GetAccountStatementQuery query) {
        Account account = accountRepository.findById(query.getAccountId()).orElseThrow();
        return new AccountStatementResponseDTO(account, operationRepository.findByAccountId(query.getAccountId()));
    }

    @QueryHandler
    public AccountOperation on(WatchEventQuery query) {
        // The tutorial starts the subscription with an empty operation, then pushes new ones.
        return AccountOperation.builder().build();
    }
}
