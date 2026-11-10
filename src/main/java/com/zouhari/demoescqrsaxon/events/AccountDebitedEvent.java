package com.zouhari.demoescqrsaxon.events;

public record AccountDebitedEvent(String accountId, double amount, String currency) {
}
