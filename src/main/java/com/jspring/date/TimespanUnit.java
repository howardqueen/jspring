package com.jspring.date;

/**
 * @author HowardQian(howard.queen@qq.com), 2012-05-04 12:43
 */
public class TimespanUnit {

    public final long value;
    public final TimeUnits unit;

    public TimespanUnit(int value, TimeUnits unit) {
        this.value = value;
        this.unit = unit;
    }

    public long getTimeInMillis() {
        return value * unit.milliseconds;
    }

    @Override
    public String toString() {
        return String.format("%s %s", value, unit);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof TimespanUnit)) { return false; }
        TimespanUnit t = (TimespanUnit) obj;
        return value == t.value && unit.equals(t.unit);
    }

}
