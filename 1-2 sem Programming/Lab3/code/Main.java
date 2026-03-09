import Characters.*;
import Characters.Character;
import Events.CeremonyOfWedding;
import Events.TypeOfCelebration;
import Events.Wedding;
import Exceptions.InvalAction;
import World.Forest;
import World.Sizes;
import World.TimePeriods;
import World.TypesOfHealth;

public class Main {
    public static void main(String[] args) {
        Prince ch1 = new Prince("Принц", "m");
        Character ch2 = new Character("Принцесса", "w");
        OneMan ch3 = new OneMan("Один человек", "m");
        Character ch4 = new Character("Жена", "w");
        Son son1 = new Son();
        Son son2 = new Son();
        Son son3 = new Son();
        Son son4 = new Son();
        Son son5 = new Son();
        Son son6 = new Son();
        Son son7 = new Son();
        Guest g1 = new Guest("Гость1");
        Guest g2 = new Guest("Гость2");
        Guest g3 = new Guest("Гость3");
        Daughter d1 = new Daughter();
        System.out.printf("\ninitialization DONE!\n\n");
        try {


            ch1.GiveAnInstruction(TimePeriods.LONGAGO, "подготовить свадьбу");
            CeremonyOfWedding cer = new CeremonyOfWedding("Церемония бракосочетания");
            Forest location = new Forest();
            location.AddPerson(ch1, ch2, g1, g2, g3);
            Wedding event2 = new Wedding("Празднование", "лес", TypeOfCelebration.LUSH);
            event2.AddPerson(g1, g2, g3);
            System.out.printf("\n");
            ch3.WantABaby("w");
            ch3.HaveABaby("m", son7.getObjectCount());
            ch4.GiveAHope(TimePeriods.Finally);
            d1.Born();
            ch3.GetAJoy();
            d1.SetTypeOfHealth(TypesOfHealth.FRAIL);
            d1.SetSize(Sizes.SMALL);
            d1.NeedForBaptism();
            d1.Baptize(TimePeriods.EARlIER);
        }
        catch (InvalAction ex) {
            System.out.println("ОШИБКА!");
        }
    }
}
