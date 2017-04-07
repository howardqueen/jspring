package com.jspring.patterns.behavioral.mediators;

public interface IDemoMediator {
    void setColleague1(IDemoColleague colleague);

    void setColleague2(IDemoColleague colleague);

    void acted1();

    void acted2();
}
