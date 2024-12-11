package models;

public class Category {
    private int id;
    private String name;
    private boolean isDefault;
    private double budgetLimit;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public double budgetLimit(boolean isDefault) {
        return budgetLimit;
    }
}
