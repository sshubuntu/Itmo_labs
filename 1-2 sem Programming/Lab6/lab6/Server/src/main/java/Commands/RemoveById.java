package Commands;

import Utility.Command;
import Utility.StandardConsole;
import Managers.CollectionManager;
import Utility.ExecutionResponse;
import Command.CommandWithArgs;

/**
 * Команда remove_by_id id: удаляет элемент из коллекции по его id.
 * @author sh_ub
 */
public class RemoveById extends Command {
    private final StandardConsole console;
    private final CollectionManager collectionManager;

    public RemoveById(StandardConsole console, CollectionManager collectionManager) {
        super("remove_by_id id ", "удалить элемент из коллекции по его id");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse execute(CommandWithArgs command) {
        try{

            String[] args = command.getArgs().split(" ");
            if(args.length < 1){
                return new ExecutionResponse("Illegal number of arguments!", false);
            }
            for (String s : args) {
                Long id = Long.parseLong(s);
                if(!collectionManager.remove(id)){
                    return new ExecutionResponse("The route with this id was not found.", false);
                }
                console.println("Remove a Route by Id: " + s);
            }
            return new ExecutionResponse("Routes with id " + command.getArgs() + " was deleted", true);
        } catch (NumberFormatException  e){
            return new ExecutionResponse( "Id isn't defined!", false);
        }
    }
}
