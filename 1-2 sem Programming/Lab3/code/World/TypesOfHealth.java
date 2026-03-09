package World;

public enum TypesOfHealth{
    FRAIL("хилая"),
    HEALTHY("здоровая");
    private String type;

    TypesOfHealth(String type) {
        this.type = type;
    }

    public String Type() {
        return this.type;
    }

}
