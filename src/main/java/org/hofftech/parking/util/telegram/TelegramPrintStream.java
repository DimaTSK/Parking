package org.hofftech.parking.util.telegram;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * {@code TelegramPrintStream} — это {@link PrintStream}, который переопределяет метод {@code println}
 * для отправки сообщений в Telegram через указанного бота. Сообщения отправляются асинхронно,
 * используя {@link ExecutorService}.
 * <p>
 * Класс предназначен для интеграции стандартного вывода с Telegram, позволяя перенаправлять
 * выводимые сообщения непосредственно в чат Telegram.
 * </p>
 *
 * <p><b>Примечание:</b> Необходимо вызвать метод {@code shutdown} при завершении работы
 * приложения для корректного завершения {@code ExecutorService}.</p>
 *
 * @автор [Ваше Имя]
 * @версия 1.0
 * @с момента 2023-04-27
 */
@Slf4j
@Setter
public final class TelegramPrintStream extends PrintStream {

    /**
     * Объект {@link AbsSender}, отвечающий за отправку сообщений через Telegram-бота.
     */
    private AbsSender bot;

    /**
     * Идентификатор чата в Telegram, куда будут отправляться сообщения.
     */
    @Setter
    private String chatId;

    /**
     * {@link ExecutorService} для выполнения асинхронных задач по отправке сообщений.
     */
    private final ExecutorService executorService;

    /**
     * Создаёт новый экземпляр {@code TelegramPrintStream} с указанным выходным потоком и ботом.
     *
     * @param out  выходной поток, который будет использоваться {@code PrintStream}
     * @param bot  объект {@link AbsSender}, используемый для отправки сообщений в Telegram
     */
    public TelegramPrintStream(OutputStream out, AbsSender bot) {
        super(out);
        this.bot = bot;
        this.executorService = Executors.newSingleThreadExecutor();
        this.chatId = null;
    }

    /**
     * Переопределяет метод {@code println} для отправки сообщения в Telegram после вывода в стандартный поток.
     *
     * @param message сообщение, которое необходимо вывести и отправить
     */
    @Override
    public void println(String message) {
        super.println(message);
        if (this.chatId != null) {
            sendToTelegramAsync(message);
        }
    }

    /**
     * Асинхронно отправляет сообщение в Telegram-чат, используя {@link AbsSender}.
     * <p>
     * Сообщение форматируется с использованием MarkdownV2 и оборачивается в блок кода.
     * </p>
     *
     * @param message сообщение, которое необходимо отправить в Telegram
     */
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

    /**
     * Завершает работу {@code ExecutorService}, освобождая ресурсы.
     * <p>
     * Рекомендуется вызывать этот метод при завершении работы приложения для корректного завершения
     * асинхронных задач по отправке сообщений.
     * </p>
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
