package com.jspring.patterns.structural.facades;

import com.jspring.patterns.structural.adapters.*;

public class DemoPatternsFacade {
    public void show() {
        //Show adapter
        IDemoTarget adapter = new DemoAdapter1(new IDemoSource1() {
            @Override
            public void act1() {
                System.out.println("this is 1.");
            }

            @Override
            public void act2() {
                System.out.println("this is 2.");
            }
        });
        System.out.println("actA adapted to act1.");
        adapter.actA();
        System.out.println("actB adapted to act2");
        adapter.actB();

        //show others...		

    }
}
