package Commands;

import Client.StandardConsole;
import Utility.ExecutionResponse;
/**
 * Команда execute_script file_name: считывает и исполняет скрипт из указанного файла.
 * В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
 *  fileName - Имя файла, содержащего скрипт.
 * @author sh_ub
 */
public class ExecuteScript extends Command {

    public ExecuteScript() {
        super("execute_script <file_name>", "исполнить скрипт из указанного файла");
    }

    @Override
    public ExecutionResponse execute(String arg) {
        if (arg.trim().isEmpty()) {
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        return new ExecutionResponse("Выполнение скрипта '" + arg + "'...", true);
    }
}
