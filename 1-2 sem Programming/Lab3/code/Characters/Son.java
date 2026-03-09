package Characters;

public class Son implements NeedForBaptism{
    private static int objectCount = 0;
    public Son(){
        objectCount++;
    }
    public void NeedForBaptism(){

    }
    public static int getObjectCount() {
        return objectCount;
    }
}
