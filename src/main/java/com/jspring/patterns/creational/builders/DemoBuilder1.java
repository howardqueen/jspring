package com.jspring.patterns.creational.builders;

public class DemoBuilder1 implements IDemoBuilder {

    private final IDemoProduct product = new IDemoProduct() {

        @Override
        public void append(Object part) {
            System.out.println("done:" + part);
        }

    };

    @Override
    public void buildPart1() {
        product.append(1);
    }

    @Override
    public void buildPart2() {
        product.append(2);
    }

    @Override
    public void buildPart3() {
        product.append(3);
    }

    @Override
    public IDemoProduct getProduct() {
        return product;
    }

}
