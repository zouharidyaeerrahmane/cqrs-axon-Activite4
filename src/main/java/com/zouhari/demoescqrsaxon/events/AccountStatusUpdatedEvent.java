package com.zouhari.demoescqrsaxon.events;

import com.zouhari.demoescqrsaxon.enums.AccountStatus;

public record AccountStatusUpdatedEvent(String accountId, AccountStatus status) {
}
