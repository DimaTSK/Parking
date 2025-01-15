package org.hofftech.parking.service;

import org.hofftech.parking.handler.impl.CommandHandlerImpl;
import org.hofftech.parking.util.telegram.TelegramAppender;
import org.hofftech.parking.util.telegram.TelegramPrintStream;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBotService extends TelegramLongPollingBot {

    private final CommandHandlerImpl commandHandlerImpl;
    private final TelegramAppender telegramAppender;
    private final TelegramPrintStream printStream;

    public static final String TOKEN = "7787231158:AAE90-cAJlHmEEF9Ds0g2Pm3xXu2RLcfeUo";
    public static final String BOT_NAME = "java_education_parking_bot";


    public TelegramBotService(String botToken, CommandHandlerImpl commandHandlerImpl,
                              TelegramAppender telegramAppender, TelegramPrintStream printStream) {
        super(botToken);
        this.commandHandlerImpl = commandHandlerImpl;
        this.telegramAppender = telegramAppender;
        this.printStream = printStream;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText();
            telegramAppender.setChatId(chatId.toString());
            printStream.setChatId(chatId.toString());
            commandHandlerImpl.handle(message);
        }
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
}