package Special_attacks;

import ru.ifmo.se.pokemon.*;

public class Charge_Beam extends SpecialMove {
    public Charge_Beam(){
        super(Type.ELECTRIC,50,90);
    }
    @Override
    protected void	applySelfEffects(Pokemon p){
        p.setCondition(new Effect().chance(0.7).stat(Stat.SPECIAL_ATTACK, +1));
    }
    @Override
    protected String describe() {
        return "использует атаку Charge_Beam";
    }
}
