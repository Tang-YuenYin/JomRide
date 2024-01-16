package com.example.jomride;

public class Cashbackinfo {
    private double amount;
    private String dateTime;

    public Cashbackinfo(){};
    public Cashbackinfo(double amount, String dateTime){
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public double getAmount(){
        return this.amount;
    }

    public void setAmount(double amount){
        this.amount = amount;
    }
    public String getDateTime(){
        return this.dateTime;
    }
    public void setDateTime(String dateTime){
        this.dateTime = dateTime;
    }
}
