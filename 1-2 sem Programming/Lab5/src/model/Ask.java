package model;

import Client.StandardConsole;
import Utility.AskBreak;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
/**
 * Класс для получения данных о маршруте от пользователя
 * @author sh_ub
 */
public class Ask {
    public static Route AskRoute(StandardConsole console, Long id) throws AskBreak {
        console.println("Add a Route:");
        var name = AskName(console);
        var coordinates = AskCoordinates(console);
        var from = AskLocation(console);
        var to = AskLocation(console);
        var distance = AskDistance(console);
        return new Route(id, name, coordinates, ZonedDateTime.now(), from, to, distance);
    }
    public static String AskName(StandardConsole console) throws AskBreak{
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

    public static Coordinates AskCoordinates(StandardConsole console) throws AskBreak{

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
    public static Location AskLocation(StandardConsole console) throws AskBreak{

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
    public static Integer AskDistance(StandardConsole console) throws AskBreak{
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

}
