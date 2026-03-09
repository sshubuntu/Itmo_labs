package Utility;

import java.io.Serializable;

/**
 * Класс для хранения результата выполнения
 * @author sh_ub
 */
public class ExecutionResponse implements Serializable {

    private String response; // сообщение о выполнение
    private boolean success; // флаг выполнения

    public ExecutionResponse(String response, boolean success) {
        this.response = response;
        this.success = success;
    }

    public String getResponse() {
        return response;
    }

    public boolean isSuccess() {
        return success;
    }

    @Override
    public String toString() {
        return String.format("ExecutionResponse [response=%s, success=%s]", response, success);
    }
}
