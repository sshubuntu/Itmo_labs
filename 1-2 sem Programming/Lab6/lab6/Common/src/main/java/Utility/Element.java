package Utility;

import model.Coordinates;
import model.Location;
/**
 * Абстрактный класс для маршрута
 * @author sh_ub
 */
public abstract class Element {
       abstract public Long getId();
       abstract public String getName();
       abstract public Coordinates getCoordinates();
       abstract public java.time.ZonedDateTime getCreationDate();
       abstract public Location getFrom();
       abstract public Location getTo();
       abstract public int getDistance();
       abstract public void setId(Long id);
       abstract public void setName(String name);
       abstract public void setCoordinates(Coordinates coordinates);
       abstract public void setCreationDate(java.time.ZonedDateTime creationDate);
       abstract public void setFrom(Location from);
       abstract public void setTo(Location to);
       abstract public void setDistance(int distance);
}
