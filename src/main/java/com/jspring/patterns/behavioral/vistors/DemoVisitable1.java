package com.jspring.patterns.behavioral.vistors;

public class DemoVisitable1 implements IDemoVisitable {

    @Override
    public void accept(IDemoVistor vistor) {
        vistor.visit1(this);
    }

    public void act1() {
    }

    public void act2() {
    }

}
