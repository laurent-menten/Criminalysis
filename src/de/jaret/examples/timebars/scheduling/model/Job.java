package de.jaret.examples.timebars.scheduling.model;

import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

public class Job extends IntervalImpl implements Interval{
    private String _name;
    private int _priority;
    
    public Job(String name, JaretDate begin, int durationMinutes, int priority) {
        super(begin, begin.copy().advanceMinutes(durationMinutes));
        _name = name;
        _priority = priority;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public int getPriority() {
        return _priority;
    }

    public void setPriority(int priority) {
        _priority = priority;
    }
}
