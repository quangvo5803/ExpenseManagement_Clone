package com.example.myapplication.data.model;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

public class Transaction  implements Serializable {
    private int id;
    private String type; // "income" hoặc "expense"
    private String category;
    private double amount;
    private String date;
    private String note;

    public Transaction(int id, String type, String category, double amount, String date, String note) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public  double getAmount(){return amount;}
    public String getAmountInVND() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }    public String getDate() { return date; }
    public String getNote() { return note; }

    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setCategory(String category) { this.category = category; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
}
