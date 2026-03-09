package World;

public enum TypesOfHope {
    KIND("добрую");
    private String type;

    TypesOfHope(String type){
        this.type =type;
    }
    public String Type(){
        return type;
    }
}
