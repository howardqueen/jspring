package com.jspring.io;

public interface ITextReader {
    String readLine();

    int read();

    int read(char[] buffer);

    int read(char[] buffer, int index, int count);

    void tryClose();
}
