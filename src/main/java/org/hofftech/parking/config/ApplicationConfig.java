package org.hofftech.parking.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.controller.ConsoleController;
import org.hofftech.parking.factory.CommandProcessorFactory;
import org.hofftech.parking.factory.PackingStrategyFactory;
import org.hofftech.parking.handler.impl.CommandHandlerImpl;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.service.*;
import org.hofftech.parking.util.telegram.TelegramAppender;
import org.hofftech.parking.util.telegram.TelegramBotService;
import org.hofftech.parking.util.telegram.TelegramPrintStream;
import org.hofftech.parking.validator.ParcelValidator;

/**
 * {@code ApplicationConfig} — класс конфигурации приложения.
 * <p>
 * В процессе инициализации создаются и настраиваются все необходимые зависимости,
 * включая репозиторий посылок, сервисы, фабрики процессоров команд и стратегии упаковки,
 * а также инициализируется Telegram-бот.
 * </p>
 *
 * @autor [Ваше Имя]
 * @версия 1.0
 * @с момента 2023-04-27
 */
@Getter
@Slf4j
public class ApplicationConfig {
    /**
     * Консольный слушатель для обработки команд пользователя.
     */
    private final ConsoleController consoleController;

    /**
     * Создаёт новый экземпляр {@code ApplicationConfig} и инициализирует все зависимости.
     */
    public ApplicationConfig() {
        log.info("Создаем зависимости...");

        // Инициализация репозитория и загрузка дефолтных посылок
        ParcelRepository parcelRepository = new ParcelRepository();
        parcelRepository.loadDefaultPackages();

        // Инициализация сервисов
        ParcelService parcelService = new ParcelService();
        TruckService truckService = new TruckService(parcelService);
        ParcelValidator parcelValidator = new ParcelValidator();

        // Инициализация фабрики процессоров команд
        CommandProcessorFactory processorFactory = getCommandProcessorFactory(
                parcelValidator, truckService, parcelRepository
        );

        // Инициализация парсера и обработчика команд
        CommandParser commandParser = new CommandParser();
        CommandHandlerImpl commandHandler = new CommandHandlerImpl(processorFactory, commandParser);

        // Инициализация контроллера консоли
        this.consoleController = new ConsoleController(commandHandler);

        // Инициализация компонентов Telegram
        initializeTelegramBot(commandHandler);
    }

    /**
     * Инициализирует Telegram-бота и настраивает интеграцию с консольным выводом.
     *
     * @param commandHandlerImpl обработчик команд, используемый Telegram-ботом
     */
    private void initializeTelegramBot(CommandHandlerImpl commandHandlerImpl) {
        String token = "7787231158:AAE90-cAJlHmEEF9Ds0g2Pm3xXu2RLcfeUo";
        String botName = "java_education_parking_bot";

        if (token == null || token.isEmpty()) {
            log.error("Переменная окружения TELEGRAM_BOT_TOKEN не установлена.");
            throw new IllegalStateException("Отсутствует токен Telegram-бота.");
        }

        if (botName == null || botName.isEmpty()) {
            log.error("Переменная окружения TELEGRAM_BOT_NAME не установлена.");
            throw new IllegalStateException("Отсутствует имя Telegram-бота.");
        }

        TelegramAppender telegramAppender = new TelegramAppender();
        TelegramPrintStream telegramPrintStream = new TelegramPrintStream(System.out, null);


        TelegramBotService telegramBotService = new TelegramBotService(
                commandHandlerImpl,
                telegramAppender,
                telegramPrintStream,
                token,
                botName
        );

        log.info("Telegram-бот инициализирован.");
    }

    /**
     * Создает и возвращает экземпляр {@link CommandProcessorFactory} с необходимыми зависимостями.
     *
     * @param parcelValidator   валидатор посылок
     * @param truckService      сервис управления грузовиками
     * @param parcelRepository  репозиторий для управления посылками
     * @return экземпляр {@link CommandProcessorFactory}
     */
    private CommandProcessorFactory getCommandProcessorFactory(
            ParcelValidator parcelValidator, TruckService truckService, ParcelRepository parcelRepository) {
        ParsingService parsingService = new ParsingService(parcelRepository);
        PackingStrategyFactory packingStrategyFactory = new PackingStrategyFactory(truckService);
        JsonProcessingService jsonProcessingService = new JsonProcessingService();
        FileProcessingService fileProcessingService = new FileProcessingService(
                parsingService, parcelValidator, truckService, jsonProcessingService, packingStrategyFactory);
        return new CommandProcessorFactory(parcelRepository, fileProcessingService, jsonProcessingService);
    }
}
