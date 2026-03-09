package Commands;

import Utility.Command;
import Utility.StandardConsole;
import Managers.CollectionManager;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;

/**
 * Команда sort: сортирует коллекцию в естественном порядке.
 * @author sh_ub
 */
public class Sort extends Command {

    private final CollectionManager collectionManager;

    public Sort(CollectionManager collectionManager) {
        super("sort", "отсортировать коллекцию в естественном порядке");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        if(command.getArgs()!= null)
            return new ExecutionResponse("Illegal number of arguments!", false);

        collectionManager.sort();
        return new ExecutionResponse("Successfully sorted collection", true);
    }
}
