package model;

public class Phase {
    private String name;
    private String description;
    private String price;

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private boolean isFinished;

    public Phase() {
    }

    public Phase(String name, String description,String price, boolean isFinished) {
        this.description = description;
        this.name = name;
        this.price = price;
        this.isFinished = isFinished;
    }

    public String getName() {

        return name;
    }

    public boolean isFinished() {
        return isFinished;
    }
    //TODO: add fields...
}
