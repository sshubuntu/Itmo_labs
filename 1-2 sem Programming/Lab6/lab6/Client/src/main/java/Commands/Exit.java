package Commands;

import Utility.Command;
import Utility.ExecutionResponse;
import Utility.StandardConsole;
import Command.CommandWithArgs;
/**
 * Команда save: сохраняет коллекцию в файл.
 * @author sh_ub
 */
public class Exit extends Command {
    StandardConsole console;
    public Exit(StandardConsole console) {
        super("exit", "выйти из приложения");
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        if (command.getArgs() != null){
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        console.println("Вы вышли из приложения.");
        System.exit(0);
        return new ExecutionResponse("Collection successfully saved!", true);
    }
}
