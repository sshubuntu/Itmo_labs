package Utility;

import Client.StandardConsole;
import model.Coordinates;
import model.Location;
import model.Route;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Вспомогательный класс для парсинга аналогового ввода объектов
 * @author sh_ub
 */
public class ParseFileRoute {

    /**
     * Основной метод для парсинга
     * @param console консоль для вывода ошибок
     * @param line параметры элемента
     * @param id id элемента
     * @return Route объект класса Route
     * @throws AskBreak обработка выхода из программы
     */
    public static Route ParseRoute(StandardConsole console, String line, Long id) throws AskBreak{

        List<String> list = new java.util.ArrayList<>(List.of(line.trim().substring(1, line.length() - 1).split(",")));
        list.replaceAll(String::trim);

        if (list.stream().anyMatch(s -> s.equals("exit")))
            throw new AskBreak();

        try{
            String name = AskName(list.get(0).trim());
            Coordinates coordinates = AskCoordinates(console, list.get(1), list.get(2));
            Location from = AskLocation(console, list.get(3), list.get(4), list.get(5), list.get(6));
            Location to = AskLocation(console, list.get(7), list.get(8), list.get(9), list.get(10));
            int distance = AskDistance(console, list.get(11));
            return new Route(id, name, coordinates, ZonedDateTime.now(), from, to, distance);
        } catch (NoSuchElementException | IndexOutOfBoundsException e){
            return null;
        }

    }
    private static String AskName(String name){
        return name.isEmpty() ? null : name;
    }

    private static Coordinates AskCoordinates(StandardConsole console, String lineX, String lineY) throws AskBreak{

        double x = 0;
        if (!lineX.isEmpty()) {
            try {
                x = Double.parseDouble(lineX);
                if (x < -605){
                    console.printError("Coordinate x must be greater -605.");
                    return null;
                }
            } catch (NumberFormatException e) {
                console.printError("Coordinate x must be number.");
            }
        }

        float y = 0;
        if (!lineY.isEmpty()){
            try {
                y = Float.parseFloat(lineY);
                if (y>243) {
                    console.printError("Coordinate y must be less 244.");
                    return null;
                }
            }
            catch (NumberFormatException e) {
                console.printError("Coordinate y must be number.");
                return null;
            }
        }
        return new Coordinates(x, y);
    }
    private static Location AskLocation(StandardConsole console, String LocationName, String coordinateX, String coordinateY, String coordinateZ) throws AskBreak{

        String name = "";
        if (!LocationName.isEmpty()){
            if (LocationName.length() <=318)
                name = LocationName;
            else{
                console.printError("Location name length must be less then 318 symbols.");
                return null;
            }
        }

        long x = 0;
        if (!coordinateX.isEmpty()){
            try {x = Long.parseLong(coordinateX);}
            catch (NumberFormatException e) {
                console.println(coordinateX);
                console.printError("Coordinate x of Location must be integer number.");
                return null;
            }
        }

        long y = 0;
        if (!coordinateY.isEmpty()){
            try {y = Long.parseLong(coordinateY);}
            catch (NumberFormatException e) {
                console.printError("Coordinate y of Location must be integer number. ");
                return null;
            }
        }

        double z = 0;
        if (!coordinateZ.isEmpty()){
            try {z = Double.parseDouble(coordinateZ);}

            catch (NumberFormatException e) {
                console.printError("Coordinate z of Location must be number.");
                return null;
            }
        }
        return new Location(x, y, z, name);
    }

    private static Integer AskDistance(StandardConsole console, String distanceLine){
        int distance = 0;
        if (!distanceLine.isEmpty()){
                try{
                    distance = Integer.parseInt(distanceLine);
                    if(distance <= 1)
                        console.printError("Distance must be greater than 1.");

                } catch(NumberFormatException e){
                    console.printError("Distance must be number.");
                }
            }
        return distance;
    }
}
