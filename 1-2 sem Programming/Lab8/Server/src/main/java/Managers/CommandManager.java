package Managers;

import Utility.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * A class for managing commands.
 *
 * @author sh_ub
 */
public class CommandManager {

    private static final HashMap<String, Command> commands = new LinkedHashMap<>();
    private static final ArrayList<String> commandHistory = new ArrayList<>();

    /**
     * Registers a command with the specified name.
     *
     * @param commandName the name of the command
     * @param command the Command object to be registered
     */
    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    /**
     * Retrieves the map of registered commands.
     *
     * @return a HashMap containing command names as keys and Command objects as values
     */
    public HashMap<String, Command> getCommands() {
        return commands;
    }

    /**
     * Adds a command to the command history.
     *
     * @param command the name of the command to be added to the history
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
    }

    /**
     * Retrieves the list of commands in the history.
     *
     * @return an ArrayList containing the history of command names
     */
    public ArrayList<String> getCommandHistory() {
        return commandHistory;
    }
}