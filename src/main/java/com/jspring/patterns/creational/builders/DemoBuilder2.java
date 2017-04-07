package com.jspring.patterns.creational.builders;

public class DemoBuilder2 implements IDemoBuilder {

    private final IDemoProduct product = new IDemoProduct() {

        @Override
        public void append(Object part) {
            System.out.println("I got it:" + part);
        }

    };

    @Override
    public void buildPart1() {
        product.append('a');
    }

    @Override
    public void buildPart2() {
        product.append('b');
    }

    @Override
    public void buildPart3() {
        product.append('c');
    }

    @Override
    public IDemoProduct getProduct() {
        return product;
    }

}
