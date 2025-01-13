package org.hofftech.parking.util.telegram;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Setter
@Slf4j
public final class TelegramAppender extends AppenderBase<ILoggingEvent> {
    private static AbsSender bot;
    private static String chatId;

    public static void initialize(AbsSender telegramBot) {
        if (telegramBot == null) {
            throw new IllegalArgumentException("Бот не может быть null");
        }
        bot = telegramBot;
    }


    public void setChatId(String telegramChatId) {
        chatId = telegramChatId;
    }


    @Override
    protected void append(ILoggingEvent event) {
        if (bot != null && chatId != null) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText(event.getFormattedMessage());
            try {
                bot.execute(message);
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения в Telegram: {}", e.getMessage(), e);
            }
        }
    }
}