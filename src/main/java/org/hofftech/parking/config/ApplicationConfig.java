package org.hofftech.parking.config;

import org.hofftech.parking.controller.TelegramController;
import org.hofftech.parking.factory.CommandFactory;
import org.hofftech.parking.factory.ParcelAlgorithmFactory;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.CommandTypeService;
import org.hofftech.parking.util.FileProcessingUtil;
import org.hofftech.parking.util.FileSavingUtil;
import org.hofftech.parking.service.json.JsonProcessingService;
import org.hofftech.parking.service.OrderManagerService;
import org.hofftech.parking.service.ParcelService;
import org.hofftech.parking.parcer.ParsingService;
import org.hofftech.parking.service.TruckService;
import org.hofftech.parking.validator.ParcelValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс приложения, отвечающий за настройку и создание бинов Spring.
 * <p>
 * Этот класс определяет все необходимые бины, включая репозитории, сервисы, утилиты и контроллеры,
 * которые используются в приложении. Каждый бин создается с помощью аннотации {@link Bean} и
 * управляется контейнером Spring.
 * </p>
 */
@Configuration
public class ApplicationConfig {

    /**
     * Создает и настраивает бин {@link ParcelRepository}.
     * <p>
     * Загружает стандартные посылки при инициализации репозитория.
     * </p>
     *
     * @return экземпляр {@link ParcelRepository}
     */
    @Bean
    public ParcelRepository parcelRepository() {
        ParcelRepository repository = new ParcelRepository();
        repository.loadDefaultParcels();
        return repository;
    }

    /**
     * Создает и настраивает бин {@link OrderManagerService}.
     *
     * @return экземпляр {@link OrderManagerService}
     */
    @Bean
    public OrderManagerService orderManagerService() {
        return new OrderManagerService();
    }

    /**
     * Создает и настраивает бин {@link ParcelService}, отвечающий за упаковку.
     *
     * @return экземпляр {@link ParcelService}
     */
    @Bean
    public ParcelService packingService() {
        return new ParcelService();
    }

    /**
     * Создает и настраивает бин {@link TruckService}, отвечающий за управление грузовиками.
     *
     * @param parcelService зависимость {@link ParcelService}
     * @return экземпляр {@link TruckService}
     */
    @Bean
    public TruckService truckService(ParcelService parcelService) {
        return new TruckService(parcelService);
    }

    /**
     * Создает и настраивает бин {@link ParcelValidator}, отвечающий за валидацию посылок.
     *
     * @return экземпляр {@link ParcelValidator}
     */
    @Bean
    public ParcelValidator validatorService() {
        return new ParcelValidator();
    }

    /**
     * Создает и настраивает бин {@link ParsingService}, отвечающий за парсинг данных.
     *
     * @param parcelRepository зависимость {@link ParcelRepository}
     * @return экземпляр {@link ParsingService}
     */
    @Bean
    public ParsingService parsingService(ParcelRepository parcelRepository) {
        return new ParsingService(parcelRepository);
    }

    /**
     * Создает и настраивает бин {@link ParcelAlgorithmFactory}, отвечающий за стратегии упаковки.
     *
     * @param truckService зависимость {@link TruckService}
     * @return экземпляр {@link ParcelAlgorithmFactory}
     */
    @Bean
    public ParcelAlgorithmFactory packingStrategyFactory(TruckService truckService) {
        return new ParcelAlgorithmFactory(truckService);
    }

    /**
     * Создает и настраивает бин {@link JsonProcessingService}, отвечающий за обработку JSON данных.
     *
     * @param orderManagerService зависимость {@link OrderManagerService}
     * @return экземпляр {@link JsonProcessingService}
     */
    @Bean
    public JsonProcessingService jsonProcessingService(OrderManagerService orderManagerService) {
        return new JsonProcessingService(orderManagerService);
    }

