package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;

/**
 * Обработчик команды удаления посылки. Реализует интерфейс {@link CommandProcessor}.
 *
 * <p>Этот класс отвечает за обработку команды удаления посылки по её имени.
 * Он использует {@link ParcelRepository} для удаления посылки и логирует результат операции.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class DeleteCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    /**
     * Выполняет обработку команды удаления посылки.
     *
     * <p>Метод выполняет следующие шаги:
     * <ul>
     *     <li>Извлекает имя посылки из объекта {@link ParsedCommand}.</li>
     *     <li>Удаляет посылку с указанным именем из {@link ParcelRepository}.</li>
     *     <li>Логирует успешное удаление посылки.</li>
     * </ul>
     * </p>
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для выполнения команды удаления
     * @throws IllegalArgumentException если имя посылки не указано или посылка не найдена
     */
    @Override
    public void execute(ParsedCommand command) {
        String name = command.getName();
        repository.deletePackage(name);

        log.info("Посылка '{}' успешно удалена.", name);
    }
}
