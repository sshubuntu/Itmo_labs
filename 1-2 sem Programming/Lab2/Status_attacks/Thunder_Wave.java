package Status_attacks;

import ru.ifmo.se.pokemon.*;

import static ru.ifmo.se.pokemon.Effect.paralyze;

public class Thunder_Wave extends StatusMove {
    public Thunder_Wave(){
        super(Type.ELECTRIC,0,90);
    }
    @Override
    protected void applyOppEffects(Pokemon p){
        paralyze(p);
        System.out.println("Оппонент парализован!");
    }
    @Override
    protected String describe() {
        return "использует специальную атаку Thunder_Wave";
    }

}
