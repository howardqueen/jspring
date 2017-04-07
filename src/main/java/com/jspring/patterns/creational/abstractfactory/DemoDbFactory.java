package com.jspring.patterns.creational.abstractfactory;

public class DemoDbFactory implements IDemoFactory {

    @Override
    public IDemoConfig newConfig() {
        return null;
    }

    @Override
    public IDemoHotConfig newHotConfig() {
        return null;
    }
}
