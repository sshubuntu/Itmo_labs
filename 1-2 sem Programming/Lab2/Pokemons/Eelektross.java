package Pokemons;

import Physical_attacks.Facade;
import ru.ifmo.se.pokemon.*;

public class Eelektross extends Eelektrik{
    public Eelektross(String name, int level){
        super(name, level);
        this.setType(Type.ELECTRIC);
        this.setStats(85,115,80,105,80,50);
        this.addMove(new Facade());
    }
}
