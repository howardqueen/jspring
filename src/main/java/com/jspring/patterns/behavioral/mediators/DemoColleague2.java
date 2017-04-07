package com.jspring.patterns.behavioral.mediators;

public class DemoColleague2 implements IDemoColleague {

    private final IDemoMediator mediator;

    public DemoColleague2(IDemoMediator mediator) {
        this.mediator = mediator;
        mediator.setColleague2(this);
    }

    @Override
    public IDemoMediator getMediator() {
        return mediator;
    }

    @Override
    public void act() {
        System.out.println("Do anything here.");
        mediator.acted2();
    }

}
