package Characters;

import World.IntelligenceLevel;

public class Prince extends Character{
    public Prince(String name, String gender){
        super(name, gender);
        this.name = name+" "+ IntelligenceLevel.Clever.Type();
        this.gender = gender;
    }
}
