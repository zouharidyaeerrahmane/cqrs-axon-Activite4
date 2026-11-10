package com.zouhari.demoescqrsaxon.command.dto;

public record DebitAccountRequestDTO(String accountId, double amount, String currency) {
}
