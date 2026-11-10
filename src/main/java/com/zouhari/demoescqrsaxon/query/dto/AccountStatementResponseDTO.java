package com.zouhari.demoescqrsaxon.query.dto;

import com.zouhari.demoescqrsaxon.query.entities.Account;
import com.zouhari.demoescqrsaxon.query.entities.AccountOperation;

import java.util.List;

public record AccountStatementResponseDTO(Account account, List<AccountOperation> operations) {
}
