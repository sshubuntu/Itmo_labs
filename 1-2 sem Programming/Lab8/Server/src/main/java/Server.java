import Commands.*;
import Managers.*;
import Utility.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import static Managers.ConnectionManager.createConnection;
import static Managers.ConnectionManager.logger;

class Server {
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

        DbManager dbManager = new DbManager() {{
            connect();
        }};
        StandardConsole console = new StandardConsole();

        final CollectionManager collectionManager = new CollectionManager(dbManager.getDbRoutesManager());
        collectionManager.init();
        var commandManager = new CommandManager() {{
            register("help", new Help(console, this));
            register("history", new History(console, this));
            register("add", new Add(collectionManager, dbManager.getDbRoutesManager()));
            register("info", new Info(collectionManager));
            register("show", new Show(collectionManager));
            register("remove_by_id", new RemoveById(collectionManager, dbManager.getDbRoutesManager()));
            register("clear", new Clear(collectionManager));
            register("remove_first", new RemoveFirst(collectionManager, dbManager.getDbRoutesManager()));
            register("sort", new Sort(collectionManager));
            register("max_by_name", new MaxByName(collectionManager));
            register("filter_contains_name", new FilterContainsName(collectionManager));
            register("print_field_ascending_distance", new PrintFieldAscendingDistance(collectionManager));
            register("update", new UpdateId(collectionManager, dbManager.getDbRoutesManager()));
            register("exit", new Exit(collectionManager));
            register("execute_script", new ExecuteScript());
        }};

        ServerSocket socket = createConnection(1234);

        if (socket == null) {
            console.printError("Не удалось создать серверный сокет. Завершение работы.");
            return;
        }

        ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        ExecutorService sendPool = Executors.newFixedThreadPool(10);

        try {
            while (true) {
                Socket clientSocket = socket.accept();
                new Thread(new ClientHandler(commandManager, new ConnectionManager(), clientSocket, forkJoinPool, sendPool)).start();
            }
        } catch (Exception e) {
            logger.error("Ошибка сервера: {}", e.getMessage(), e);
        } finally {
            forkJoinPool.shutdown();
            sendPool.shutdown();
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Ошибка при закрытии серверного сокета: {}", e.getMessage(), e);
            }
        }
    }
}