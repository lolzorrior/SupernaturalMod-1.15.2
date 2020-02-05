package com.lolzorrior.supernaturalmod.capabilities;

import java.util.concurrent.Callable;

public class ClassFactory implements Callable<ISupernaturalClasses> {

    @Override
    public ISupernaturalClasses call() throws Exception {
        return new SupernaturalClasses();
    }
}
