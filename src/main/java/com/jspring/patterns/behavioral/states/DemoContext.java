package com.jspring.patterns.behavioral.states;

public abstract class DemoContext {

    private int a = 0;
    private String b = "1";

    protected IDemoState getState() {
        if (a == 0 && b.equals("2")) { return new IDemoState() {

            @Override
            public void handle() {
                System.out.println("Handle with method 1");
            }

        }; }
        return new IDemoState() {

            @Override
            public void handle() {
                System.out.println("Handle with method 2");
            }

        };
    }

    public void request() {
        getState().handle();
    }

}
