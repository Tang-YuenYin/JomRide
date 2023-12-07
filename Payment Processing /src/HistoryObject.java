package com.example.jomride;

public class HistoryObject {

    private String transaction_id;
    private HistoryData data;

    public HistoryObject(String transaction_id, HistoryData data) {
        this.transaction_id = transaction_id;
        this.data = data;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public HistoryData getData() {
        return data;
    }

    public void setData(HistoryData data) {
        this.data = data;
    }
}
