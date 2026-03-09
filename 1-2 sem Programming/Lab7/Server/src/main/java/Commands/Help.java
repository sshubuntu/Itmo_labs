package Commands;

import Utility.Command;
import Utility.StandardConsole;
import Managers.CommandManager;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;
import java.util.stream.Collectors;

/**
 * Команда help: выводит справку по доступным командам.
 * @author sh_ub
 */
public class Help extends Command {

    private final StandardConsole console;
    private final CommandManager commandmanager;

    public Help(StandardConsole console, CommandManager commandmanager) {
        super("help ", "вывести справку по доступным командам");
        this.console = console;
        this.commandmanager = commandmanager ;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs CommandArgs) {

        if (CommandArgs.getArgs()!= null)
            return new ExecutionResponse("Illegal number of arguments!",false);
        return new ExecutionResponse(commandmanager.getCommands().values().stream().map(command -> console.printTable(command.getName(), command.getDescription())).collect(Collectors.joining("\n")), true);
    }
}

