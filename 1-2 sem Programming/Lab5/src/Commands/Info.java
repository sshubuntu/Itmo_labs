package Commands;

import Client.StandardConsole;
import Managers.CollectionManager;
import Utility.ExecutionResponse;

import java.time.LocalDateTime;

/**
 * Команда info: выводит информацию о коллекции (тип, дата инициализации, количество элементов и т.д.).
 *
 * @author sh_ub
 */
public class Info extends Command {
    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        super("info", "Вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        if(!arg.isEmpty())
            return new ExecutionResponse("Illegal number of arguments!", false);

        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        LocalDateTime lastSaveTime = collectionManager.getLastSaveTime();

        var r = """
                Information about collection: Type, lastInitTime, lastSaveTime and count of elements:
                Type: %s,
                LastInitTime: %s
                LastSaveTime: %s
                CountOfElements: %d"""
                .formatted(collectionManager.getCollection().getClass(),
                lastInitTime==null ? "The collection has not been updated in this session yet.": lastInitTime,
                lastSaveTime==null ? "The collection has not been saved in this session yet.": lastSaveTime,
                collectionManager.getCollection().size());

        return new ExecutionResponse(r, true);
    }
}
