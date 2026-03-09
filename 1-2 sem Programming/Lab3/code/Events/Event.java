package Events;

import Characters.Guest;
import World.Forest;
import World.Sizes;
import World.TypesOfHealth;

import java.util.ArrayList;
import java.util.List;

public abstract class Event {
    List<Guest> guests = new ArrayList<>();
    String name;
    String location;
    public Event(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public abstract String GetName();

}
