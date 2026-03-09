package Commands;

import Client.StandardConsole;
import Managers.CollectionManager;
import Utility.AskBreak;
import Utility.ExecutionResponse;
import model.Route;

import java.util.Objects;

import static Utility.ParseFileRoute.ParseRoute;
import static model.Ask.AskRoute;

/**
 * Команда add {element}: добавляет новый элемент в коллекцию.
 * {element} - Элемент, который нужно добавить в коллекцию.
 * @author sh_ub
 */
public class Add extends Command{
    private StandardConsole console;
    private CollectionManager collectionManager;

    public Add(StandardConsole console, CollectionManager collectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        Route a;
        try{
            if(!arg.isEmpty() && !(arg.trim().charAt(0) == '{'  ))
                return new ExecutionResponse("Illegal number of arguments!", false);
            else if (arg.isEmpty())
                a = AskRoute(console, collectionManager.getCurrentId());
            else
                a = ParseRoute(console, arg, collectionManager.getCurrentId());

            if (Objects.requireNonNull(a).validate()){
                collectionManager.add(a);
                return new ExecutionResponse("Object Route is successfully added!!", true);
            }
            else
                return new ExecutionResponse("Object Route is not valid.", false);

        } catch (AskBreak e){
            return new ExecutionResponse("Interrupting", false);
        } catch (NullPointerException e){
            return new ExecutionResponse("Illegal arguments!", false);
        }
    }
}
