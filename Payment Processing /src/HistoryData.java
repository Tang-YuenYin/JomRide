package com.example.jomride;

import java.io.Serializable;

public class HistoryData implements Serializable {
    private String DateTime;
    private String description;
    private String type;
    private String amount;

    public HistoryData()
    {
    }
    public HistoryData(String dateTime, String description, String type, String amount) {
        DateTime = dateTime;
        this.description = description;
        this.type = type;
        this.amount = amount;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
