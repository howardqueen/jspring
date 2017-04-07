package com.jspring.date;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-05-04 12:34
 */
public enum TimeUnits {
    Year(365 * 24 * 60 * 60 * 1000L), Month(30 * 24 * 60 * 60 * 1000L), Week(7
        * 24
        * 60
        * 60
        * 1000L), Day(24 * 60 * 60 * 1000L), Hour(60 * 60 * 1000L), Minute(
            60 * 1000L), Second(1000L), MilliSecond(1L);
    public final long milliseconds;

    TimeUnits(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
