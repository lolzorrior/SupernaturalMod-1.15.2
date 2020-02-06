package com.lolzorrior.supernaturalmod.capabilities;

public class SupernaturalPower implements ISupernaturalPower {
    private int power;

    public SupernaturalPower() {
        power = 0;
    }

    public void consume(int points) {
        this.power -= points;
        if (this.power < 0) this.power = 0;
    }

    @Override
    public void fill(int points) {
        this.power += points;
    }

    public void set(int points) {
        this.power = points;
    }

    public int getPower() {
        return this.power;
    }
}
