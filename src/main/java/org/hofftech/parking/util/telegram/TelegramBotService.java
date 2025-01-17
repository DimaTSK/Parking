package org.hofftech.parking.util.telegram;


import lombok.extern.slf4j.Slf4j;
import org.hofftech.parking.handler.impl.CommandHandlerImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

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
 * @autor [Ваше Имя]
 * @версия 1.0
 * @с момента 2023-04-27
 */
@Slf4j
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
     */
    private final String token;

    /**
     * Имя пользователя Telegram-бота.
     */
    private final String botName;

    /**
     * Создаёт новый экземпляр {@code TelegramBotService} с указанными параметрами
     * и регистрирует бота в Telegram API.
     *
     * @param commandHandlerImpl реализация обработчика команд
     * @param telegramAppender   appender Telegram для логирования или обработки сообщений
     * @param printStream        поток вывода для отправки сообщений пользователям Telegram
     * @param token              токен аутентификации для Telegram-бота
     * @param botName            имя пользователя Telegram-бота
     */
    public TelegramBotService(CommandHandlerImpl commandHandlerImpl,
                              TelegramAppender telegramAppender,
                              TelegramPrintStream printStream,
                              String token,
                              String botName) {
        super(token);
        this.commandHandlerImpl = commandHandlerImpl;
        this.telegramAppender = telegramAppender;
        this.printStream = printStream;
        this.token = token;
        this.botName = botName;

        // Регистрация бота при создании экземпляра
        initializeTelegram();
    }

    /**
     * Инициализирует и регистрирует бота в TelegramBotsApi.
     */
    private void initializeTelegram() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            TelegramAppender.initialize(this);
            printStream.setBot(this);
            System.setOut(printStream);
            Runtime.getRuntime().addShutdownHook(new Thread(printStream::shutdown));
            log.info("Telegram-бот успешно зарегистрирован.");
        } catch (TelegramApiException e) {
            log.error("Ошибка при запуске Telegram-бота: {}", e.getMessage(), e);
        }
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
    public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
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
        return this.botName;
    }

    /**
     * Возвращает токен аутентификации Telegram-бота.
     *
     * @return токен бота как {@code String}
     */
    @Override
    public String getBotToken() {
        return this.token;
    }
}
