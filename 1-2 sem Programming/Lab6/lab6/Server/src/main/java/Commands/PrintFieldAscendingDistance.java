package Commands;

import Managers.CollectionManager;
import Utility.Command;
import Utility.ExecutionResponse;
import model.Route;
import Command.CommandWithArgs;


import java.util.Vector;
import java.util.stream.Collectors;
/**
 * Команда print_field_ascending_distance: выводит значения поля distance всех элементов в порядке возрастания.
 * @author sh_ub
 */
public class PrintFieldAscendingDistance extends Command {
    private final CollectionManager collectionManager;

    public PrintFieldAscendingDistance(CollectionManager collectionManager) {
        super("print_field_ascending_distance", "вывести значения поля distance всех элементов в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        if(command.getArgs() != null)
            return new ExecutionResponse("Illegal number of arguments!", false);

        if(collectionManager.getCollection().isEmpty())
            return new ExecutionResponse("Collection is empty!", false);

        else return new ExecutionResponse(GettingDistances(collectionManager.getCollection()), true);
    }
    /**
     * Метод для получения списка дистанций
     * @param routes маршруты
     * @return String список дистанций
     * @author sh_ub
     */
    private String GettingDistances(Vector<Route> routes) {
        return routes.stream()
                .map(Route::getDistance)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
    }
}
