package com.example.jomride;

import java.io.Serializable;

public class RideData implements Serializable {

    private String TimeDate;
    private double price;
    private int point;
    private String transport;
    private double rate;
    private double duration;
    private double distance;
    private String start;

    private String end;

    public RideData(){}
    public RideData(String datetime, double price, int point, String transport, double rate, double distance, double duration, String start, String end) {
        TimeDate=datetime;
        this.transport = transport;
        this.price=price;
        this.point = point;
        this.rate = rate;
        this.duration = duration;
        this.distance = distance;
        this.start = start;
        this.end = end;
    }


    public String getTimeDate() {
        return TimeDate;
    }

    public void setTimeDate(String timeDate) {
        TimeDate = timeDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public int getPoint() {
        return point;
    }
    public void setPoint(int point) {
        this.point = point;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }




}
