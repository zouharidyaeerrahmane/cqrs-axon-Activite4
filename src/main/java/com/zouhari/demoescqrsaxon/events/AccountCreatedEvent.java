package com.zouhari.demoescqrsaxon.events;

import com.zouhari.demoescqrsaxon.enums.AccountStatus;

public record AccountCreatedEvent(String accountId, double initialBalance,
                                  AccountStatus status, String currency) {
}
