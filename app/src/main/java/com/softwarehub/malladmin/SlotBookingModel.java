package com.softwarehub.malladmin;

public class SlotBookingModel {
    String userId;
    String noMember;
    String email;
    String mobileNo;

    public SlotBookingModel(){

    }
    public SlotBookingModel(String userId, String noMember) {
        this.userId = userId;
        this.noMember = noMember;
    }

    public SlotBookingModel(String userId, String noMember,String email,String mobileNo) {
        this.userId = userId;
        this.noMember = noMember;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoMember() {
        return noMember;
    }

    public void setNoMember(String noMember) {
        this.noMember = noMember;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
}
