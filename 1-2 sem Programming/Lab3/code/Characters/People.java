package Characters;
import World.Sizes;
import World.TypesOfHealth;

abstract class People {
    String name;
    String gender;
    int countofcids;
    public Sizes size;
    public TypesOfHealth health = TypesOfHealth.HEALTHY;
    public People(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }

    public abstract String GetGender();

    public abstract String GetName();




}
