package org.hofftech.parking.util.telegram;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Setter
public final class TelegramPrintStream extends PrintStream {
    private AbsSender bot;

    @Setter
    private String chatId;
    private final ExecutorService executorService;


    public TelegramPrintStream(OutputStream out, AbsSender bot) {
        super(out);
        this.bot = bot;
        this.executorService = Executors.newSingleThreadExecutor();
        this.chatId = null;
    }


    @Override
    public void println(String message) {
        super.println(message);
        if (this.chatId != null) {
            sendToTelegramAsync(message);
        }
    }

    private void sendToTelegramAsync(String message) {
        if (bot == null) {
            log.error("Бот не установлен. Сообщение не отправлено: {}", message);
            return;
        }

        executorService.submit(() -> {
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText("```\n" + message + "\n```");
                sendMessage.setParseMode("MarkdownV2");
                bot.execute(sendMessage);
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения в Telegram: {}", e.getMessage(), e);
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
}