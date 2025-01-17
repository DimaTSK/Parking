package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.exception.InvalidCommandException;
import org.hofftech.parking.exception.ParcelCreationException;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParcelStartPosition;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.validator.ParcelValidator;

import java.util.List;

/**
 * Обработчик команды создания посылки. Реализует интерфейс {@link CommandProcessor}.
 *
 * <p>Этот класс отвечает за обработку команды создания новой посылки.
 * Он валидирует входные данные, создает объект {@link Parcel} и сохраняет его
 * в {@link ParcelRepository}. В случае ошибок обработки, соответствующие
 * исключения логируются и выбрасываются для дальнейшей обработки.</p>
 */
@RequiredArgsConstructor
@Slf4j
public class CreateCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    private static final char DEFAULT_SYMBOL = ' ';

    /**
     * Выполняет обработку команды создания новой посылки.
     *
     * <p>Метод выполняет следующие шаги:
     * <ul>
     *     <li>Извлекает имя и форму посылки из объекта {@link ParsedCommand}.</li>
     *     <li>Проверяет наличие необходимых данных (имя и форма).</li>
     *     <li>Определяет символ посылки, используя предоставленный символ или устанавливая символ по умолчанию.</li>
     *     <li>Парсит и валидирует форму посылки с помощью {@link ParcelValidator}.</li>
     *     <li>Создает новый объект {@link Parcel} с заданными параметрами.</li>
     *     <li>Добавляет новый объект посылки в {@link ParcelRepository}.</li>
     *     <li>Логирует успешное создание посылки и выводит форму посылки на консоль.</li>
     * </ul>
     * </p>
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для создания посылки
     * @throws InvalidCommandException    если отсутствуют необходимые данные для создания посылки
     * @throws ParcelCreationException    если возникает ошибка при создании посылки из-за внутренней ошибки
     */
    @Override
    public void execute(ParsedCommand command) {
        try {
            String name = command.getName();
            String form = command.getForm();

            if (name == null || form == null) {
                throw new InvalidCommandException("Недостаточно данных для создания посылки.");
            }

            String symbolStr = command.getSymbol();
            char symbol = (symbolStr != null && !symbolStr.isEmpty()) ? symbolStr.charAt(0) : DEFAULT_SYMBOL;

            List<String> shape = ParcelValidator.parseAndValidateForm(form);
            Parcel newParcel = new Parcel(name, shape, symbol, new ParcelStartPosition(0, 0));
            repository.addPackage(newParcel);

            log.info("Посылка '{}' успешно создана.", name);

            StringBuilder output = new StringBuilder();
            output.append("Форма посылки:\n");
            shape.forEach(line -> output.append(line).append("\n"));

            System.out.println(output.toString());
        } catch (InvalidCommandException e) {
            log.error("Ошибка выполнения команды создания посылки: {}", e.getMessage());
            // Дополнительно можно выбросить или обработать исключение на более высоком уровне
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при создании посылки: {}", e.getMessage(), e);
            // Обработка других возможных исключений
            throw new ParcelCreationException("Не удалось создать посылку из-за внутренней ошибки.", e);
        }
    }
}
