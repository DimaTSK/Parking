package org.hofftech.parking.controller;

import org.hofftech.parking.handler.CommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleControllerTest {

    @Mock
    private CommandHandler commandHandler;

    @InjectMocks
    private ConsoleController consoleController;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testStart() {
        String expectedMessage = "Привет! Это приложение по перекладыванию посылочек.";
        String actualMessage = consoleController.start();
        assertThat(actualMessage).isEqualTo(expectedMessage);
        verifyNoInteractions(commandHandler);
    }

    @Test
    void testFind_WithArguments() {
        String args = "id=123";
        String command = "find " + args;
        String expectedResponse = "Found parcel with id=123";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.find(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testFind_WithoutArguments() {
        String args = "";
        String command = "find " + args;
        String expectedResponse = "All parcels listed";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.find(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testCreate_WithArguments() {
        String args = "name=NewParcel";
        String command = "create " + args;
        String expectedResponse = "Parcel created successfully";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.create(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testCreate_WithoutArguments() {
        String args = "";
        String command = "create " + args;
        String expectedResponse = "Create command executed without arguments";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.create(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testUpdate() {
        String args = "id=123,name=UpdatedParcel";
        String command = "update " + args;
        String expectedResponse = "Parcel updated successfully";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.update(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testDelete() {
        String args = "id=123";
        String command = "delete " + args;
        String expectedResponse = "Parcel deleted successfully";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.delete(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testList() {
        String command = "list";
        String expectedResponse = "Listing all parcels";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.list();
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testLoad() {
        String args = "file=parcels.json";
        String command = "load " + args;
        String expectedResponse = "Data loaded successfully";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.load(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testUnload() {
        String args = "destination=export.json";
        String command = "unload " + args;
        String expectedResponse = "Data unloaded successfully";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.unload(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }

    @Test
    void testBilling() {
        String args = "month=September";
        String command = "billing " + args;
        String expectedResponse = "Billing report generated";
        when(commandHandler.handleCommand(command)).thenReturn(expectedResponse);
        String actualResponse = consoleController.billing(args);
        assertThat(actualResponse).isEqualTo(expectedResponse);
        verify(commandHandler, times(1)).handleCommand(command);
    }
}
