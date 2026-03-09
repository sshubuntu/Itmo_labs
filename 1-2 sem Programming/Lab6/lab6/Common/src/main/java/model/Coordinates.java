package model;

import Utility.Validatable;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Coordinates implements Validatable, Serializable {
    @JsonProperty("x")
    private Double x; //Значение поля должно быть больше -605, Поле не может быть null
    @JsonProperty("y")
    private Float y; //Максимальное значение поля: 243, Поле не может быть null

    public Coordinates(double x, float y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates() {}

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "("+x + "; " + y+")";
    }

    @Override
    public boolean validate() {
        if (x== null) return false;
        return y != null;
    }
}
