package Managers;

import Utility.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Класс для управления командами
 * @author sh_ub
 */
public class CommandManager {

    private static final HashMap<String, Command> commands = new LinkedHashMap<>();
    private static final ArrayList<String> commandHistory = new ArrayList<>();

    /**
     * Метод для регистрации команды
     * @param commandName имя команды
     * @param command объект класса Command
     */
    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public HashMap<String, Command> getCommands() {
        return commands;
    }

    /**
     * Метод для сохранения команды в истории
     * @param command объект класса Command
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
    }

    public ArrayList<String> getCommandHistory() {
        return commandHistory;
    }
}
