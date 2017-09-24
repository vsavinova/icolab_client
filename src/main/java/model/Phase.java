package model;

import org.web3j.abi.datatypes.Uint;

public class Phase {
    private int uid;
    private String name;
    private String description;
    private String price;
    private double estimation;
    private boolean isNew;
    private boolean isFinished; // if !isFinished && !isNew && !isEstimated -> in Working

    private boolean isEstimated;

    public double getEstimation() {
        return estimation;
    }
    public boolean isEstimated() {
        return isEstimated;
    }

    public boolean isNew() {
        return isNew;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Phase() {
    }

    public int getUid() {
        return uid;
    }

    public Phase(int uid, String name, String description, String price, double estimation, boolean isFinished,
                 boolean isNew, boolean isEstimated) {
        this.uid = uid;

        this.description = description;
        this.name = name;
        this.price = price;
        this.estimation = estimation;
        this.isFinished = isFinished;
        this.isNew = isNew;
        this.isEstimated = isEstimated;
    }

    public String getName() {

        return name;
    }

    public boolean isFinished() {
        return isFinished;
    }
    //TODO: add fields...
}
