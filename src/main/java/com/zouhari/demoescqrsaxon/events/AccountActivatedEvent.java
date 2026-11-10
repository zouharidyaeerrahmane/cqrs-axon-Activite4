package com.zouhari.demoescqrsaxon.events;

import com.zouhari.demoescqrsaxon.enums.AccountStatus;

public record AccountActivatedEvent(String accountId, AccountStatus status) {
}
