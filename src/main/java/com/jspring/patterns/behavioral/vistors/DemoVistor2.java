package com.jspring.patterns.behavioral.vistors;

public class DemoVistor2 implements IDemoVistor {

    @Override
    public void visit1(DemoVisitable1 element) {
        element.act2();
        System.out.println("Do anything here.");
    }

    @Override
    public void visit2(DemoVisitable2 element) {
        element.act2();
        System.out.println("Do anything here.");
    }

}
