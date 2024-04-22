package de.jaret.examples.timebars.scheduling.swing.renderer;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;

import de.jaret.examples.timebars.scheduling.model.Job;
import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.TimeBarRenderer;

public class JobRenderer implements TimeBarRenderer {
    /** component used for rendering. */
    protected JButton _component = new JButton();


    /**
     * {@inheritDoc}
     */
    public JComponent getTimeBarRendererComponent(TimeBarViewer tbv, Interval value, boolean isSelected,
            boolean overlapping) {

        _component.setText(value.toString());
        _component.setToolTipText(value.toString());
        
        Job job = (Job)value;
        switch (job.getPriority()) {
        case 0:
            _component.setBackground(Color.RED);
            break;
        case 1:
            _component.setBackground(Color.ORANGE);
            break;
        case 2:
            _component.setBackground(Color.YELLOW);
            break;
        case 3:
            _component.setBackground(Color.LIGHT_GRAY);
            break;

        default:
            break;
        }
        
        if (isSelected) {
            _component.setBackground(Color.BLUE);
          } 
        return _component;
    }

    /**
     * {@inheritDoc} Simple default implementation.
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea,
            TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean overlap) {
        return intervalDrawingArea;
    }
}
