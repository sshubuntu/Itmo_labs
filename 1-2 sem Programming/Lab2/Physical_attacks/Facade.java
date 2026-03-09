package Physical_attacks;

import ru.ifmo.se.pokemon.*;



public class Facade extends PhysicalMove {
    public Facade(){
        super(Type.NORMAL, 70,100);
    }
    @Override
    protected String describe(){
        return "использует физическую атаку Facade";
    }
    @Override
    protected void applyOppDamage(Pokemon def, double damage) {
        switch (def.getCondition()){
            case BURN, POISON, PARALYZE ->{
                super.applyOppDamage(def, damage*2);
                System.out.println("Нанесен критический удар!");
            }
            default -> super.applyOppDamage(def, damage);
        }
    }

}





