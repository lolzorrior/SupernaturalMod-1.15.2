package com.lolzorrior.supernaturalmod.capabilities;


import java.util.concurrent.Callable;

public class PowerFactory implements Callable<ISupernaturalPower> {
    @Override
    public ISupernaturalPower call() throws Exception {
        return new SupernaturalPower();
    }
}
