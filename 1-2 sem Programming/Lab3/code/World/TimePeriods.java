package World;

public enum TimePeriods {
    EARlIER("раньше срока"),
    LONGAGO("давно"),
    Finally("наконец");

    private String type;

    TimePeriods(String type){
        this.type = type;
    }
    public String Type(){
        return this.type;
    }
}
