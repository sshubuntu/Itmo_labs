package Commands;


import Managers.CollectionManager;
import Utility.Command;
import Utility.ExecutionResponse;

import model.Route;
import Command.CommandWithArgs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Команда filter_contains_name name: выводит элементы, значение поля name которых содержит заданную подстроку.
 * Name Подстрока, которую нужно искать в поле name.
 * @author sh_ub
 */
public class FilterContainsName extends Command {
    private final CollectionManager collectionManager;

    public FilterContainsName(CollectionManager collectionManager) {
        super("filter_contains_name name", "вывести элементы, значение поля name которых содержит заданную подстроку");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        String substring = command.getArgs();
        if (substring.isEmpty())
            return new ExecutionResponse("Substring is empty", false);

        String matches = collectionManager.getCollection().stream()
                .filter(route -> route.getName().toLowerCase().contains(substring))
                .map(Route::toString)
                .collect(Collectors.joining("\n"));

        String result = matches.isEmpty()
                ? "0 matches found."
                : matches + "\n" + matches.split("\n").length + " matches found.";

        return new ExecutionResponse(result, true);
    }
}
