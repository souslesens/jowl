package com.souslesens.Jowl.model;

public class myData {
    private boolean myBoolean;
    private String myString;

    public myData(boolean myBoolean, String myString) {
        this.myBoolean = myBoolean;
        this.myString = myString;
    }

    public boolean isMyBoolean() {
        return myBoolean;
    }

    public void setMyBoolean(boolean myBoolean) {
        this.myBoolean = myBoolean;
    }

    public String getMyString() {
        return myString;
    }

    public void setMyString(String myString) {
        this.myString = myString;
    }
}
