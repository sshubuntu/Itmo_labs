package model;

import Utility.Validatable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Класс локации маршрута
 * @author sh_ub
 */
public class Location implements Validatable, Serializable {
    private Long x; //Поле не может быть null
    private long y;
    private Double z; //Поле не может быть null
    private String name; //Длина строки не должна быть больше 318, Поле может быть null

    public Location(Long x, long y, Double z, String name) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.name = name;
    }

    public Location() {}

    public Double getZ() {
        return z;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Проверяет правильность полей.
     * @return true, если все верно, иначе false
     */
    @Override
    public boolean validate() {
        if (x== null) return false;
        if (z== null) return false;
        return !name.isEmpty() && name.length() <= 318;
    }

    @Override
    public String toString() {
        return name+"(" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +")";
    }
}

