package com.jspring.patterns.creational.builders;

public class DemoDirector {
    public IDemoProduct construct(IDemoBuilder builder) {
        builder.buildPart1();
        builder.buildPart2();
        builder.buildPart3();
        return builder.getProduct();
    }
}
