package Characters;

import World.Forest;
import World.GetEnding;

public class Guest extends Character implements IsInvited{
    boolean isinvinted = false;
    public Guest(String name, String gender){
        super(name, gender);
        this.name = name;
        this.gender = gender;
    }
    public Guest(String name){
        super(name);
        this.name = name;
    }
    public void Invited(){
        this.isinvinted = true;
    }
    //public GoAway(Wedding location){
       // location.RemovePeson(name);
   // }
}
