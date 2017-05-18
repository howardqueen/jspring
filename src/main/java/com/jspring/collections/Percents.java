package com.jspring.collections;

public class Percents {
    public final int value;

    public Percents(int percents) {
        if (percents < 0 || percents > 99) {
            this.value = 100;
            return;
        }
        this.value = percents;
    }
}
