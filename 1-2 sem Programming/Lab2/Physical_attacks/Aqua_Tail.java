package Physical_attacks;
import ru.ifmo.se.pokemon.*;


public class Aqua_Tail extends PhysicalMove{
    public Aqua_Tail(){
        super(Type.WATER, 90,90);
    }
    protected String describe(){
        return "использует атаку Aqua_Tail";
    }

}
