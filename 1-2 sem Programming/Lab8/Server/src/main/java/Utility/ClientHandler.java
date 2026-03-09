package Utility;

import Authentication.User;
import Managers.CommandManager;
import Managers.ConnectionManager;
import Command.CommandWithArgs;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import static Managers.AuthorizationManager.login;
import static Managers.ConnectionManager.logger;
import static Managers.AuthorizationManager.register;

public class ClientHandler implements Runnable {
    private final CommandManager commandManager;
    private final ConnectionManager connectionManager;
    private final Socket socket;
    private final ForkJoinPool forkJoinPool;
    private final ExecutorService sendPool;

    public ClientHandler(CommandManager commandManager, ConnectionManager connectionManager, Socket socket, ForkJoinPool forkJoinPool, ExecutorService sendPool) {
        this.commandManager = commandManager;
        this.connectionManager = connectionManager;
        this.socket = socket;
        this.forkJoinPool = forkJoinPool;
        this.sendPool = sendPool;
    }

    @Override
    public void run() {
        connectionManager.start(socket);
        try {
            while (true) {
                byte[] data = connectionManager.receive();
                try {
                    if (data == null) {
                        logger.info("Client exits from socket {}", socket.getRemoteSocketAddress());
                        break;
                    }
                    CommandWithArgs commandWithArgs = connectionManager.deserialize(data, CommandWithArgs.class);

                    String commandName = commandWithArgs.getCommand().toString().toLowerCase();

                    Command command = commandManager.getCommands().get(commandName);

                    if (commandManager.getCommands().containsKey(commandName)) {

                        ForkJoinTask<ExecutionResponse> task = new CommandExecutionTask(command, commandWithArgs);

                        try {
                            ExecutionResponse response = forkJoinPool.invoke(task);
                            commandManager.addToHistory(commandName);

                            sendPool.submit(() -> {
                                byte[] serializedResponse = connectionManager.serializeObject(response);
                                connectionManager.send(serializedResponse);
                            });
                        } catch (NullPointerException e) {
                            logger.error("Could not connect to database!");
                            System.exit(1);
                        }
                    }

                } catch (Exception e) {
                    User user = connectionManager.deserialize(data, User.class);
                    ExecutionResponse answer = user.getType().equals("registration") ? register(user) : login(user);
                    connectionManager.send(connectionManager.serializeObject(answer));
                }


            }
        } finally {
            connectionManager.close();
            try {
                socket.close();
                logger.info("Socket {} was closed.", socket.getRemoteSocketAddress());
            } catch (IOException e) {
                logger.error("Ошибка при закрытии сокета: {}", e.getMessage(), e);
            }
        }
    }
}