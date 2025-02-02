package org.hofftech.parking.controller;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.factory.CommandFactory;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.FormatterService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Контроллер для Telegram-бота, отвечающий за обработку входящих сообщений и выполнение соответствующих команд.
 * <p>
 * Класс расширяет {@link TelegramLongPollingBot} и использует {@link CommandFactory}, {@link CommandParser}
 * и {@link FormatterService} для обработки и выполнения пользовательских команд.
 * </p>
 */
@Slf4j
@Getter
public class TelegramController extends TelegramLongPollingBot {

    private final String botToken;
    private final String botName;
    private static final String PARSE_MODE = ParseMode.MARKDOWNV2;
    private final CommandFactory processorFactory;
    private final CommandParser commandParser;
    private final FormatterService formatterService;

    public TelegramController(String botToken, String botName,
                              CommandFactory processorFactory,
                              CommandParser commandParser,
                              FormatterService formatterService) {
        super("");
        this.botToken = botToken;
        this.botName = botName;
        this.processorFactory = processorFactory;
        this.commandParser = commandParser;
        this.formatterService = formatterService;
    }

    /**
     * Возвращает имя бота.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botName;
    }

    /**
     * Возвращает токен бота.
     *
     * @return токен бота
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Обрабатывает входящие обновления от Telegram.
     * <p>
     * При получении текстового сообщения, команда разбирается и выполняется соответствующим процессором.
     * Результат выполнения отправляется обратно пользователю в формате Markdown.
     * </p>
     *
     * @param update объект обновления, полученного от Telegram
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            try {
                ParsedCommand parsedCommand = commandParser.parse(message);
                String response = processorFactory.createProcessor(parsedCommand.getCommandType())
                        .execute(parsedCommand);
                String markdownResponse = formatterService.formatAsMarkdownCodeBlock(response);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId.toString());
                sendMessage.setText(markdownResponse);
                sendMessage.setParseMode(PARSE_MODE);
                execute(sendMessage);
            } catch (Exception e) {
                log.error("Ошибка обработки команды из Telegram: {}", e.getMessage());
                // Можно добавить отправку сообщения об ошибке пользователю
                sendErrorMessage(chatId);
            }
        }
    }

    /**
     * Отправляет сообщение об ошибке пользователю.
     *
     * @param chatId ID чата, куда отправляется сообщение
     */
    private void sendErrorMessage(Long chatId) {
        try {
            SendMessage errorMessage = new SendMessage();
            errorMessage.setChatId(chatId.toString());
            errorMessage.setText("Произошла ошибка при обработке вашей команды.");
            errorMessage.setParseMode(PARSE_MODE);
            execute(errorMessage);
        } catch (Exception ex) {
            log.error("Ошибка отправки сообщения об ошибке: {}", ex.getMessage());
        }
    }

    /**
     * Регистрирует бота в Telegram API.
     * <p>
     * Использует {@link TelegramBotsApi} для регистрации текущего экземпляра бота.
     * В случае ошибки регистрации выбрасывает {@link RuntimeException}.
     * </p>
     */
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            log.info("Бот успешно зарегистрирован.");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка регистрации Telegram-бота", e);
        }
    }
}
