package com.jspring.patterns.behavioral.strategies;

public class DemoStringStrategy1 implements IDemoStrategy<String, String> {

    private final String args;

    public DemoStringStrategy1(String args) {
        this.args = args;
    }

    @Override
    public String getArgs() {
        return args;
    }

    @Override
    public String execute() {
        return "Do in one way:" + getArgs();
    }

}
