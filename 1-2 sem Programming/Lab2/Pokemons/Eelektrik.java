package Pokemons;

import Physical_attacks.Spark;
import ru.ifmo.se.pokemon.Type;

public class Eelektrik extends Tynamo{
    public Eelektrik(String name, int level){
        super(name, level);
        this.setType(Type.ELECTRIC);
        this.setStats(65,85,70,75, 70,40);
        this.addMove(new Spark());
    }

}
