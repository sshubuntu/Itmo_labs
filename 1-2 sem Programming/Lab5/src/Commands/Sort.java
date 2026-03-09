package Commands;

import Client.StandardConsole;
import Managers.CollectionManager;
import Utility.ExecutionResponse;

/**
 * Команда sort: сортирует коллекцию в естественном порядке.
 * @author sh_ub
 */
public class Sort extends Command {

    private final CollectionManager collectionManager;

    public Sort(StandardConsole console, CollectionManager collectionManager) {
        super("sort", "отсортировать коллекцию в естественном порядке");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        if(!arg.isEmpty())
            return new ExecutionResponse("Illegal number of arguments!", false);

        collectionManager.sort();
        return new ExecutionResponse("Successfully sorted collection", true);
    }
}
