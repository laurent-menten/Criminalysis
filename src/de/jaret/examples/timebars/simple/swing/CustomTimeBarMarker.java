package de.jaret.examples.timebars.simple.swing;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.model.TimeBarRow;

public class CustomTimeBarMarker extends TimeBarMarkerImpl{
    protected TimeBarRow _stopRow;
    
    public CustomTimeBarMarker(boolean draggable, JaretDate date) {
        super(draggable, date);
    }
    
    public CustomTimeBarMarker(boolean draggable, JaretDate date, TimeBarRow stopRow) {
        super(draggable, date);
        _stopRow = stopRow;
    }

    public TimeBarRow getStopRow() {
        return _stopRow;
    }
    
    
}
