package com.jspring.patterns.structural.adapters;

public class DemoAdapter1 implements IDemoTarget {

    private final IDemoSource1 source;

    public DemoAdapter1(IDemoSource1 source) {
        this.source = source;
    }

    @Override
    public void actA() {
        source.act1();
    }

    @Override
    public void actB() {
        source.act2();
    }

}
