package Commands;

import Managers.CollectionManager;
import Utility.ExecutionResponse;

/**
 * Команда remove_first: удаляет первый элемент из коллекции.
 * @author sh_ub
 */
public class RemoveFirst extends Command {
    private final CollectionManager collectionManager;

    public RemoveFirst(CollectionManager collectionManager) {
        super("remove_first", "удалить первый элемент из коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        if(!arg.isEmpty())
            return new ExecutionResponse("Illegal number of arguments!", false);

        var id = collectionManager.getCollection().get(0).getId();

        if(!collectionManager.remove(id))
            return new ExecutionResponse("Element with this Id is not found", false);

        return new ExecutionResponse("Element with id "+id+" was removed", true);
    }
}