    /**
     * Создает и настраивает бин {@link FileProcessingUtil}, отвечающий за обработку файлов.
     *
     * @param parsingService           зависимость {@link ParsingService}
     * @param parcelValidator          зависимость {@link ParcelValidator}
     * @param truckService             зависимость {@link TruckService}
     * @param jsonProcessingService    зависимость {@link JsonProcessingService}
     * @param parcelAlgorithmFactory   зависимость {@link ParcelAlgorithmFactory}
     * @param orderManagerService      зависимость {@link OrderManagerService}
     * @return экземпляр {@link FileProcessingUtil}
     */
    @Bean
    public FileProcessingUtil fileProcessingService(
            ParsingService parsingService,
            ParcelValidator parcelValidator,
            TruckService truckService,
            JsonProcessingService jsonProcessingService,
            ParcelAlgorithmFactory parcelAlgorithmFactory,
            OrderManagerService orderManagerService) {
        return new FileProcessingUtil(
                parsingService, parcelValidator, truckService,
                jsonProcessingService, parcelAlgorithmFactory, orderManagerService);
    }

    /**
     * Создает и настраивает бин {@link FileSavingUtil}, отвечающий за сохранение файлов.
     *
     * @return экземпляр {@link FileSavingUtil}
     */
    @Bean
    public FileSavingUtil fileSavingService() {
        return new FileSavingUtil();
    }

    /**
     * Создает и настраивает бин {@link CommandFactory}, отвечающий за создание команд.
     *
     * @param parcelRepository      зависимость {@link ParcelRepository}
     * @param fileProcessingUtil    зависимость {@link FileProcessingUtil}
     * @param jsonProcessingService зависимость {@link JsonProcessingService}
     * @param fileSavingUtil        зависимость {@link FileSavingUtil}
     * @param parcelValidator       зависимость {@link ParcelValidator}
     * @param orderManagerService   зависимость {@link OrderManagerService}
     * @return экземпляр {@link CommandFactory}
     */
    @Bean
    public CommandFactory commandFactory(
            ParcelRepository parcelRepository,
            FileProcessingUtil fileProcessingUtil,
            JsonProcessingService jsonProcessingService,
            FileSavingUtil fileSavingUtil,
            ParcelValidator parcelValidator,
            OrderManagerService orderManagerService) {
        return new CommandFactory(
                parcelRepository, fileProcessingUtil, jsonProcessingService,
                fileSavingUtil, parcelValidator, orderManagerService);
    }

    /**
     * Создает и настраивает бин {@link CommandTypeService}, отвечающий за управление типами команд.
     *
     * @return экземпляр {@link CommandTypeService}
     */
    @Bean
    public CommandTypeService commandTypeService() {
        return new CommandTypeService();
    }

    /**
     * Создает и настраивает бин {@link CommandParser}, отвечающий за парсинг команд.
     *
     * @param commandTypeService зависимость {@link CommandTypeService}
     * @return экземпляр {@link CommandParser}
     */
    @Bean
    public CommandParser commandParser(CommandTypeService commandTypeService) {
        return new CommandParser(commandTypeService);
    }

    /**
     * Создает и настраивает бин {@link TelegramController}, отвечающий за интеграцию с Telegram-ботом.
     * <p>
     * Регистрация бота производится при инициализации бина. В случае ошибки регистрации выбрасывается {@link RuntimeException}.
     * </p>
     *
     * @param token           токен бота, считываемый из свойств приложения
     * @param botName         имя бота, считываемое из свойств приложения
     * @param processorFactory зависимость {@link CommandFactory}
     * @param commandParser     зависимость {@link CommandParser}
     * @return экземпляр {@link TelegramController}
     */
    @Bean
    public TelegramController telegramBotController(
            @Value("${telegram.bot.token}") String token,
            @Value("${telegram.bot.name}") String botName,
            CommandFactory processorFactory,
            CommandParser commandParser) {
        TelegramController botController = new TelegramController(token, botName, processorFactory, commandParser);
        try {
            botController.registerBot();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка регистрации Telegram-бота", e);
        }
        return botController;
    }
}
