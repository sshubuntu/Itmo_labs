package World;

import java.util.ArrayList;
import java.util.List;
import Characters.Character;

public class Forest {

    public Forest(){
        System.out.println("Создание локации лес");
    }
    protected List<Character> residents = new ArrayList<>();

    public void AddPerson(Character... characters){
        for (Character ch:characters) {
            residents.add(ch);
            GetEnding Ending = new GetEnding();
            System.out.println(ch.GetName()+" добавлен"+Ending.GetEnd(ch.GetGender())+" в лес");
        }
    }

    public void RemovePerson(Character ch){
        residents.remove(ch);
        System.out.println(ch.toString()+" уходит из леса");
    }
    public List<Character> GetResidents() {
        return residents;
    }

}
