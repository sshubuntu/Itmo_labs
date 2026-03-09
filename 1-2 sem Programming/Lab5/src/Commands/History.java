package Commands;

import Client.StandardConsole;
import Managers.CommandManager;
import Utility.ExecutionResponse;

import java.util.ArrayList;
/**
 * Команда history: выводит последние 7 команд (без их аргументов).
 * @author sh_ub
 */
public class History extends Command {
    CommandManager commandmanager;
    StandardConsole console;

    public History(CommandManager commandmanager, StandardConsole console) {
        super("history", "вывести последние 7 команд (без их аргументов)");
        this.commandmanager = commandmanager;
        this.console = console;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        if (!arg.trim().isEmpty()) {
            return new ExecutionResponse("Illegal number of arguments!", false);
        }
        var list = commandmanager.getCommandHistory();

        return new ExecutionResponse(GettingHistory(list), true);
    }

    /**
     * Получение истории запросов
     * @param list список запросов
     * @return последние 7 комманд
     */
    public String GettingHistory(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        int firstIndex = list.size()>=8? list.size()-8 : 0 ;
        int lastIndex = list.size()-1;
        for (; firstIndex < lastIndex; lastIndex--) {
            sb.append(list.get(lastIndex-1)).append(lastIndex-firstIndex==1 ? "" : "\n");
        }
        return sb.toString();
    }
}
