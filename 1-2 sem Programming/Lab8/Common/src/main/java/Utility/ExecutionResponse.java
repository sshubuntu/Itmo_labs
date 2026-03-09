package Utility;

import model.Route;

import java.io.Serializable;
import java.util.List;

/**
 * Класс для хранения результата выполнения
 * @author sh_ub
 */
public class ExecutionResponse implements Serializable {

    private String response; // сообщение о выполнение
    private boolean success;
    private List<Route> routes;// флаг выполнения

    public ExecutionResponse(String response, boolean success) {
        this.response = response;
        this.success = success;
    }

    public ExecutionResponse(List<Route> routes, boolean success) {
        this.routes = routes;
        this.success = success;
    }

    public String getResponse() {
        return response;
    }
    public List<Route> getRoutes() {
        return routes;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return String.format("ExecutionResponse [response=%s, success=%s]", response, success);
    }

}
