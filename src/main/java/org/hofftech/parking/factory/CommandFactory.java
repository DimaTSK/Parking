package org.hofftech.parking.factory;
import lombok.RequiredArgsConstructor;
import org.hofftech.parking.model.enums.CommandType;
import org.hofftech.parking.repository.ParcelRepository;
import org.hofftech.parking.util.FileProcessingUtil;
import org.hofftech.parking.service.FileSavingService;
import org.hofftech.parking.service.json.JsonProcessingService;
import org.hofftech.parking.service.OrderManagerService;
import org.hofftech.parking.validator.ParcelValidator;
import org.hofftech.parking.service.command.UserCommand;
import org.hofftech.parking.service.command.impl.BillingUserCommand;
import org.hofftech.parking.service.command.impl.CreateUserCommand;
import org.hofftech.parking.service.command.impl.DeleteUserCommand;
import org.hofftech.parking.service.command.impl.ExitUserCommand;
import org.hofftech.parking.service.command.impl.FindUserCommand;
import org.hofftech.parking.service.command.impl.CreateParcelCommand;
import org.hofftech.parking.service.command.impl.LoadUserCommand;
import org.hofftech.parking.service.command.impl.StartUserCommand;
import org.hofftech.parking.service.command.impl.UnloadUserCommand;
import org.hofftech.parking.service.command.impl.UpdateUserCommand;

/**
 * Фабрика для создания процессоров пользовательских команд на основе типа команды.
 * <p>
 * Использует различные зависимости для инициализации конкретных реализаций {@link UserCommand}.
 * </p>
 */
@RequiredArgsConstructor
public class CommandFactory {
    private final ParcelRepository repository;
    private final FileProcessingUtil fileProcessingUtil;
    private final JsonProcessingService jsonProcessingService;
    private final FileSavingService fileSavingService;
    private final ParcelValidator parcelValidator;
    private final OrderManagerService orderManagerService;

    /**
     * Создает процессор команды на основе указанного типа команды.
     *
     * @param commandType тип команды, для которой нужно создать процессор
     * @return экземпляр {@link UserCommand}, соответствующий заданному типу команды
     */
    public UserCommand createProcessor(CommandType commandType) {
        return switch (commandType) {
            case CREATE -> new CreateUserCommand(repository, parcelValidator);
            case FIND -> new FindUserCommand(repository);
            case UPDATE -> new UpdateUserCommand(repository, parcelValidator);
            case DELETE -> new DeleteUserCommand(repository);
            case LIST -> new CreateParcelCommand(repository);
            case LOAD -> new LoadUserCommand(fileProcessingUtil);
            case UNLOAD -> new UnloadUserCommand(jsonProcessingService, fileSavingService);
            case BILLING -> new BillingUserCommand(orderManagerService);
            case START -> new StartUserCommand();
            case EXIT -> new ExitUserCommand();
        };
    }
}