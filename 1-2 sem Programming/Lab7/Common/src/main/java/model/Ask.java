package model;

import Command.CommandType;
import Utility.AskBreak;
import Utility.StandardConsole;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Класс для получения данных о маршруте от пользователя
 * @author sh_ub
 */
public class Ask {
    public static Route AskRoute(StandardConsole console, String args, CommandType command) throws AskBreak {
        Pattern FileRoute = Pattern.compile("\\{.+}");

        Pattern FileRouteWithId = Pattern.compile("\\d+\\s+\\{.+}");
        if (command == CommandType.ADD && args.isEmpty()) {
            return AskRoute(console);
        }
        else if (command == CommandType.ADD && FileRoute.matcher(args).matches()){
            return AskRoute(console, args);}

        else if (command == CommandType.UPDATE && FileRouteWithId.matcher(args).matches()) {
            try{
                String[] argList = args.split(" ", 2);
                Long id = Long.parseLong(argList[0]);
                return AskRoute(console, argList[1], id);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        else if(command == CommandType.UPDATE){
            try{
                Long id = Long.parseLong(args);
                return AskRoute(console, id);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        else
            return null;
    }

    private static Route AskRoute(StandardConsole console, Long id) throws AskBreak {
        Route route = AskRoute(console);
        route.setId(id);
        return route;
    }

    private static Route AskRoute(StandardConsole console) throws AskBreak {
        console.println("Add a Route:");
        var name = AskName(console);
        var coordinates = AskCoordinates(console);
        var from = AskLocation(console);
        var to = AskLocation(console);
        var distance = AskDistance(console);
        return new Route(0L, name, coordinates, ZonedDateTime.now(), from, to, distance);
    }

    private static Route AskRoute(StandardConsole console, String line, Long id) throws AskBreak {
        Route route = AskRoute(console, line);
        Objects.requireNonNull(route).setId(id);
        return route;
    }

    private static Route AskRoute(StandardConsole console, String line) throws AskBreak {
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
            return new Route(0L, name, coordinates, ZonedDateTime.now(), from, to, distance);
        } catch (NoSuchElementException | IndexOutOfBoundsException e){
            return null;
        }
    }

    private static String AskName(StandardConsole console) throws AskBreak{
        console.print("name: ");
        String name;
        try{
            while(true){
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
                if (!name.isEmpty()) break;
                console.print("name: ");
            }
            return name;

        }  catch(NoSuchElementException | IllegalStateException e){
            console.printError("Reading error");
            return null;
        }
    }

    private static String AskName(String name){
        return name.isEmpty() ? null : name;
    }

    private static Coordinates AskCoordinates(StandardConsole console) throws AskBreak{

        console.print("Coordinate x: ");
        try{
            double x;
            while(true){
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        x = Double.parseDouble(line);
                        if (x > -605) break;
                        console.print("Coordinate x must be greater -605. Coordinate x: ");
                    } catch (NumberFormatException e) {
                        console.print("Coordinate x must be number. Coordinate x: ");
                    }
                }
            }
            float y;
            console.print("Coordinate y: ");
            while(true){
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        y = Float.parseFloat(line);
                        if (y<=243) {
                            break;
                        }

                        console.print("Coordinate y must be less 244. Coordinate y: ");
                    }
                    catch (NumberFormatException e) {
                        console.print("Coordinate y must be number. Coordinate y: ");
                    }

                }
            }
            return new Coordinates(x,y);
        }  catch(NoSuchElementException | IllegalStateException e){
            console.printError("Reading error");
            return null;
        }
    }

    private static Coordinates AskCoordinates(StandardConsole console, String lineX, String lineY) {

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



    private static Location AskLocation(StandardConsole console) throws AskBreak{

        try{
            console.print("Location name: ");
            String name;

            while(true){
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    if (line.length() <=318) {
                        name = line;
                        break;
                    }
                    console.print("Location name length must be less then 318 symbols. Location name: ");
                }
            }
            console.print("Coordinate x of Location: ");
            long x;

            while(true){
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        x = Long.parseLong(line);
                        break;
                }
                    catch (NumberFormatException e) {
                        console.print("Coordinate x of Location must be integer number. Coordinate x of Location: ");
                    }
                }
            }
            console.print("Coordinate y of Location: ");
            long y;

            while(true){
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        y = Long.parseLong(line);
                        break;
                    }
                    catch (NumberFormatException e) {
                        console.print("Coordinate y of Location must be integer number. Coordinate y of Location: ");
                    }
                }
            }

            console.print("Coordinate z of Location: ");
            double z;

            while(true){
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try {
                        z = Double.parseDouble(line);
                        break;

                    }
                    catch (NumberFormatException e) {
                        console.print("Coordinate z of Location must be number. Coordinate z of Location:");
                    }
                }
            }
             return new Location(x, y, z, name);
        }catch(NoSuchElementException | IllegalStateException e){
            console.printError("Reading error");
            return null;
        }
    }

    private static Location AskLocation(StandardConsole console, String LocationName, String coordinateX, String coordinateY, String coordinateZ) {

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


    private static Integer AskDistance(StandardConsole console) throws AskBreak{
        console.print("Distance: ");
        try{
            int distance;
            while (true){
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()){
                    try{
                       distance = Integer.parseInt(line);

                       if(distance > 1) break;
                       console.print("Distance must be greater than 1. Distance: ");

                    }catch(NumberFormatException e){console.println("Distance must be number. Distance: ");}
                }
            }
            return distance;

        }catch(NullPointerException |NoSuchElementException | IllegalStateException e){
            console.printError("Reading error");
            return 0;
        }
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