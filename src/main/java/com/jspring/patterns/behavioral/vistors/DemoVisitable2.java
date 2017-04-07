package com.jspring.patterns.behavioral.vistors;

public class DemoVisitable2 implements IDemoVisitable {

    @Override
    public void accept(IDemoVistor vistor) {
        vistor.visit2(this);
    }

    public void act1() {
    }

    public void act2() {
    }

}
