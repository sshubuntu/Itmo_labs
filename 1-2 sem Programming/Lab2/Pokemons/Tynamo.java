package Pokemons;

import Special_attacks.Charge_Beam;
import Status_attacks.Thunder_Wave;
import ru.ifmo.se.pokemon.*;

public class Tynamo extends Pokemon{
    public Tynamo(String name, int level){
        super(name, level);
        this.setType(Type.ELECTRIC);
        this.setStats(35,55, 40, 45, 40,60);
        this.setMove(new Thunder_Wave(), new Charge_Beam());
    }
}
