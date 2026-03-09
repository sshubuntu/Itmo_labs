package Physical_attacks;

import ru.ifmo.se.pokemon.*;

public class Hammer_arm extends PhysicalMove {
    public Hammer_arm(){
        super(Type.FIGHTING,100, 90);
    }
    @Override
    protected String describe(){
        return "ипользует физическую атаку  Hammer Arm";
    }
    @Override
    protected void applyOppEffects(Pokemon p) {
        p.setCondition(new Effect().chance(1).stat(Stat.SPEED, -1));
    }
}
