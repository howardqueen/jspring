package com.jspring.patterns.behavioral.strategies;

public class DemoStringStrategy2 extends DemoStringStrategy1 {

    public DemoStringStrategy2(String args) {
        super(args);
    }

    @Override
    public String execute() {
        return "Do in another way:"
            + getArgs().substring(0, getArgs().length() / 2);
    }

}
