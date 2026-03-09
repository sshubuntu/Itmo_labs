package Utility;

/**
 * Управляет переменной окружения для парсинга history
 * @author sh_ub
 */
public class Path {

    /**
     * Возвращает путь к файлу в зависимости от нахождения программы на сервере или на локальном устройстве.
     * @return String путь к файлу
     */

    public static String getJsonPath() {
        String jsonPath = System.getenv("JSON_PATH");
        return jsonPath;
    }
}