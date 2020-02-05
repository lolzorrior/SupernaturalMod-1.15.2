package com.lolzorrior.supernaturalmod.capabilities;

public class SupernaturalClasses implements ISupernaturalClasses {
    private String supernaturalClass = "";


    @Override
    public void set(String classes) {
        this.supernaturalClass = classes;
    }

    @Override
    public String get() {
        return supernaturalClass;
    }
}
