package org.hofftech.parking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.hofftech.parking.factory.CommandFactory;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.service.FormatterService;
import org.hofftech.parking.service.command.UserCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@ExtendWith(MockitoExtension.class)
class TelegramControllerTest {

    @Mock
    private CommandFactory commandFactory;

    @Mock
    private CommandParser commandParser;

    @Mock
    private FormatterService formatterService;

    @Mock
    private UserCommand userCommand;

    @InjectMocks
    private TelegramController telegramController;

    private final String BOT_TOKEN = "test_token";
    private final String BOT_NAME = "test_bot";

    @BeforeEach
    void setUp() {
        telegramController = new TelegramController(BOT_TOKEN, BOT_NAME, commandFactory, commandParser, formatterService);
    }

    @Test
    void onUpdateReceived_successfulMessageProcessing() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getChatId()).thenReturn(123456L);
        when(message.getText()).thenReturn("/start");

        ParsedCommand parsedCommand = new ParsedCommand(
                false,
                false,
                false,
                "user123",
                "2023-01-01",
                "2023-12-31",
                "parcelsText",
                "parcelsFile",
                "trucks",
                "inFile",
                false
        );
        parsedCommand.setCommandType(org.hofftech.parking.model.enums.CommandType.START);

        when(commandParser.parse("/start")).thenReturn(parsedCommand);
        when(commandFactory.createProcessor(org.hofftech.parking.model.enums.CommandType.START)).thenReturn(userCommand);
        when(userCommand.execute(parsedCommand)).thenReturn("Команда выполнена успешно.");
        when(formatterService.formatAsMarkdownCodeBlock("Команда выполнена успешно.")).thenReturn("```\nКоманда выполнена успешно.\n```");

        telegramController.onUpdateReceived(update);

        verify(commandParser, times(1)).parse("/start");
        verify(commandFactory, times(1)).createProcessor(org.hofftech.parking.model.enums.CommandType.START);
        verify(userCommand, times(1)).execute(parsedCommand);
        verify(formatterService, times(1)).formatAsMarkdownCodeBlock("Команда выполнена успешно.");
    }

    @Test
    void onUpdateReceived_commandExecutionThrowsException() throws Exception {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getChatId()).thenReturn(654321L);
        when(message.getText()).thenReturn("/invalid");

        ParsedCommand parsedCommand = new ParsedCommand(
                false,
                false,
                false,
                "user456",
                "2023-02-01",
                "2023-11-30",
                "parcelsText",
                "parcelsFile",
                "trucks",
                "inFile",
                false
        );
        parsedCommand.setCommandType(org.hofftech.parking.model.enums.CommandType.EXIT);

        when(commandParser.parse("/invalid")).thenReturn(parsedCommand);
        when(commandFactory.createProcessor(org.hofftech.parking.model.enums.CommandType.EXIT)).thenReturn(userCommand);
        when(userCommand.execute(parsedCommand)).thenThrow(new RuntimeException("Некорректная команда"));

        telegramController.onUpdateReceived(update);

        verify(commandParser, times(1)).parse("/invalid");
        verify(commandFactory, times(1)).createProcessor(org.hofftech.parking.model.enums.CommandType.EXIT);
        verify(userCommand, times(1)).execute(parsedCommand);
        verify(formatterService, never()).formatAsMarkdownCodeBlock(any());
    }

    @Test
    void onUpdateReceived_nonTextMessage() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        Message message = mock(Message.class);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(false);

        telegramController.onUpdateReceived(update);

        verify(commandParser, never()).parse(any());
        verify(commandFactory, never()).createProcessor(any());
        verify(userCommand, never()).execute(any());
        verify(formatterService, never()).formatAsMarkdownCodeBlock(any());
    }

    @Test
    void onUpdateReceived_noMessage() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(false);

        telegramController.onUpdateReceived(update);

        verify(commandParser, never()).parse(any());
        verify(commandFactory, never()).createProcessor(any());
        verify(userCommand, never()).execute(any());
        verify(formatterService, never()).formatAsMarkdownCodeBlock(any());
    }
}
