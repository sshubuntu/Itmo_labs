package World;

public enum IntelligenceLevel {
    Clever("умный");
    private String type;

    IntelligenceLevel(String type){
        this.type = type;
    }
    public String Type(){
        return this.type;
    }
}
