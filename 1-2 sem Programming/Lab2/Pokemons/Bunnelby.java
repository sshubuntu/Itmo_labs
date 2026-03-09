package Pokemons;
import Physical_attacks.Facade;
import Special_attacks.*;
import ru.ifmo.se.pokemon.*;

public class Bunnelby extends Pokemon {
    public Bunnelby(String name, int level){
        super(name, level);
        this.setType(Type.NORMAL);
        this.setStats(38, 36, 38, 32, 36, 57);
        this.setMove(new Mud_Slap(), new Mud_Shot(), new Facade());
    }
}
