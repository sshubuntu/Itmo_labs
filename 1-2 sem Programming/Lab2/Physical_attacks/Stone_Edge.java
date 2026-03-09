package Physical_attacks;

import ru.ifmo.se.pokemon.*;

public class Stone_Edge extends PhysicalMove {
    public Stone_Edge(){
        super(Type.ROCK, 100, 76.4);
    }
    @Override
    protected String describe(){
        return "использует атаку Stone Edge";
    }
}
