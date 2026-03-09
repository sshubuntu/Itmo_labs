
import Pokemons.*;
import ru.ifmo.se.pokemon.*;

class lab2_main {
    public static void main(String[] args){
        Battle b = new Battle();
        Pokemon p1 = new Keldeo("Единорог", 1);
        Pokemon p2 = new Bunnelby("Заяц", 1);
        Pokemon p3 = new Diggersby("Ультра заяц", 1);
        Pokemon p4 = new Tynamo("Рыба", 1);
        Pokemon p5 = new Eelektrik("Прокаченная рыба", 1);
        Pokemon p6 = new Eelektross("Прокаченная рыба 2.0", 1);

        b.addAlly(p1);
        b.addFoe(p2);
        b.addAlly(p3);
        b.addFoe(p4);
        b.addAlly(p5);
        b.addFoe(p6);
        b.go();
    }
}
