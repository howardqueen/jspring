package com.jspring.patterns.creational.abstractfactory;

public interface IDemoFactory {
    IDemoConfig newConfig();

    IDemoHotConfig newHotConfig();
}
