package org.hofftech.parking.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.listener.ConsoleListener;
import org.hofftech.parking.handler.impl.CommandHandlerImpl;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.FileProcessingService;
import org.hofftech.parking.service.JsonProcessingService;
import org.hofftech.parking.service.ParcelService;
import org.hofftech.parking.service.ParsingService;
import org.hofftech.parking.service.TelegramBotService;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.factory.CommandProcessorFactory;
import org.hofftech.parking.factory.PackingStrategyFactory;
import org.hofftech.parking.util.telegram.TelegramAppender;
import org.hofftech.parking.util.telegram.TelegramPrintStream;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Getter
@Slf4j
public class ApplicationConfig {
    private final ConsoleListener consoleListener;

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
        CommandHandlerImpl commandHandlerImpl = new CommandHandlerImpl(processorFactory, commandParser);
        this.consoleListener = new ConsoleListener(commandHandlerImpl);
        initializeTelegram(commandHandlerImpl);
    }

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