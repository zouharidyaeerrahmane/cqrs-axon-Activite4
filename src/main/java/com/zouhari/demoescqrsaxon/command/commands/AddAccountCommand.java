package com.zouhari.demoescqrsaxon.command.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class AddAccountCommand {
    @TargetAggregateIdentifier
    private final String id;
    private final double initialBalance;
    private final String currency;
}
