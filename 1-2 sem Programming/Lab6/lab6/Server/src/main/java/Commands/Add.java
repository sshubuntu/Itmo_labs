package Commands;

import Managers.CollectionManager;
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
    private CollectionManager collectionManager;

    public Add(CollectionManager collectionManager)   {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    public ExecutionResponse execute(CommandWithArgs command) {
        Route a = command.getRoute();
        try{
            if (a.validate()){
                a.setId(collectionManager.getCurrentId());
                collectionManager.add(a);
                return new ExecutionResponse("Object Route is successfully added!!", true);
            }
            else return new ExecutionResponse("Object Route is not valid.", false);

        } catch (NullPointerException e){
            return new ExecutionResponse("Illegal arguments!", false);
        }
    }
}
