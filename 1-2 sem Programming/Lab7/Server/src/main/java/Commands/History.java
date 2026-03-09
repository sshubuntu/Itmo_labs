package Commands;

import Utility.Command;
import Utility.StandardConsole;
import Managers.CommandManager;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Команда history: выводит последние 7 команд (без их аргументов).
 * @author sh_ub
 */
public class History extends Command {
    CommandManager commandmanager;
    StandardConsole console;

    public History(StandardConsole console, CommandManager commandmanager) {
        super("history", "вывести последние 7 команд (без их аргументов)");
        this.console = console;
        this.commandmanager = commandmanager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {

        if (command.getArgs() != null) {
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        var list = commandmanager.getCommandHistory();
        return new ExecutionResponse(GettingHistory(list), true);
    }

    /**
     * Получение истории запросов
     * @param history список запросов
     * @return последние 7 комманд
     */
    public String GettingHistory(ArrayList<String> history) {
        return history.stream()
                .skip(Math.max(0, history.size() - 7))  // Take last 7 (or less if not enough)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.reverse(list);  // Reverse to show newest first
                            return String.join("\n", list);
                        }
                ));
    }
}
