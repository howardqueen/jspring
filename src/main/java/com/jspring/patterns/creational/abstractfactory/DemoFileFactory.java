package com.jspring.patterns.creational.abstractfactory;

public class DemoFileFactory implements IDemoFactory {

    @Override
    public IDemoConfig newConfig() {
        return null;
    }

    @Override
    public IDemoHotConfig newHotConfig() {
        return null;
    }
}
