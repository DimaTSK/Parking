package org.hofftech.parking.handler.impl;

import org.hofftech.parking.exception.CommandProcessingException;
import org.hofftech.parking.model.ParsedCommand;
import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.parcer.CommandParser;
import org.hofftech.parking.processor.CommandProcessor;
import org.hofftech.parking.factory.CommandProcessorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class CommandHandlerImplTest {

    @Mock
    private CommandProcessorFactory processorFactory;

    @Mock
    private CommandParser commandParser;

    @Mock
    private CommandProcessor commandProcessor;

    @InjectMocks
    private CommandHandlerImpl commandHandler;

    // Пример команды для создания посылки
    private final String createCommandString = "create -name ParcelA -form Box -symbol B";

    private ParsedCommand createParsedCommand;

    @BeforeEach
    void setUp() {
        createParsedCommand = new ParsedCommand(
                true,    // saveToFile
                false,   // useEasyAlgorithm
                false,   // useEvenAlgorithm
                "ParcelA,ParcelB", // parcelsText
                null,    // parcelsFile
                "6x6,6x6", // trucks
                null,    // inFile
                false    // withCount
        );
        createParsedCommand.setCommandType(CommandType.CREATE);
        createParsedCommand.setName("ParcelA");
        createParsedCommand.setForm("Box");
        createParsedCommand.setSymbol("B");
    }

    @Test
    void handle_CreateCommand_SuccessfulProcessing() {
        when(commandParser.parse(createCommandString)).thenReturn(createParsedCommand);
        when(processorFactory.getProcessor(CommandType.CREATE)).thenReturn(commandProcessor);


        assertDoesNotThrow(() -> commandHandler.handle(createCommandString));


        verify(commandParser, times(1)).parse(createCommandString);
        verify(processorFactory, times(1)).getProcessor(CommandType.CREATE);
        verify(commandProcessor, times(1)).execute(createParsedCommand);
    }

    @Test
    void handle_UpdateCommand_SuccessfulProcessing() {
        String updateCommandString = "update -oldName ParcelA -name ParcelA1 -form Box2 -symbol B2";
        ParsedCommand updateParsedCommand = new ParsedCommand(
                false,   // saveToFile
                false,   // useEasyAlgorithm
                false,   // useEvenAlgorithm
                null,    // parcelsText
                null,    // parcelsFile
                null,    // trucks
                null,    // inFile
                false    // withCount
        );
        updateParsedCommand.setCommandType(CommandType.UPDATE);
        updateParsedCommand.setOldName("ParcelA");
        updateParsedCommand.setName("ParcelA1");
        updateParsedCommand.setForm("Box2");
        updateParsedCommand.setSymbol("B2");

        when(commandParser.parse(updateCommandString)).thenReturn(updateParsedCommand);
        when(processorFactory.getProcessor(CommandType.UPDATE)).thenReturn(commandProcessor);

        assertDoesNotThrow(() -> commandHandler.handle(updateCommandString));

        verify(commandParser, times(1)).parse(updateCommandString);
        verify(processorFactory, times(1)).getProcessor(CommandType.UPDATE);
        verify(commandProcessor, times(1)).execute(updateParsedCommand);
    }

    @Test
    void handle_ProcessorNotFoundThrowsException() {

        when(commandParser.parse(createCommandString)).thenReturn(createParsedCommand);
        when(processorFactory.getProcessor(CommandType.CREATE)).thenReturn(null);


        CommandProcessingException exception = assertThrows(
                CommandProcessingException.class,
                () -> commandHandler.handle(createCommandString)
        );


        assertTrue(exception.getMessage().contains("Процессор для команды не найден: " + CommandType.CREATE));


        verify(commandParser, times(1)).parse(createCommandString);
        verify(processorFactory, times(1)).getProcessor(CommandType.CREATE);
        verify(commandProcessor, never()).execute(any());
    }

    @Test
    void handle_CommandProcessingExceptionThrownByProcessor() {

        when(commandParser.parse(createCommandString)).thenReturn(createParsedCommand);
        when(processorFactory.getProcessor(CommandType.CREATE)).thenReturn(commandProcessor);
        doThrow(new CommandProcessingException("Ошибка при выполнении")).when(commandProcessor).execute(createParsedCommand);


        CommandProcessingException exception = assertThrows(
                CommandProcessingException.class,
                () -> commandHandler.handle(createCommandString)
        );

        assertEquals("Ошибка при выполнении", exception.getMessage());


        verify(commandParser, times(1)).parse(createCommandString);
        verify(processorFactory, times(1)).getProcessor(CommandType.CREATE);
        verify(commandProcessor, times(1)).execute(createParsedCommand);
    }

    @Test
    void handle_OtherExceptionThrown() {

        when(commandParser.parse(createCommandString)).thenReturn(createParsedCommand);
        when(processorFactory.getProcessor(CommandType.CREATE)).thenReturn(commandProcessor);
        doThrow(new RuntimeException("Неизвестная ошибка")).when(commandProcessor).execute(createParsedCommand);


        CommandProcessingException exception = assertThrows(
                CommandProcessingException.class,
                () -> commandHandler.handle(createCommandString)
        );


        assertEquals("Ошибка обработки команды", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Неизвестная ошибка", exception.getCause().getMessage());


        verify(commandParser, times(1)).parse(createCommandString);
        verify(processorFactory, times(1)).getProcessor(CommandType.CREATE);
        verify(commandProcessor, times(1)).execute(createParsedCommand);
    }

    @Test
    void handle_InvalidCommandType_ThrowsException() {

        String invalidCommandString = "invalid -param value";
        ParsedCommand invalidParsedCommand = new ParsedCommand(
                false,   // saveToFile
                false,   // useEasyAlgorithm
                false,   // useEvenAlgorithm
                null,    // parcelsText
                null,    // parcelsFile
                null,    // trucks
                null,    // inFile
                false    // withCount
        );
        invalidParsedCommand.setCommandType(null); // Некорректный тип

        when(commandParser.parse(invalidCommandString)).thenReturn(invalidParsedCommand);


        CommandProcessingException exception = assertThrows(
                CommandProcessingException.class,
                () -> commandHandler.handle(invalidCommandString)
        );


        assertTrue(exception.getMessage().contains("Процессор для команды не найден"));

        verify(commandParser, times(1)).parse(invalidCommandString);
        verify(processorFactory, times(1)).getProcessor(null);
        verify(commandProcessor, never()).execute(any());
    }
}
