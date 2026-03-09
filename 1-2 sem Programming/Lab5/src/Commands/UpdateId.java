package Commands;

import Client.StandardConsole;
import Managers.CollectionManager;
import Utility.AskBreak;
import Utility.ExecutionResponse;
import model.Route;

import static Utility.ParseFileRoute.ParseRoute;
import static model.Ask.AskRoute;

/**
 * Команда update id {element}: обновляет значение элемента коллекции, id которого равен заданному.
 * Id - Идентификатор элемента, который нужно обновить.
 * {element} - Новое значение элемента.
 * @author sh_ub
 */
public class UpdateId extends Command {
    private final StandardConsole console;
    private final CollectionManager collectionManager;

    public UpdateId(StandardConsole console, CollectionManager collectionManager) {
        super("update id {element}  ", "обновить значения элементов коллекции, id которого равны заданным");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(String arg) {
        String[] args = arg.split(" ");

        try{
            Long id = Long.parseLong(args[0].trim());
            Route a;
            if (!(args.length == 1 || (args.length == 2 && args[1].trim().charAt(0) == '{')))
                return new ExecutionResponse("Illegal number of arguments!", false);
            else if (args.length == 1)
                a = AskRoute(console, id);
            else {
                try {
                    a = ParseRoute(console, args[1], id);
                } catch (NullPointerException e) {
                    return new ExecutionResponse("Illegal argument!", false);
                }
            }

            if (!collectionManager.update(a))
                return new ExecutionResponse("The route with this id was not found.", false);

        } catch(AskBreak e){
            return new ExecutionResponse("Exit", false);
        }
        return new ExecutionResponse("Route was successfully updated", true);
    }
}


