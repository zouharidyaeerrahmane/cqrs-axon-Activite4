package com.zouhari.demoescqrsaxon.events;

public record AccountCreditedEvent(String accountId, double amount, String currency) {
}
