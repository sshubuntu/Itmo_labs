package World;

import Characters.Character;

public class GetEnding {
     public String GetEnd(String gender) {
         String End;
         switch (gender) {
            case "m"-> End = "";
            case "w"-> End = "а";
            default-> End = "(а)";
        }
        return End;
    }
}