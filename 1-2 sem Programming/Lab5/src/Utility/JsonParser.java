package Utility;

import model.Coordinates;
import model.Location;
import model.Route;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Класс для дессереализации данных из json
 * @author sh_ub
 */
public class JsonParser {
    public static Vector<Route> parseJson(String json) {
        Vector<Route> routes = new Vector<>();
        json = json.trim().replace("\n", "").replace("\r", "").replace(" ", "");
        json = json.substring(1, json.length() - 1);
        String[] routeStrings = json.split("\\},\\s*\\{");

        for (String routeString : routeStrings) {
            if (!routeString.startsWith("{"))
                routeString = "{" + routeString;
            if (!routeString.endsWith("}"))
                routeString = routeString + "}";

            Route route = parseRoute(routeString);
            routes.add(route);
        }
        return routes;
    }

    private static Route parseRoute(String routeString) {
        String regex = "\\{\"id\":(.+),\"name\":\"(.+)\",\"coordinates\":(\\{.+}),\"creationDate\":(\\{.+}),\"from\":(\\{.+}),\"to\":(\\{.+}),\"distance\":(.+)}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(routeString);

        if (matcher.find()) {
            String id = matcher.group(1);// Группа 1: id
            String name = matcher.group(2); // Группа 2: name
            String coordinates = matcher.group(3); // Группа 3: coordinates
            String creationDate = matcher.group(4); // Группа 4: creationDate
            String from = matcher.group(5); // Группа 5: from
            String to = matcher.group(6); // Группа 6: to
            String distance = matcher.group(7); // Группа 7: distance
            return new Route(Long.parseLong(id), name, parseCoordinates(coordinates), parseCreationDate(creationDate), parseLocation(from), parseLocation(to), Integer.parseInt(distance));
        } else {
            return null;
        }
    }

    private static Coordinates parseCoordinates(String coordinatesString) {
        Pattern pattern = Pattern.compile("\\{\"x\":(.+),\"y\":(.+)}");
        Matcher matcher = pattern.matcher(coordinatesString);

        if (matcher.find())
            return new Coordinates(Double.parseDouble(matcher.group(1)), Float.parseFloat(matcher.group(2)));

        return null;
    }

    private static ZonedDateTime parseCreationDate(String creationDateString) {
        Pattern pattern = Pattern.compile("\\{\"year\":(.+),\\s*\"month\":(.+),\\s*\"day\":(.+),\\s*\"hour\":(.+),\\s*\"minute\":(.+)}");
        Matcher matcher = pattern.matcher(creationDateString);
        if (matcher.find()){
            LocalDateTime localDateTime = LocalDateTime.of(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)));
            
            ZoneId zoneId = ZoneId.systemDefault();
            return ZonedDateTime.of(localDateTime, zoneId);
        }
        return null;
    }

    private static Location parseLocation(String locationString) {
        Pattern pattern = Pattern.compile("\\{\"LocationName\":(.+),\\s*\"x\":(.+),\\s*\"y\":(.+),\\s*\"z\":(.+)}");
        Matcher matcher = pattern.matcher(locationString);
        if (matcher.find())
            return new Location(Long.parseLong(matcher.group(2)), Long.parseLong(matcher.group(3)), Double.parseDouble(matcher.group(4)), matcher.group(1));

        return null;
    }
}
