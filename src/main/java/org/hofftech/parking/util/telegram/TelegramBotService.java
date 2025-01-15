package org.hofftech.parking.util.telegram;

import org.hofftech.parking.handler.impl.CommandHandlerImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * {@code TelegramBotService} — сервисный класс, расширяющий {@link TelegramLongPollingBot}
 * для обработки входящих обновлений от Telegram. Он обрабатывает сообщения пользователей и
 * делегирует команды соответствующему обработчику команд.
 * <p>
 * Этот сервис интегрируется с API Telegram для получения и отправки сообщений, используя
 * обработчик команд для обработки конкретных команд и поток вывода для отправки ответов
 * пользователю.
 * </p>
 *
 * @автор [Ваше Имя]
 * @версия 1.0
 * @с момента 2023-04-27
 */
public class TelegramBotService extends TelegramLongPollingBot {

    /**
     * Реализация обработчика команд, отвечающая за обработку команд пользователей.
     */
    private final CommandHandlerImpl commandHandlerImpl;

    /**
     * Appender для обработки логирования или добавления сообщений, специфичных для Telegram.
     */
    private final TelegramAppender telegramAppender;

    /**
     * Поток вывода для отправки сообщений обратно пользователю Telegram.
     */
    private final TelegramPrintStream printStream;

    /**
     * Токен аутентификации для Telegram-бота.
     * <p>
     * <b>Примечание:</b> Рекомендуется хранить токены в безопасности и не размещать их в исходном коде.
     * Рассмотрите возможность использования переменных среды или файлов конфигурации для управления
     * конфиденциальной информацией.
     * </p>
     */
    public static final String TOKEN = "7787231158:AAE90-cAJlHmEEF9Ds0g2Pm3xXu2RLcfeUo";

    /**
     * Имя пользователя Telegram-бота.
     */
    public static final String BOT_NAME = "java_education_parking_bot";

    /**
     * Создаёт новый экземпляр {@code TelegramBotService} с указанными параметрами.
     *
     * @param botToken           токен аутентификации для Telegram-бота
     * @param commandHandlerImpl реализация обработчика команд
     * @param telegramAppender   appender Telegram для логирования или обработки сообщений
     * @param printStream        поток вывода для отправки сообщений пользователям Telegram
     */
    public TelegramBotService(String botToken, CommandHandlerImpl commandHandlerImpl,
                              TelegramAppender telegramAppender, TelegramPrintStream printStream) {
        super(botToken);
        this.commandHandlerImpl = commandHandlerImpl;
        this.telegramAppender = telegramAppender;
        this.printStream = printStream;
    }

    /**
     * Обрабатывает входящие обновления от Telegram. Обрабатывает сообщения, содержащие текст,
     * и делегирует обработку команд {@code CommandHandlerImpl}.
     * <p>
     * Когда получено сообщение с текстом, метод устанавливает соответствующий идентификатор чата
     * для appender и потока вывода перед обработкой команды.
     * </p>
     *
     * @param update обновление, полученное от Telegram, содержащее сообщение и метаданные
     */
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

    /**
     * Возвращает имя пользователя Telegram-бота.
     *
     * @return имя пользователя бота как {@code String}
     */
    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
}
