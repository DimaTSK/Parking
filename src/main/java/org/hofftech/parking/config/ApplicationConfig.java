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

@Configuration
public class ApplicationConfig {

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
    public JsonProcessingService jsonProcessingService(OrderManagerService orderManagerService) {
        return new JsonProcessingService(orderManagerService);
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
    public FileSavingUtil fileSavingService() {
        return new FileSavingUtil();
    }

    @Bean
    public CommandFactory commandProcessorFactory(
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

    @Bean
    public CommandTypeService commandTypeService() {
        return new CommandTypeService();
    }

    @Bean
    public CommandParser commandParser(CommandTypeService commandTypeService) {
        return new CommandParser(commandTypeService);
    }

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