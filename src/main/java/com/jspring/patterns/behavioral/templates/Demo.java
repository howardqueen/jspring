package com.jspring.patterns.behavioral.templates;

public abstract class Demo {
    public void main() {
        System.out.println("do something here.");
        act1();
        System.out.println("do something here.");
        act2();
        System.out.println("do something here.");
    }

    protected abstract void act1();

    protected abstract void act2();
}
