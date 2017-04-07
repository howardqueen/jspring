package com.jspring.patterns.structural.composites;

public interface IDemoComposite extends IDemoComponent {
    void add(IDemoComposite child);

    void remove(IDemoComposite child);

    IDemoComposite getChild(int i);
}
