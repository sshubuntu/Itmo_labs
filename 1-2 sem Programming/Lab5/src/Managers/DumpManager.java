package Managers;

import Client.StandardConsole;
import model.Route;

import java.io.*;

import java.util.Vector;

import static Utility.JsonParser.parseJson;
import static Utility.JsonSerialization.ToJson;
/**
 * Класс для записи и чтения данных из файла
 * @author sh_ub
 */
public class DumpManager {
    private final String fileName;
    private final StandardConsole console;

    public DumpManager(String fileName, StandardConsole console) {
        this.fileName = fileName;
        this.console = console;
    }
    /**
     * Записывает данные в файл
     * @param collection коллекция
     */
    public void dump(Vector<Route> collection) {
        try (OutputStream outputStream = new FileOutputStream(fileName, false);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream)) {

            outputStreamWriter.write(ToJson(collection)); // Записываем данные в файл
            outputStreamWriter.flush(); // Обеспечиваем, что данные записаны

        } catch (IOException e) {
            console.printError("File " + fileName + " could not be opened or written.");
        }
    }
    /**
     * Считывает данные из файла
     * @return коллекцию
     */
    public Vector<Route> read() {
        if (fileName != null && !fileName.isEmpty()){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileName));

                var jsonString = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty())
                        jsonString.append(line);
                }
                if (!jsonString.toString().trim().isEmpty()) {
                    if (jsonString.substring(1, jsonString.length() - 1).trim().isEmpty()) {
                        return new Vector<>();
                    }
                    return parseJson(jsonString.toString());
                }
                return new Vector<>();

            } catch (FileNotFoundException e) {
                console.printError("File " + fileName + " could not be opened.");
            } catch (IOException e) {
                console.printError("Error in reading file " + fileName + ".");
                System.exit(0);
            }
        } else
            console.printError("file is not found");

        return new Vector<>();
    }
}
