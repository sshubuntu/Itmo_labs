package Characters;

import World.TypesOfHealth;

public class Daughter extends Character implements NeedForBaptism{

    public Daughter(){
        super("Дочь", "w");
        this.name= name;
        this.gender = gender;

    }
    public void SetTypeOfHealth(){
        this.health = TypesOfHealth.FRAIL;
    }
    public void NeedForBaptism(){
        System.out.println(this.name+" необходимо крестить");
    }
}