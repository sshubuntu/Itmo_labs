package Commands;

import Managers.CollectionManager;
import Utility.ExecutionResponse;

/**
 * Команда save: сохраняет коллекцию в файл.
 * @author sh_ub
 */
public class Save extends Command {
    private final CollectionManager collectionManager;

    public Save(CollectionManager collectionManager) {
        super("save", "сохранить коллекцию в файл");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        if (!arg.isEmpty()){
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        collectionManager.saveCollection();
        return new ExecutionResponse("Collection successfully saved!", true);
    }
}
