package com.lolzorrior.supernaturalmod.capabilities;

public class SupernaturalClass implements ISupernaturalClass {
    private String supernaturalClass;

    public SupernaturalClass(){
        supernaturalClass = "Human";
    }

    @Override
    public void setSupernaturalClass(String classes) {
        this.supernaturalClass = classes;
    }

    @Override
    public String getSupernaturalClass() {
        return supernaturalClass;
    }

}
