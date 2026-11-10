package com.zouhari.demoescqrsaxon.query.controllers;

import com.zouhari.demoescqrsaxon.query.dto.AccountStatementResponseDTO;
import com.zouhari.demoescqrsaxon.query.entities.Account;
import com.zouhari.demoescqrsaxon.query.entities.AccountOperation;
import com.zouhari.demoescqrsaxon.query.queries.GetAccountStatementQuery;
import com.zouhari.demoescqrsaxon.query.queries.GetAllAccountsQuery;
import com.zouhari.demoescqrsaxon.query.queries.WatchEventQuery;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/query")
public class AccountQueryController {
    private final QueryGateway queryGateway;

    public AccountQueryController(QueryGateway queryGateway) {
        this.queryGateway = queryGateway;
    }

    @GetMapping("/account/all")
    public CompletableFuture<List<Account>> getAllAccounts() {
        return queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(Account.class));
    }

    @GetMapping("/account/statement/{accountId}")
    public CompletableFuture<AccountStatementResponseDTO> getAccountStatement(@PathVariable String accountId) {
        return queryGateway.query(new GetAccountStatementQuery(accountId),
                ResponseTypes.instanceOf(AccountStatementResponseDTO.class));
    }

    @GetMapping(value = "/account/watch/{accountId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<AccountOperation> watch(@PathVariable String accountId) {
        return Flux.defer(() -> {
            var result = queryGateway.subscriptionQuery(new WatchEventQuery(accountId),
                    ResponseTypes.instanceOf(AccountOperation.class),
                    ResponseTypes.instanceOf(AccountOperation.class));
            return result.initialResult().concatWith(result.updates()).doFinally(signal -> result.close());
        });
    }
}
