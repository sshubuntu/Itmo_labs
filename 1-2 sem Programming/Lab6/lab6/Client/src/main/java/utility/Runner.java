package utility;

import Command.CommandType;
import Command.CommandWithArgs;

import Commands.Exit;
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
    private StandardConsole console;
    private  List<String> scriptStack = new ArrayList<>();
    private int lengthRecursion = -1;
    private ConnectionClient connection = new ConnectionClient(1234, "localhost");

    public Runner(StandardConsole console) {
        this.console = console;
    }

    /**
     * Режим чтения команд из консоли
     */
    public void interactiveMode() throws AskBreak {
        try {
            if (!connection.start()) System.exit(0);
            ExecutionResponse commandStatus;
            String[] userCommand;
            console.prompt();
            do {
                userCommand = (console.readln().trim().toLowerCase() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();

                commandStatus = launchCommand(userCommand);
                String answer = commandStatus.getResponse();

                if(commandStatus.isSuccess()) console.print(answer + (answer.isEmpty() ? "" : "\n"));
                else console.printError(answer);

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
    private ExecutionResponse launchCommand(String[] userCommand) throws AskBreak {
        try {
            CommandType command = CommandType.valueOf(userCommand[0].toUpperCase());
            CommandWithArgs commandWithArgs;
            if (command == CommandType.EXECUTE_SCRIPT) {
                ScriptMode(userCommand[1]);
            }
            else {
                if (command == CommandType.ADD || command == CommandType.UPDATE)
                    commandWithArgs = new CommandWithArgs(command, AskRoute(console, userCommand[1].trim(), command));

                else if (command == CommandType.FILTER_CONTAINS_NAME || command == CommandType.REMOVE_BY_ID)
                    commandWithArgs = new CommandWithArgs(command, userCommand[1].trim());
                else if (command == CommandType.EXIT) {
                    Exit ex = new Exit(console);
                    commandWithArgs = new CommandWithArgs(command);
                    connection.send(connection.serializeObject(commandWithArgs));
                    ex.execute(commandWithArgs);
                } else
                    commandWithArgs = new CommandWithArgs(command);
                connection.send(connection.serializeObject(commandWithArgs));

                ExecutionResponse commandStatus = connection.deserializeObject(connection.receive());
                return commandStatus;
            }
        }
        catch (IllegalArgumentException e) {
            return new ExecutionResponse("Command '" + userCommand[0] + "' not found. Use 'help' for more information.", false);
        }catch (IOException e){
            return new ExecutionResponse("Server is unavailable. Retry later." , false);
        }

        return new ExecutionResponse("", true);
    }

    /**
     * Режим чтения команд из файла
     * @param fileName имя файла
     * @return ExecutionResponse результат выполнения
     */
    private ExecutionResponse ScriptMode(String fileName) throws AskBreak {
        String[] userCommand;
        ExecutionResponse commandStatus;
        scriptStack.add(fileName);
        if (!new File(fileName).exists()) {
            return new ExecutionResponse("File is not exists",false);
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

        } catch (NullPointerException exception) {
            return new ExecutionResponse("---------------Script succesfully executed---------------", true);
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
        return new ExecutionResponse("", true);
    }
}