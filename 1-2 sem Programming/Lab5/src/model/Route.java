package model;

import Utility.Element;
import Utility.Validatable;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
/**
 * Класс маршрута
 * @author sh_ub
 */
public class Route extends Element implements Validatable, Serializable, Comparable<Route> {
    private Long id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Location from; //Поле может быть null
    private Location to; //Поле может быть null
    private int distance; //Значение поля должно быть больше 1

    public Route(Long id, String name, Coordinates coordinates, java.time.ZonedDateTime creationDate,
                 Location from, Location to, int distance) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public Route(Long id, String name, Coordinates coordinates, Location from, Location to, int distance) {
        this(id, name, coordinates, ZonedDateTime.now(), from, to, distance);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public java.time.ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public int getDistance() {
        return distance;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(java.time.ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setFrom(Location from) {
        this.from = from;
    }

    public void setTo(Location to) {
        this.to = to;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
    /**
     * Проверяет правильность полей.
     * @return true, если все верно, иначе false
     */
    @Override
    public boolean validate() {
        if (id == null || id < 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (from == null) return false;
        if (to == null) return false;
        return distance > 1;
    }
    public String toString() {
        return "Route : {\"id\" : "+id+", \"name\" : " + name + ", \"coordinates\" : " + coordinates +", \"creationDate\" : "
                + creationDate + ", \"from\" : " + from + ", \"to\" : " + to + ", \"distance\" : " + distance + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route that = (Route) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, creationDate, coordinates,  from, to,  distance);
    }

    /**
     * Сортирует объекты по полю distance.
     * @return 0, 1 или -1 в зависимости от полей distance
     */
    @Override
    public int compareTo(Route o) {
        return this.getDistance() - o.getDistance();
    }
}
