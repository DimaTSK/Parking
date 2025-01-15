package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;

/**
 * Обработчик команды поиска посылки. Реализует интерфейс {@link CommandProcessor}.
 *
 * <p>Этот класс отвечает за обработку команды поиска посылки по её имени.
 * Он использует {@link ParcelRepository} для поиска посылки и выводит результат пользователю.</p>
 */
@RequiredArgsConstructor
@Slf4j
public class FindCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    /**
     * Выполняет обработку команды поиска посылки.
     *
     * <p>Метод выполняет следующие шаги:
     * <ul>
     *     <li>Извлекает имя посылки из объекта {@link ParsedCommand}.</li>
     *     <li>Проверяет, указано ли имя посылки.</li>
     *     <li>Использует {@link ParcelRepository#findPackage(String)} для поиска посылки по имени.</li>
     *     <li>Если посылка найдена, выводит её информацию пользователю.</li>
     *     <li>Если посылка не найдена, логирует сообщение об ошибке.</li>
     * </ul>
     * </p>
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для выполнения команды поиска
     * @throws IllegalArgumentException если имя посылки не указано или посылка не найдена
     */
    @Override
    public void execute(ParsedCommand command) {
        String name = command.getName();
        if (name == null || name.isEmpty()) {
            log.error("Имя посылки не указано.");
            return;
        }

        Parcel pkg = repository.findPackage(name);
        if (pkg != null) {
            System.out.println("Найдена посылка: " + pkg);
        } else {
            log.error("Посылка с именем '{}' не найдена.", name);
        }
    }
}
