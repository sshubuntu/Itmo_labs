package Utility;

import java.util.concurrent.RecursiveTask;
import Command.CommandWithArgs;

public class CommandExecutionTask extends RecursiveTask<ExecutionResponse> {
    private final Command commandName;
    private final CommandWithArgs command;

    public CommandExecutionTask(Command commandName, CommandWithArgs command) {
        this.commandName = commandName;
        this.command = command;
    }

    @Override
    protected ExecutionResponse compute() {
        return commandName.execute(command);
    }
}
