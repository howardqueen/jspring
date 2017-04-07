package com.jspring.patterns.creational.builders;

public interface IDemoBuilder {
    void buildPart1();

    void buildPart2();

    void buildPart3();

    IDemoProduct getProduct();
}
