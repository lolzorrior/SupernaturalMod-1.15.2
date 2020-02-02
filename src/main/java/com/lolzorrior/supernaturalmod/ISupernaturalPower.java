package com.lolzorrior.supernaturalmod;

public interface ISupernaturalPower {
    void consume(int points);
    void fill(int points);
    void set(int points);

    int getPower();
}
