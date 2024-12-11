package models;
public class Expense {
    private String title;
    private double amount;
    private String date;

    public Expense(String title, double amount, String date) {
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
