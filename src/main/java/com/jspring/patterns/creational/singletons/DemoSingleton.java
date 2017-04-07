package com.jspring.patterns.creational.singletons;

public final class DemoSingleton {
    private DemoSingleton() {
    }

    public static final DemoSingleton singleton = new DemoSingleton();

    private static DemoSingleton lazySingleton;

    public static DemoSingleton getLazySingleton() {
        if (null == lazySingleton) {
            lazySingleton = new DemoSingleton();
        }
        return lazySingleton;
    }
}
