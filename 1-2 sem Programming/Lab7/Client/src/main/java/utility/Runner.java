package utility;

import Authentication.User;
import Command.CommandType;
import Command.CommandWithArgs;

import Connection.ConnectionClient;

import Utility.AskBreak;
import Utility.ExecutionResponse;
import Utility.StandardConsole;


import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


import static model.Ask.AskRoute;


public class Runner {
    private final StandardConsole console;
    private final List<String> scriptStack = new ArrayList<>();
    private int lengthRecursion = -1;
    private final ConnectionClient connection = new ConnectionClient(1234, "localhost");
    private User user;

    public Runner(StandardConsole console) {
        this.console = console;
    }

    private void checkConnection() throws InterruptedException {
        final int MAX_ATTEMPTS = 5;
        final int RETRY_DELAY_MS = 5000;

        ExecutionResponse response;
        boolean connected = false;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            response = connection.start();

            if (response.isSuccess()) {
                connected = true;
                break;
            }

            console.println(String.format("Connection attempt %d/%d failed: %s", attempt, MAX_ATTEMPTS, response.getResponse()));

            if (attempt < MAX_ATTEMPTS) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }

        if (!connected) {
            console.printError("Connection failed after " + MAX_ATTEMPTS +
                    " attempts. Please try again later.");
            System.exit(1);
        }
    }
    /**
     * Режим чтения команд из консоли
     */
    public void interactiveMode() throws AskBreak, ClassNotFoundException, InterruptedException, IOException {
        try {
            AskAuth ask = new AskAuth(console);

            do {
                do {
                    user = ask.askCredentials();
                } while (user== null);
                checkConnection();
                connection.send(connection.serializeObject(user));
                ExecutionResponse response = connection.deserializeObject(connection.receive());
                console.println(response.getResponse());
                if (response.isSuccess()) {
                    break;
                }
            }while (true);

            ExecutionResponse commandStatus;
            String[] userCommand;
            console.prompt();
            do {
                userCommand = (console.readln().trim().toLowerCase() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                checkConnection();
                commandStatus = launchCommand(userCommand);
                String answer = commandStatus.getResponse();

                if(commandStatus.isSuccess())
                    console.print(answer + (answer.isEmpty() ? "" : "\n"));
                else
                    console.printError(answer);

                console.prompt();
            } while (commandStatus.isSuccess() || !commandStatus.getResponse().equals("exit"));

        } catch (NoSuchElementException exception) {
            console.printError("Input is not defined");
        } catch (IllegalStateException exception) {
            console.printError("Error");
        }
    }

    /**
     * Метод для выбора команды
     * @param userCommand команда пользователя
     * @return ExecutionResponse выбора
     */
    private ExecutionResponse launchCommand(String[] userCommand) throws AskBreak, ClassNotFoundException, InterruptedException {
        try {
            CommandType command = CommandType.valueOf(userCommand[0].toUpperCase());
            CommandWithArgs commandWithArgs = null;
            if (command == CommandType.EXECUTE_SCRIPT)
                ScriptMode(userCommand[1]);
            else if (command == CommandType.ADD || command == CommandType.UPDATE)
                commandWithArgs = new CommandWithArgs(command, AskRoute(console, userCommand[1].trim(), command), user);
            else{
                commandWithArgs = new CommandWithArgs(command, userCommand[1].isEmpty() ? null : userCommand[1].trim(), user);
            }

            connection.send(connection.serializeObject(commandWithArgs));
            return connection.deserializeObject(connection.receive());

        }
        catch (IllegalArgumentException e) {
            return new ExecutionResponse("Command '" + userCommand[0] + "' not found. Use 'help' for more information.", false);
        }catch (IOException e){
            checkConnection();
        }

        return new ExecutionResponse("", true);
    }

    /**
     * Режим чтения команд из файла
     *
     * @param fileName имя файла
     */
    private void ScriptMode(String fileName) throws AskBreak, ClassNotFoundException, InterruptedException {
        String[] userCommand;
        ExecutionResponse commandStatus;
        scriptStack.add(fileName);
        if (!new File(fileName).exists()) {
            return;
        }
        try (FileReader filereader = new FileReader(fileName); BufferedReader bufferedReader = new BufferedReader(filereader)) {
            String line;
            do{
                line = bufferedReader.readLine();
                userCommand = (line.trim()+ " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                boolean needLaunch = true;
                if (userCommand[0].equals("execute_script")) {
                    int recStart = -1;
                    int i = 0;
                    for (String script : scriptStack) {
                        i++;
                        if (userCommand[1].equals(script)) {
                            if (recStart < 0) recStart = i;
                            if (lengthRecursion < 0) {
                                console.println("Была замечена рекурсия! Введите максимальную глубину рекурсии (0..500)");
                                while (lengthRecursion < 0 || lengthRecursion > 500) {
                                    try { console.print("> "); lengthRecursion = Integer.parseInt(console.readln().trim()); } catch (NumberFormatException e) { console.println("длина не распознан"); }
                                }
                            }
                            if (i > recStart + lengthRecursion || i > 500)
                                needLaunch = false;
                        }
                    }
                }

                commandStatus = needLaunch ?  launchCommand(userCommand) : new ExecutionResponse("Recursion depth exceeded!", false);

                String answer = commandStatus.getResponse();

                if(commandStatus.isSuccess())
                    console.print(answer + (answer.isEmpty() ? "" : "\n"));
                else
                    console.printError(answer);
            }
            while (commandStatus.isSuccess() || !commandStatus.getResponse().equals("exit"));

        }
        catch (FileNotFoundException exception) {
            console.printError("File is not found");
        }catch (NoSuchElementException exception) {
            console.printError("File is empty");
        }catch (IllegalStateException exception) {
            console.printError("Error");
        } catch (IOException e) {
            console.printError("Error while reading a file");
        } finally {
            scriptStack.remove(scriptStack.size()-1);
        }
    }
}