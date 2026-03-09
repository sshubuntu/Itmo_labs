package Characters;

import World.*;

import java.util.Objects;


public class Character extends People{


    public Character(){
        super("Безымянный", "неопределенный");
        System.out.println("Безымянный человек создан(a)");
    }
    public Character(String name, String gender){
        super(name, gender);
        this.name = name;
        this.gender = gender;
        GetEnding Ending = new GetEnding();
        System.out.println(name+" создан"+Ending.GetEnd(gender));

    }
    public Character(String name) {
        super(name, "неопределенный");
        this.name = name;
        System.out.println(name + " создан(a)");
    }

    public void Born(){
        System.out.println("родился(ась) "+ this.toString());
    }

    public void Baptize(TimePeriods time){

        System.out.println("крестили "+this.GetName()+' '+time.Type());
    }
    public void SetTypeOfHealth(TypesOfHealth health){
        this.health = health;
        System.out.println(this.name+" была "+health.Type());
    }
    public TypesOfHealth GetHealth(People p1){
        return p1.health;
    }
    public void GetAJoy(){
        System.out.println("радость была "+Sizes.BIG.Type());
    }
    public void GiveAnInstruction(TimePeriods time, String instruction){
        GetEnding Ending = new GetEnding();
        System.out.println(toString()+" "+time.Type()+" дал "+Ending.GetEnd(gender) +" распоряжение "+instruction);
    }
    public void GiveAHope(TimePeriods time){
        GetEnding Ending = new GetEnding();
        System.out.println(time.Type()+" "+this.GetName()+" подал"+Ending.GetEnd(this.GetGender())+" "+TypesOfHope.KIND.Type()+" надежду");
    }
    public void SetSize(Sizes size){
        this.size = size;
        System.out.println(name+" была "+size.Type());
    }
    public TypesOfHealth GetHealth(){
        try {
            return health;
        }
        finally {
            return TypesOfHealth.HEALTHY;
        }


    }
    public void WantABaby(String gender){
        GetEnding Ending = new GetEnding();
        System.out.print(toString()+" хотел"+Ending.GetEnd(this.GetGender()));
        if (gender=="w") System.out.println(" дочку");
        else if (gender=="m") System.out.println(" сына");
        else System.out.println("ребенка");
    }
    public void HaveABaby(String gender, int count){
        GetEnding Ending = new GetEnding();
        System.out.print(toString()+" имел"+Ending.GetEnd(this.GetGender()));
        if (gender=="w") System.out.printf(" %d дочерей\n", count);
        else if (gender=="m") System.out.printf(" %d сыновей\n", count);
    }
    @Override
    public String GetGender() {
        return gender;
    }


    @Override
    public String GetName() {
        return name;
    }



    @Override
    public String toString(){
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Character character = (Character) o;
        return this.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, gender);
    }
}
