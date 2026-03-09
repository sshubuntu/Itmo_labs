package Commands;

import Managers.CollectionManager;
import Utility.Command;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;

/**
 * Команда show: выводит все элементы коллекции в строковом представлении.
 * @author sh_ub
 */
public class Show extends Command {

    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        if (command.getArgs() != null)
            return new ExecutionResponse("Illegal number of arguments!", false);

        return new ExecutionResponse(collectionManager.getCollection(), true);
    }
}
