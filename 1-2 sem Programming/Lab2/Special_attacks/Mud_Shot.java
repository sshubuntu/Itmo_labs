package Special_attacks;

import ru.ifmo.se.pokemon.*;

public class Mud_Shot extends SpecialMove {
    public Mud_Shot(){
        super(Type.GROUND, 55, 95);
    }
    @Override
    protected void applyOppEffects(Pokemon p){
        p.setCondition(new Effect().chance(1).attack(1).stat(Stat.SPEED, -1));
    }
    @Override
    protected String describe(){
        return "использует специальную атаку Mud Shot";
    }
}
