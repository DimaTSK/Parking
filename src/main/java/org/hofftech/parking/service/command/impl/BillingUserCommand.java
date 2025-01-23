package org.hofftech.parking.service.command.impl;

import lombok.RequiredArgsConstructor;
import org.hofftech.parking.exception.BillingException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.service.OrderManagerService;
import org.hofftech.parking.service.command.UserCommand;

import java.util.ArrayList;
import java.util.List;

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

        List<String> missingFields = new ArrayList<>();

        if (user == null || user.isEmpty()) {
            missingFields.add("user");
        }
        if (dateFrom == null || dateFrom.isEmpty()) {
            missingFields.add("dateFrom");
        }
        if (dateTo == null || dateTo.isEmpty()) {
            missingFields.add("dateTo");
        }

        if (missingFields.isEmpty()) {
            return orderManagerService.generateReport(user, dateFrom, dateTo);
        } else {
            String missing = String.join(", ", missingFields);
            throw new BillingException("Недостаточно данных для выполнения BILLING команды. Отсутствующие параметры: " + missing);
        }
    }
}
