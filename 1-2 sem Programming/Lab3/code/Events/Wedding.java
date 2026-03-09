package Events;

import Characters.Guest;
import World.Forest;

import java.util.ArrayList;
import java.util.List;

public class Wedding extends Event{
    public Wedding(String name, String location, TypeOfCelebration type){
        super(name, location);
        this.name= name;
        this.location = location;
        System.out.println(type.Type() +" "+ this.name+" началось в " + location+"у");
    }

    public void AddPerson(Guest... guests){
        for (Guest ch:guests) {
            System.out.println(ch.GetName()+" пришел(ла) на "+this.name);
        }
    }
    public void Prepare() {
        System.out.println(this.name+ " готово");
    }

    @Override
    public String GetName() {
        return this.name;
    }
}
