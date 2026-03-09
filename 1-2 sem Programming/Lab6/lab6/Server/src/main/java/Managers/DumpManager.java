package Managers;

import Utility.StandardConsole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Route;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;
/**
 * Класс для записи и чтения данных из файла
 * @author sh_ub
 */
public class DumpManager {
    private final String fileName;
    private final StandardConsole console;
    private final File file ;


    public DumpManager(String fileName, StandardConsole console) {
        this.fileName = fileName;
        this.console = console;
        file = new File(fileName);
    }
    /**
     * Записывает данные в файл
     * @param collection коллекция
     */
    public void dump(Vector<Route> collection) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.setDateFormat(new StdDateFormat().withColonInTimeZone(true));
            mapper.writeValue(file, collection);

        } catch (IOException e) {
            console.printError("File " + fileName + " could not be opened or written.");
        }
    }
    /**
     * Считывает данные из файла
     * @return коллекцию
     */
    public Vector<Route> read() {
        if (!fileName.isEmpty()){
            ObjectMapper mapper = new ObjectMapper();
            try {
                Path path = Path.of(fileName);
                String content = Files.readString(path).trim();

                if (content.isEmpty())
                    Files.write(path, "[]".getBytes());

                mapper.registerModule(new JavaTimeModule());
                return mapper.readValue(
                        new File(fileName),
                        mapper.getTypeFactory().constructCollectionType(Vector.class, Route.class)
                );
            } catch (FileNotFoundException e) {
                console.printError("File " + fileName + " could not be opened.");
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        } else
            console.printError("file is not found");

        return new Vector<>();
    }
}
