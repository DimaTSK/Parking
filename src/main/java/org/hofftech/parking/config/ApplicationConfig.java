package org.hofftech.parking.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.controller.ConsoleController;
import org.hofftech.parking.handler.CommandHandler;
import org.hofftech.parking.handler.impl.CommandHandlerImpl;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonProcessingService;
import org.hofftech.parking.service.ParcelService;
import org.hofftech.parking.service.ParsingService;
import org.hofftech.parking.util.telegram.TelegramBotService;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.factory.CommandProcessorFactory;
import org.hofftech.parking.factory.PackingStrategyFactory;
import org.hofftech.parking.util.telegram.TelegramAppender;
import org.hofftech.parking.util.telegram.TelegramPrintStream;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Конфигурационный класс приложения.
 * <p>
 * Этот класс отвечает за инициализацию всех необходимых зависимостей и компонентов
 * приложения, включая консольный слушатель и Telegram-бота. Он создает экземпляры
 * сервисов, фабрик и других компонентов, необходимых для работы приложения.
 * </p>
 *
 * <p>
 * Основные компоненты включают:
 * <ul>
 *     <li>{@link ConsoleController} для прослушивания команд пользователя в консоли.</li>
 *     <li>{@link CommandHandlerImpl} для обработки пользовательских команд.</li>
 *     <li>Инициализацию Telegram-бота для взаимодействия через Telegram.</li>
 * </ul>
 * </p>
 *
 * <p>
 * При создании экземпляра этого класса выполняется загрузка стандартных пакетов
 * в {@link ParcelRepository} и настройка всех необходимых сервисов и фабрик.
 * Также настраивается вывод System.out в Telegram через {@link TelegramPrintStream}.
 * </p>
 *
 * <p>
 * Добавлен также обработчик завершения работы приложения для корректного
 * завершения работы Telegram-бота.
 * </p>
 *
 * @author
 * @version 1.0
 */
@Getter
@Slf4j
public class ApplicationConfig {
    /**
     * Консольный слушатель для обработки команд пользователя.
     */
    private final ConsoleController consoleController;

    /**
     * Конструктор класса {@link ApplicationConfig}.
     * <p>
     * В процессе инициализации создаются и настраиваются все необходимые зависимости,
     * включая репозиторий посылок, сервисы, фабрики процессоров команд и стратегии упаковки,
     * а также инициализируется Telegram-бот.
     * </p>
     */
    public ApplicationConfig() {
        log.info("Создаем зависимости...");
        ParcelRepository parcelRepository = new ParcelRepository();
        parcelRepository.loadDefaultPackages();
        ParcelService parcelService = new ParcelService();
        TruckService truckService = new TruckService(parcelService);
        ParcelValidator parcelValidator = new ParcelValidator();
        CommandProcessorFactory processorFactory = getCommandProcessorFactory(
                parcelValidator, truckService, parcelRepository
        );
        CommandParser commandParser = new CommandParser();
        CommandHandler commandHandler = new CommandHandlerImpl(processorFactory, commandParser);
        this.consoleController = new ConsoleController(commandHandler);
        initializeTelegram((CommandHandlerImpl) commandHandler);
    }

    /**
     * Инициализирует Telegram-бота и настраивает интеграцию с консольным выводом.
     * <p>
     * Создает экземпляры необходимых компонентов для работы Telegram-бота, регистрирует бота
     * и перенаправляет стандартный вывод {@code System.out} в Telegram через {@link TelegramPrintStream}.
     * Также добавляет обработчик завершения работы для корректного закрытия потоков.
     * </p>
     *
     * @param commandHandlerImpl обработчик команд, используемый Telegram-ботом
     */
    private static void initializeTelegram(CommandHandlerImpl commandHandlerImpl) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            TelegramAppender telegramAppender = new TelegramAppender();
            TelegramPrintStream telegramPrintStream = new TelegramPrintStream(System.out, null);
            TelegramBotService telegramBotService = new TelegramBotService(
                    TelegramBotService.TOKEN,
                    commandHandlerImpl,
                    telegramAppender,
                    telegramPrintStream
            );
            botsApi.registerBot(telegramBotService);
            TelegramAppender.initialize(telegramBotService);
            telegramPrintStream.setBot(telegramBotService);
            System.setOut(telegramPrintStream);
            Runtime.getRuntime().addShutdownHook(new Thread(telegramPrintStream::shutdown));
        } catch (Exception e) {
            log.error("Ошибка при запуске Telegram-бота: {}", e.getMessage(), e);
        }
    }

    /**
     * Создает и возвращает экземпляр {@link CommandProcessorFactory} с необходимыми зависимостями.
     * <p>
     * Инициализирует сервисы парсинга и обработки файлов, а также фабрику стратегий упаковки,
     * которые затем используются для создания фабрики процессоров команд.
     * </p>
     *
     * @param parcelValidator валидатор посылок
     * @param truckService сервис управления грузовиками
     * @param parcelRepository репозиторий для управления посылками
     * @return экземпляр {@link CommandProcessorFactory}
     */
    private static CommandProcessorFactory getCommandProcessorFactory(
            ParcelValidator parcelValidator, TruckService truckService, ParcelRepository parcelRepository) {
        ParsingService parsingService = new ParsingService(parcelRepository);
        PackingStrategyFactory packingStrategyFactory = new PackingStrategyFactory(truckService);
        JsonProcessingService jsonProcessingService = new JsonProcessingService();
        FileProcessingService fileProcessingService = new FileProcessingService(
                parsingService, parcelValidator, truckService, jsonProcessingService, packingStrategyFactory);
        return new CommandProcessorFactory(parcelRepository, fileProcessingService, jsonProcessingService);
    }
}
