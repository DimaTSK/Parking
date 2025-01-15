package org.hofftech.parking.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.model.Parcel;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.repository.ParcelRepository;

import java.util.List;

/**
 * Обработчик команды вывода списка всех посылок. Реализует интерфейс {@link CommandProcessor}.
 *
 * <p>Этот класс отвечает за обработку команды отображения всех доступных посылок.
 * Он извлекает список посылок из {@link ParcelRepository} и выводит их пользователю.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class ListCommandProcessor implements CommandProcessor {
    private final ParcelRepository repository;

    /**
     * Выполняет обработку команды вывода списка всех посылок.
     *
     * <p>Метод выполняет следующие шаги:
     * <ul>
     *     <li>Извлекает все посылки из {@link ParcelRepository}.</li>
     *     <li>Если список посылок пуст, логирует сообщение о отсутствии доступных посылок.</li>
     *     <li>В противном случае, выводит список всех посылок.</li>
     * </ul>
     * </p>
     *
     * @param command объект {@link ParsedCommand}, содержащий данные для выполнения команды
     */
    @Override
    public void execute(ParsedCommand command) {
        List<Parcel> parcels = repository.getAllPackages();
        if (parcels.isEmpty()) {
            log.info("Нет доступных посылок.");
        } else {
            log.info("Список всех посылок:");
            parcels.forEach(pkg -> System.out.println(pkg.toString()));
        }
    }
}
