package Utility;

import model.Coordinates;
import model.Location;
import model.Route;

import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Vector;
/**
 * Класс для сереализации данных в json
 * @author sh_ub
 */
public class JsonSerialization {
    private static StringBuilder json = new StringBuilder("[\n");

    /**
     * Основной метод для парсинга коллекции в json строку
     * @param routes коллекция
     * @return json строка
     */
    public static String ToJson(Vector<Route> routes) {
        json = new StringBuilder("[\n");
        for (int i = 0; i < routes.size(); i++) {
            json.append(RouteToJson(routes.get(i)));
            if (i < routes.size() - 1) {
                json.append(",");
            }
        }
        json.append("\n]");
        return json.toString();
    }

    private static String RouteToJson(Route object){
        return """
            {
                "id": %d,
                "name": "%s",
                "coordinates": %s,
                "creationDate": %s,
                "from": %s,
                "to" : %s,
                "distance": %d
            }
            """.formatted(object.getId(),
                object.getName(),
                CoordinatesToJson(object.getCoordinates()),
                CreationDateToJson(object.getCreationDate()),
                LocationToJson(object.getFrom()),
                LocationToJson(object.getTo()),
                object.getDistance());

    }
    private static String CoordinatesToJson(Coordinates object) {
        return """
                {
                      "x": %s,
                      "y": %s
                 }""".formatted(String.format(Locale.US, "%.5f", object.getX()),
                String.format(Locale.US, "%.5f", object.getY()));
    }
    private static String CreationDateToJson(ZonedDateTime object) {
        return """
                {
                      "year": %d,
                      "month": %d,
                      "day": %d,
                      "hour": %d,
                      "minute": %d
                 }""".formatted(object.getYear(),
                object.getMonthValue(),
                object.getDayOfMonth(),
                object.getHour(),
                object.getMinute());
    }
    private static String LocationToJson(Location object) {
        return """
                {
                      "LocationName": "%s",
                      "x": %d,
                      "y": %d,
                      "z": %s
                 }""".formatted(object.getName(),
                object.getX(),
                object.getY(),
                String.format(Locale.US, "%.5f", object.getZ()));
    }


}
