package Command;

import model.Route;

import java.io.Serializable;

public class CommandWithArgs implements Serializable {
    private  CommandType command;
    private String args;
    private Route route;

    public CommandWithArgs(CommandType command, String args) {
        this.command = command;
        this.args = args;
    }

    public CommandWithArgs(CommandType command) {
        this.command = command;
    }

    public CommandWithArgs(CommandType command, Route route) {
        this.command = command;
        this.route = route;
    }

    public CommandType getCommand() {
        return command;
    }

    public void setCommand(CommandType command) {
        this.command = command;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
    public Route getRoute() {
        return route;
    }
    public void setRoute(Route route) {
        this.route = route;
    }
}
