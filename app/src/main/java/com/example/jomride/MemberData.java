package com.example.jomride;

public class MemberData {
    private boolean isMember;
    private int rideCount;
    private double totalRideFare;
    private String fullName;
    private String birthday;
    private String icPassport;
    private String address;
    private String occupation;

    public MemberData(){};
    public MemberData(boolean isMember, int rideCount, double totalRideFare, String fullName, String birthday, String icPassport, String address, String occupation){
        this.isMember = isMember;
        this.rideCount = rideCount;
        this.totalRideFare = totalRideFare;
        this.fullName = fullName;
        this.birthday = birthday;
        this.icPassport = icPassport;
        this.address = address;
        this.occupation = occupation;
    }

    public boolean getIsMember(){
        return this.isMember;
    }
    public void setIsMember(boolean isMember){
        this.isMember = isMember;
    }
    public int getRideCount(){
        return this.rideCount;
    }
    public void setRideCount(int rideCount){
        this.rideCount = rideCount;
    }
    public double getTotalRideFare(){
        return this.totalRideFare;
    }
    public void setTotalRideFare(double totalRideFare){
        this.totalRideFare = totalRideFare;
    }
    public String getFullName(){
        return  this.fullName;
    }
    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public String getBirthday(){
        return this.birthday;
    }
    public void setBirthday(String birthday){
        this.birthday = birthday;
    }
    public String getIcPassport(){
        return this.icPassport;
    }
    public void setIcPassport(String icPassport){
        this.icPassport = icPassport;
    }
    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getOccupation(){
        return this.occupation;
    }
    public void setOccupation(String occupation){
        this.occupation = occupation;
    }
}
