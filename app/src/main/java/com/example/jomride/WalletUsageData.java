package com.example.jomride;

public class WalletUsageData {

    String usage;
    String inout;

    String DateTime;


    public WalletUsageData(){

    }
    public WalletUsageData(String usage, String inout, String DateTime) {
        this.usage = usage;
        this.inout = inout;
        this.DateTime= DateTime;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }


    public String getInout() {
        return inout;
    }

    public void setInout(String inout) {
        this.inout = inout;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }
}
