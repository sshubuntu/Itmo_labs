package Commands;

import Managers.CollectionManager;
import Utility.ExecutionResponse;

/**
 * Команда clear: очищает коллекцию.
 * @author sh_ub
 */
public class Clear extends Command {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        if (!arg.isEmpty()){
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        collectionManager.clear();
        return new ExecutionResponse("Collection successfully cleared!", true);
    }
}
