package com.jspring.io;

public interface ITextWriter {	
    //bool, byte, double, float, int, long, short, datetime
    void write(boolean b);
    void write(byte b);
    void write(double d);
    void write(float f);
    void write(int i);
    void write(long l);
    void write(short s);
	void write(char c);
    void write(char[] cs);
    
    void writeLine(boolean b);
    void writeLine(byte b);
    void writeLine(double d);
    void writeLine(float f);
    void writeLine(int i);
    void writeLine(long l);
    void writeLine(short s);
    void writeLine(char c);
    void writeLine(char[] cs);
    
    
    void write(String text);
    void writeFormat(String format, Object... params);
    void writeLine(String text) ;
    void writeLineFormat(String format, Object... params);
    
    void writeLine();    
    void tryClose();
}
