package com.zouhari.demoescqrsaxon.command.commands;

import com.zouhari.demoescqrsaxon.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class UpdateAccountStatusCommand {
    @TargetAggregateIdentifier
    private final String id;
    private final AccountStatus status;
}
