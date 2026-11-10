package com.zouhari.demoescqrsaxon.command.dto;

public record CreditAccountRequestDTO(String accountId, double amount, String currency) {
}
