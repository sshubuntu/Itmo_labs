package Commands;

import Command.CommandWithArgs;
import Managers.CollectionManager;
import Managers.DbRoutesManager;
import Utility.ExecutionResponse;
import model.Route;
import Utility.Command;
/**
 * Команда update id {element}: обновляет значение элемента коллекции, id которого равен заданному.
 * Id - Идентификатор элемента, который нужно обновить.
 * {element} - Новое значение элемента.
 * @author sh_ub
 */
public class UpdateId extends Command {
    private final CollectionManager collectionManager;
    private final DbRoutesManager dbRoutesManager;

    public UpdateId(CollectionManager collectionManager, DbRoutesManager dbRoutesManager) {
        super("update id {element}", "обновить значения элементов коллекции, id которого равны заданным");
        this.collectionManager = collectionManager;
        this.dbRoutesManager = dbRoutesManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        Route a = command.getRoute();

        if (a == null)
            return new ExecutionResponse("Illegal arguments!", false);
        else if (!a.validate())
            return new ExecutionResponse("Route is not valid!", false);

        else if (!dbRoutesManager.getOwner(a.getId()).equals(command.getUser().getLogin()))
            return new ExecutionResponse("Permission denied!", false);

        else if (collectionManager.update(a))
            return new ExecutionResponse("Route was successfully updated", true);
        return new ExecutionResponse("The route with this id was not found.", false);
    }
}


