package Physical_attacks;

import ru.ifmo.se.pokemon.*;

public class Spark extends PhysicalMove {
    public Spark(){
        super(Type.ELECTRIC, 65,100);
    }
    @Override
    protected void applyOppEffects(Pokemon p){
        p.setCondition(new Effect().chance(0.3).condition(Status.PARALYZE));
    }
    @Override
    protected String describe() {
        return "использует атаку Spark";
    }
}
