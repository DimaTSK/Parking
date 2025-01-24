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

    // Константы для названий полей
    private static final String USER_FIELD = "user";
    private static final String DATE_FROM_FIELD = "dateFrom";
    private static final String DATE_TO_FIELD = "dateTo";

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
            missingFields.add(USER_FIELD);
        }
        if (dateFrom == null || dateFrom.isEmpty()) {
            missingFields.add(DATE_FROM_FIELD);
        }
        if (dateTo == null || dateTo.isEmpty()) {
            missingFields.add(DATE_TO_FIELD);
        }

        if (missingFields.isEmpty()) {
            return orderManagerService.generateReport(user, dateFrom, dateTo);
        } else {
            String missing = String.join(", ", missingFields);
            throw new BillingException("Недостаточно данных для выполнения BILLING команды. Отсутствующие параметры: " + missing);
        }
    }
}
