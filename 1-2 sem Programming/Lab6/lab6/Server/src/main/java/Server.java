import Commands.Add;
import Commands.*;
import Managers.CollectionManager;
import Managers.CommandManager;
import Managers.DumpManager;
import Utility.StandardConsole;

import static Utility.ConnectionStarter.run;
import static Utility.Path.getJsonPath;

class Server {
    public static void main(String[] args) {
        StandardConsole console = new StandardConsole();
        DumpManager dumpManager = new DumpManager(getJsonPath(), console);
        final CollectionManager collectionManager = new CollectionManager(dumpManager);
        collectionManager.init();
        var commandManager = new CommandManager() {{
            register("help", new Help(console, this));
            register("history", new History(console, this));
            register("add", new Add(collectionManager));
            register("info", new Info(collectionManager));
            register("show", new Show(collectionManager));
            register("remove_by_id", new RemoveById(console, collectionManager));
            register("clear", new Clear(collectionManager));
            register("remove_first", new RemoveFirst(collectionManager));
            register("sort", new Sort(collectionManager));
            register("max_by_name", new MaxByName(collectionManager));
            register("filter_contains_name", new FilterContainsName(collectionManager));
            register("print_field_ascending_distance", new PrintFieldAscendingDistance(collectionManager));
            register("update", new UpdateId(collectionManager));
            register("exit", new Exit(collectionManager));
        }};
        run(1234, commandManager, console);
    }
}
