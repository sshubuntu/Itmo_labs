package Commands;

import Managers.CollectionManager;
import Managers.DbRoutesManager;
import Utility.Command;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;

/**
 * Команда remove_first: удаляет первый элемент из коллекции.
 * @author sh_ub
 */
public class RemoveFirst extends Command {
    private final CollectionManager collectionManager;
    private final DbRoutesManager dbRoutesManager;

    public RemoveFirst(CollectionManager collectionManager , DbRoutesManager dbRoutesManager) {
        super("remove_first", "удалить первый элемент из коллекции");
        this.collectionManager = collectionManager;
        this.dbRoutesManager = dbRoutesManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        if(command.getArgs() != null)
            return new ExecutionResponse("Illegal number of arguments!", false);

        Long id = collectionManager.getCollection().get(0).getId();

        if (!dbRoutesManager.getOwner(id).equals(command.getUser().getLogin()))
            return new ExecutionResponse("Permission denied!", false);

        if(!collectionManager.remove(id))
            return new ExecutionResponse("Element with this Id is not found", false);

        return new ExecutionResponse("Element with id "+id+" was removed", true);
    }
}
