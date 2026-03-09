package Commands;

import Managers.CollectionManager;
import Managers.DbRoutesManager;
import Utility.ExecutionResponse;
import Utility.Command;
import model.Route;
import Command.CommandWithArgs;


/**
 * Команда add {element}: добавляет новый элемент в коллекцию.
 * {element} - Элемент, который нужно добавить в коллекцию.
 * @author sh_ub
 */
public class Add extends Command{
    private final CollectionManager collectionManager;
    private final DbRoutesManager dbRoutesManager;
    public Add(CollectionManager collectionManager, DbRoutesManager dbRoutesManager)   {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
        this.dbRoutesManager = dbRoutesManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        Route a = command.getRoute();
        try {
            if (a.validate()){
                Long id = dbRoutesManager.addRoute(a);
                a.setId(id);
                collectionManager.add(a);
                dbRoutesManager.setOwner(command.getUser().getLogin(), id);
                return new ExecutionResponse("Object Route is successfully added!!", true);
            }
            else return new ExecutionResponse("Object Route is not valid.", false);

        } catch (NullPointerException e){
            e.printStackTrace();
            return new ExecutionResponse("Failed to add route.", false);
        }
    }
}
