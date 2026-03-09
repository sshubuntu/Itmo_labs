package Events;

public enum TypeOfCelebration {
    LUSH("пышное"),
    MODEST("скромное");
    private String type;

    TypeOfCelebration(String type){
        this.type = type;
    }
    public String Type(){
        return this.type;
    }
}
