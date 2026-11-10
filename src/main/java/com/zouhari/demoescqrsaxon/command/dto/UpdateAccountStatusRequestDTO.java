package com.zouhari.demoescqrsaxon.command.dto;

import com.zouhari.demoescqrsaxon.enums.AccountStatus;

public record UpdateAccountStatusRequestDTO(String accountId, AccountStatus status) {
}
