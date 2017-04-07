package com.jspring.patterns.behavioral.mediators;

public class DemoColleague1 implements IDemoColleague {

    private final IDemoMediator mediator;

    public DemoColleague1(IDemoMediator mediator) {
        this.mediator = mediator;
        mediator.setColleague1(this);
    }

    @Override
    public IDemoMediator getMediator() {
        return mediator;
    }

    @Override
    public void act() {
        System.out.println("Do anything here.");
        mediator.acted1();
    }

}
