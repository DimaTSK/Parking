package org.hofftech.parking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hofftech.parking.controller.TelegramController;
import org.hofftech.parking.exception.TelegramBotRegistrationException;
import org.hofftech.parking.factory.CommandFactory;
import org.hofftech.parking.factory.ParcelAlgorithmFactory;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.*;
import org.hofftech.parking.util.FileProcessingUtil;
import org.hofftech.parking.service.json.JsonProcessingService;
import org.hofftech.parking.parcer.ParsingService;
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

    // Существующие бины...

    @Bean
    public ParcelRepository parcelRepository() {
        ParcelRepository repository = new ParcelRepository();
        repository.loadDefaultParcels();
        return repository;
    }

    @Bean
    public OrderManagerService orderManagerService() {
        return new OrderManagerService();
    }

    @Bean
    public ParcelService packingService() {
        return new ParcelService();
    }

    @Bean
    public TruckService truckService(ParcelService parcelService) {
        return new TruckService(parcelService);
    }

    @Bean
    public ParcelValidator validatorService() {
        return new ParcelValidator();
    }

    @Bean
    public ParsingService parsingService(ParcelRepository parcelRepository) {
        return new ParsingService(parcelRepository);
    }

    @Bean
    public ParcelAlgorithmFactory packingStrategyFactory(TruckService truckService) {
        return new ParcelAlgorithmFactory(truckService);
    }
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }

    @Bean
    public JsonProcessingService jsonProcessingService(OrderManagerService orderManagerService, ObjectMapper objectMapper) {
        return new JsonProcessingService(orderManagerService, objectMapper);
    }

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

    @Bean
    public FileSavingService fileSavingService() {
        return new FileSavingService();
    }

    @Bean
    public CommandFactory commandFactory(
            ParcelRepository parcelRepository,
            FileProcessingUtil fileProcessingUtil,
            JsonProcessingService jsonProcessingService,
            FileSavingService fileSavingService,
            ParcelValidator parcelValidator,
            OrderManagerService orderManagerService,
            FormatterService formatterService) {
        return new CommandFactory(
                parcelRepository, fileProcessingUtil, jsonProcessingService,
                fileSavingService, parcelValidator, orderManagerService,formatterService);
    }

    @Bean
    public CommandTypeSelectionService commandTypeService() {
        return new CommandTypeSelectionService();
    }

    @Bean
    public CommandParser commandParser(CommandTypeSelectionService commandTypeSelectionService) {
        return new CommandParser(commandTypeSelectionService);
    }


    @Bean
    public FormatterService responseFormatter() {
        return new FormatterService();
    }

    /**
     * Создаёт и настраивает бин {@link TelegramController}, отвечающий за интеграцию с Telegram-ботом.
     * <p>
     * Регистрация бота производится при инициализации бина. В случае ошибки регистрации выбрасывается {@link RuntimeException}.
     * </p>
     *
     * @param token           токен бота, считываемый из свойств приложения
     * @param botName         имя бота, считываемое из свойств приложения
     * @param processorFactory зависимость {@link CommandFactory}
     * @param commandParser     зависимость {@link CommandParser}
     * @param formatterService зависимость {@link FormatterService}
     * @return экземпляр {@link TelegramController}
     */
    @Bean
    public TelegramController telegramBotController(
            @Value("${telegram.bot.token}") String token,
            @Value("${telegram.bot.name}") String botName,
            CommandFactory processorFactory,
            CommandParser commandParser,
            FormatterService formatterService) {
        TelegramController botController = new TelegramController(token, botName, processorFactory, commandParser, formatterService);
        try {
            botController.registerBot();
            return botController;
        } catch (Exception e) {
            throw new TelegramBotRegistrationException("Ошибка регистрации Telegram-бота", e);
        }
    }
}
