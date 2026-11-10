package com.zouhari.demoescqrsaxon.query.dto;

import com.zouhari.demoescqrsaxon.query.entities.Account;
import com.zouhari.demoescqrsaxon.query.entities.AccountOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

// XStream 1.4.20 can restore immutable classes, but not Java records on Java 21.
@Getter
@AllArgsConstructor
public class AccountStatementResponseDTO {
    private final Account account;
    private final List<AccountOperation> operations;
}
