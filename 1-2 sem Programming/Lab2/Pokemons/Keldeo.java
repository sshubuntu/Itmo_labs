package Pokemons;

import Physical_attacks.*;
import Status_attacks.*;
import ru.ifmo.se.pokemon.*;

public class Keldeo extends Pokemon{
    public Keldeo(String name, int level){
        super(name, level);
        this.setType(Type.WATER, Type.FIGHTING);
        this.setStats(91, 72, 90, 129, 90, 108);
        this.setMove(new Aqua_Tail(), new Confide(), new Stone_Edge(), new Leer());
    }
}
