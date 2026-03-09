package Commands;

import Managers.CollectionManager;
import Utility.Command;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;
/**
 * Команда save: сохраняет коллекцию в файл.
 * @author sh_ub
 */
public class Exit extends Command {
    CollectionManager collectionManager;

    public Exit(CollectionManager collectionManager) {
        super("exit", "выйти из приложения");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        if (command.getArgs() != null) {
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        return new ExecutionResponse("Connection closed", true);
    }
}
