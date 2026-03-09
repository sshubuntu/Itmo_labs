package Commands;

import Managers.CollectionManager;
import Utility.Command;
import Utility.ExecutionResponse;
import model.Route;
import Command.CommandWithArgs;


import java.util.Vector;

/**
 * Команда max_by_name: выводит любой объект из коллекции, значение поля name которого является максимальным.
 * @author sh_ub
 */
public class MaxByName extends Command {
    private final CollectionManager collectionManager;

    public MaxByName(CollectionManager collectionManager) {
        super("max_by_name", "Вывести любой объект из коллекции, значение поля name которого является максимальным");
        this.collectionManager = collectionManager;
    }

    /**
     * Метод для получения объекта из коллекции, значение поля name которого является максимальным.
     * @param collection коллекция
     */
    public String GettingMaxName(Vector<Route> collection){
        return collection.stream()
                .map(Route::getName)
                .max(String::compareTo)
                .orElseThrow(() -> new IllegalStateException("Collection is Empty"));
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        if(command.getArgs() != null) {
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        return new ExecutionResponse(GettingMaxName(collectionManager.getCollection()), true);
    }


}
