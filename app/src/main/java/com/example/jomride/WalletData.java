package com.example.jomride;

public class WalletData {
    private String balance;
    private int point;

    public WalletData(){}
    public WalletData(String balance, int point) {
        this.balance = balance;
        this.point = point;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

}
