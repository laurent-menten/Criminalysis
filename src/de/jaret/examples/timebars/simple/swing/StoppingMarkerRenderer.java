package de.jaret.examples.timebars.simple.swing;



import java.awt.Color;
import java.awt.Graphics;

import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.renderer.IMarkerRenderer;

/**
 * Stopping marker renderer rendering the marker as a single line.
 * ATTENTION: does not support VERTICAL ORIENTATION
 * 
 * @author kliem
 * @version $Id: DefaultMarkerRenderer.java 823 2009-02-04 21:20:58Z kliem $
 */
public class StoppingMarkerRenderer implements IMarkerRenderer {
    /** color used when the marker is dragged. */
    protected Color _draggedColor = Color.BLUE;
    /** color for marker rendering. */
    protected Color _markerColor = Color.RED;

    /**
     * {@inheritDoc}
     */
    public int getMarkerWidth(TimeBarMarker marker) {
        return 4;
    }

    /**
     * {@inheritDoc}
     */
    public void renderMarker(TimeBarViewerDelegate delegate, Graphics graphics, TimeBarMarker marker, int x,
            boolean isDragged) {
        Color oldCol = graphics.getColor();
        if (isDragged) {
            graphics.setColor(_draggedColor);
        } else {
            graphics.setColor(_markerColor);
        }
        int stopY;
        
        if (marker instanceof CustomTimeBarMarker) {
            CustomTimeBarMarker cmarker = (CustomTimeBarMarker)marker;
            TimeBarRow stopRow = cmarker.getStopRow();
            System.out.println("row "+stopRow);
            if (delegate.isRowDisplayed(stopRow)) {
                stopY = delegate.getRowBounds(stopRow).y+delegate.getRowBounds(stopRow).height;
                System.out.println("found "+stopY);
            } else {
                // might be above or below the viewport
                if (delegate.getAbsPosForRow(delegate.getRowIndex(stopRow))<0) {
                    stopY = 0;
                } else {
                    stopY = delegate.getDiagramRect().height + delegate.getXAxisHeight();
                }
            }
        } else {
            // default behaviour
            stopY = delegate.getDiagramRect().height + delegate.getXAxisHeight();
        }
        
        
        graphics.drawLine(x, 0, x, stopY);

        graphics.setColor(oldCol);
    }

}
