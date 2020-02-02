package com.lolzorrior.supernaturalmod;

public class SupernaturalPower implements ISupernaturalPower {
    private int power = 0;

    public void consume(int points)
    {
        this.power -= points;
        if (this.power < 0) this.power = 0;
    }

    public void fill(int points)
    {
        this.power += points;
    }

    public void set(int points)
    {
        this.power = points;
    }

    public int getPower()
    {
        return this.power;
    }
}
