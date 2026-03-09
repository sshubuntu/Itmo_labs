package Utility;

import Managers.CommandManager;
import Managers.ConnectionManager;

import java.io.IOException;
import Command.CommandWithArgs;


public class ConnectionStarter {
    public static void run(int port, CommandManager commandManager, StandardConsole console) {
        try {
            ConnectionManager cm = new ConnectionManager(port);
            while (true) {
                byte[] data = cm.receive();
                CommandWithArgs command = cm.deserialize(data);
                ExecutionResponse response = commandManager.getCommands().get(command.getCommand().toString().toLowerCase()).execute(command);
                commandManager.addToHistory(command.getCommand().toString().toLowerCase());
                cm.send(cm.serializeObject(response));
                }
            }
        catch (IOException e) {
            console.println("Sending error");
        }
    }
}
