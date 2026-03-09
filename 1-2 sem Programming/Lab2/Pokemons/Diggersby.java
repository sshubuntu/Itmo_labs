package Pokemons;
import Physical_attacks.Hammer_arm;
import ru.ifmo.se.pokemon.Type;

public class Diggersby extends Bunnelby{
    public Diggersby(String name, int level){
         super(name,level);
         this.setType(Type.NORMAL, Type.GROUND);
         this.setStats(85,56,77,50,77,78);
         this.addMove(new Hammer_arm());
    }

}
