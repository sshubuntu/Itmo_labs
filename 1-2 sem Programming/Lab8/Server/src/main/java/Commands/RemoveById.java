package Commands;

import Managers.DbRoutesManager;
import Utility.Command;
import Utility.StandardConsole;
import Managers.CollectionManager;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;

/**
 * Команда remove_by_id id: удаляет элемент из коллекции по его id.
 * @author sh_ub
 */
public class RemoveById extends Command {
    private final CollectionManager collectionManager;
    private final DbRoutesManager dbRoutesManager;

    public RemoveById(CollectionManager collectionManager, DbRoutesManager dbRoutesManager) {
        super("remove_by_id id ", "удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
        this.dbRoutesManager = dbRoutesManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        try {
            String[] args = command.getArgs().split(" ");
            if (args.length < 1)
                return new ExecutionResponse("Illegal number of arguments!", false);

            for (String s : args) {
                Long id = Long.parseLong(s);

                if (!dbRoutesManager.getOwner(id).equals(command.getUser().getLogin()))
                    return new ExecutionResponse("Permission denied!", false);

                if (!collectionManager.remove(id))
                    return new ExecutionResponse("The route with this id was not found.", false);
            }
            return new ExecutionResponse("Routes with id " + command.getArgs() + " was deleted", true);
        } catch (NumberFormatException  e){
            return new ExecutionResponse( "Id isn't defined!", false);
        }
    }
}
