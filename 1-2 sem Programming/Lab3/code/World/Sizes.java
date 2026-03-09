package World;

public enum Sizes {
    BIG("большая"),
    SMALL("маленькая");

    private String type;

    Sizes(String type) {
        this.type = type;
    }

    public String Type() {
        return this.type;
    }
}
