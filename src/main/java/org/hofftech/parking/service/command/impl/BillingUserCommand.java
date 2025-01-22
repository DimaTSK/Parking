package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.BillingException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.OrderManagerService;
import org.hofftech.parking.service.command.UserCommand;
/**
 * Класс реализации пользовательской команды для биллинга.
 */
@RequiredArgsConstructor
public class BillingUserCommand implements UserCommand {
    private final OrderManagerService orderManagerService;

    /**
     * Выполняет команду создания посылки на основе переданной команды.
     */
    @Override
    public String execute(ParsedCommand command) {
        String user = command.getUser();
        String dateFrom = command.getFrom();
        String dateTo = command.getTo();

        if (user != null && dateFrom != null && dateTo != null &&
                !user.isEmpty() && !dateFrom.isEmpty() && !dateTo.isEmpty()) {
            return orderManagerService.generateReport(user, dateFrom, dateTo);
        } else {
            throw new BillingException("Пользователь и диапазон дат должны быть указаны в BILLING");
        }

    }
}