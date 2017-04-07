package com.jspring.io;

class StringBuilderWriter extends BaseTextWriter {
    private final StringBuilder builder;

    public StringBuilderWriter(StringBuilder builder) {
        this.builder = builder;
    }

    @Override
    public void write(char c) {
        builder.append(c);
    }

    @Override
    public void write(char[] cs) {
        builder.append(cs);
    }

    @Override
    public void write(String text) {
        builder.append(text);
    }

    @Override
    public void tryClose() {
    }

}
