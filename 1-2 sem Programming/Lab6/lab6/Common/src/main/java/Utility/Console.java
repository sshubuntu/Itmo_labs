package Utility;

/**
 * Интерфейс для контроля консольных методов
 * @author sh_ub
 */
public interface Console {
    /**
     * Вывод данных в поток вывода
     * @param obj объект вывода
     */
    void print(Object obj);

    /**
     *  Вывод данных в поток вывода + ln
     * @param obj объект вывода
     */
    void println(Object obj);

    /**
     * Чтение данных
     */
    String readln();

    /**
     * Вывод данных в поток ошибок + ln
     * @param obj объект вывода
     */
    void printError(Object obj);

    /**
     * Специальный символ коммандной строки
     */
    void prompt();
    String getPrompt();
}
