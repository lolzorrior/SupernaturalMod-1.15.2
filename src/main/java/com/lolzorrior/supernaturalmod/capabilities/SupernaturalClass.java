package com.lolzorrior.supernaturalmod.capabilities;

public class SupernaturalClass implements ISupernaturalClass {
    private String supernaturalClass;

    @Override
    public void setSupernaturalClass(String classes) {
        this.supernaturalClass = classes;
    }

    @Override
    public String getSupernaturalClass() {
        return supernaturalClass;
    }

    public static SupernaturalClass getDefaultClass() {
        SupernaturalClass supernaturalClass = new SupernaturalClass();
        supernaturalClass.setSupernaturalClass("Human");
        return supernaturalClass;
    }
}
