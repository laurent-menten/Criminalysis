/*
 *  File: SwingOverlapExample.java 
 *  Copyright (c) 2004-2007  Peter Kliem (Peter.Kliem@jaret.de)
 *  A commercial license is available, see http://www.jaret.de.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.jaret.examples.timebars.simple.swing;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import de.jaret.examples.timebars.simple.model.ModelCreator;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.IGlobalAssistantRenderer;

/**
 * Swing: the swing version of the overlap example (without drag and drop).
 * 
 * @author Peter Kliem
 * @version $Id: SwingTimeBarExample.java 202 2007-01-15 22:00:02Z olk $
 */
public class SwingOverlapExample {
    static TimeBarViewer _tbv;

    public static void main(String[] args) {
        SwingOverlapExample ex = new SwingOverlapExample();
    }

    public SwingOverlapExample() {
        JFrame f = new JFrame(SwingOverlapExample.class.getName());
        f.setSize(800, 500);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        final TimeBarModel model = ModelCreator.createModel();
        _tbv = new TimeBarViewer(model);

        _tbv.addIntervalModificator(new DefaultIntervalModificator());

        _tbv.setPixelPerSecond(0.05);
        _tbv.setDrawRowGrid(true);

        _tbv.setDrawOverlapping(false);
        _tbv.setSelectionDelta(6);
        _tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP);

        // timebar marker
        CustomTimeBarMarker marker1 = new CustomTimeBarMarker(true, new JaretDate().advanceHours(1), model.getRow(2));
        CustomTimeBarMarker marker2 = new CustomTimeBarMarker(true, new JaretDate().advanceHours(2), model.getRow(4));
        _tbv.addMarker(marker1);
        _tbv.addMarker(marker2);
        _tbv.setMarkerRenderer(new StoppingMarkerRenderer());
        
        _tbv.setGlobalAssistantRenderer(new IGlobalAssistantRenderer() {
            
            @Override
            public void doRenderingLast(TimeBarViewerDelegate delegate, Graphics graphics) {
                TimeBarRow row = delegate.getRow(2); // just use row ad index 2 as a test
                Rectangle bounds = delegate.getRowBounds(row);
                int endX = delegate.xForDate(new JaretDate().advanceHours(3)); // end date 
                Graphics2D g2 = (Graphics2D)graphics;
                       
                g2.setPaint(new GradientPaint(endX-300, bounds.y, Color.WHITE, endX, bounds.y, Color.GREEN));
                float alpha = .3f;
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2.fillRect(endX-300, bounds.y, 300, bounds.height); // just draw 300 pixels from dest x
            }
            
            @Override
            public void doRenderingBeforeIntervals(TimeBarViewerDelegate delegate, Graphics graphics) {
            }
        });
        
        
        
        // Box tsr with DST correction
        // BoxTimeScaleRenderer btsr = new BoxTimeScaleRenderer();
        // btsr.setCorrectDST(true);
        // _tbv.setTimeScaleRenderer(btsr);
        
        f.getContentPane().add(_tbv, BorderLayout.CENTER);

        f.getContentPane().add(new OverlapControlPanel(_tbv), BorderLayout.SOUTH);

        // export the viewer to an image
        //	        
        // JPanel bPanel = new JPanel();
        // JButton button=new JButton("export image");
        // bPanel.add(button);
        // f.getContentPane().add(bPanel, BorderLayout.WEST);
        // button.addActionListener(new ActionListener() {
        //	          
        // public void actionPerformed(ActionEvent e) {
        // DefaultTimeBarModel m = (DefaultTimeBarModel)model;
        //
        // BufferedImage bi = new BufferedImage(2000, 2000, BufferedImage.TYPE_4BYTE_ABGR);
        // TimeBarViewer viewer = new TimeBarViewer(model);
        // viewer.setVisible(true);
        // viewer.setBounds(0, 0, 2000, 2000);
        // viewer.doLayout();
        // viewer.printAll(bi.getGraphics());
        // try {
        // File outputfile = new File("saved.png");
        // ImageIO.write(bi, "png", outputfile);
        //	                  
        // } catch (IOException ex) {
        // ex.printStackTrace();
        // }
        // }
        // });

        setupDND();

        f.setVisible(true);
        
        
        // test mouse wheellistener
//        _tbv._diagram.addMouseWheelListener(new MouseWheelListener() {
//            
//            @Override
//            public void mouseWheelMoved(MouseWheelEvent arg0) {
//                System.out.println("mouse wheel");
//            }
//        });
        
        
    }

    private ArrayList<Interval> _draggedJobs;
    private ArrayList<Integer> _draggedJobsOffsets;
    private DefaultTimeBarRowModel _tbvDragOrigRow;
    private JaretDate _tbvDragOrigBegin;
    private JaretDate _tbvDragOrigEnd;

    /**
     * Setup time bar viewer as drag source and drop target. Quick hack: - one interval only - hold ALT to start a drag
     * (could be differentiated by other means)
     */
    private void setupDND() {

        // Drag source
        DragSource dragSource = DragSource.getDefaultDragSource();
        DragGestureListener dgl = new TimeBarViewerDragGestureListener();
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(_tbv._diagram,
                DnDConstants.ACTION_MOVE, dgl);

        // create and setup drop target
        DropTarget dropTarget = new DropTarget();
        _tbv.setDropTarget(dropTarget);

        try {
            dropTarget.addDropTargetListener(new DropTargetListener() {

                public void dropActionChanged(DropTargetDragEvent evt) {
                }

                public void drop(DropTargetDropEvent evt) {
                    if (_draggedJobs != null) {
                        TimeBarRow overRow = _tbv.getRowForXY(evt.getLocation().x, evt.getLocation().y);
                        if (overRow != null) {
                            for (Interval job : _draggedJobs) {
                                ((DefaultTimeBarRowModel) overRow).addInterval(job);
                            }
                            _tbv.setGhostIntervals(null, null);
                            evt.dropComplete(true);
                            evt.getDropTargetContext().dropComplete(true);
                            // TODO mystic problem with drop success
                            _tbvDragOrigRow = null; // mark the drag successful ...
                        }
                        _tbv.deHighlightRow();
                    }
                }

                public void dragOver(DropTargetDragEvent evt) {
                    TimeBarRow overRow = _tbv.getRowForXY(evt.getLocation().x, evt.getLocation().y);
                    if (overRow != null) {
                        _tbv.highlightRow(overRow);

                        JaretDate curDate = _tbv.dateForXY(evt.getLocation().x, evt.getLocation().y);
                        correctDates(_draggedJobs, curDate);

                        // tell the timebar viewer
                        _tbv.setGhostIntervals(_draggedJobs, _draggedJobsOffsets);
                        _tbv.setGhostOrigin(evt.getLocation().x, evt.getLocation().y);
                        // there could be a check whether dropping is allowed at the current location
                        if (true) {// dropAllowed(_draggedJobs, overRow)) {
                            evt.acceptDrag(DnDConstants.ACTION_MOVE);
                        } else {
                            evt.rejectDrag();
                            _tbv.setGhostIntervals(null, null);
                        }
                    } else {
                        _tbv.deHighlightRow();
                    }
                }

                public void dragExit(DropTargetEvent evt) {
                    _tbv.deHighlightRow();
                }

                public void dragEnter(DropTargetDragEvent evt) {
                }
            });
        } catch (TooManyListenersException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void correctDates(List<Interval> draggedJobs, JaretDate curDate) {
        for (int i = 0; i < draggedJobs.size(); i++) {
            Interval interval = draggedJobs.get(i);
            int secs = interval.getSeconds();
            interval.setBegin(curDate.copy());
            interval.setEnd(curDate.copy().advanceSeconds(secs));
        }
    }

    /**
     * Drag gesture listener.
     * 
     * @author kliem
     * @version $id:$
     */
    class TimeBarViewerDragGestureListener implements DragGestureListener {

        public void dragGestureRecognized(DragGestureEvent e) {
            Component c = e.getComponent();

            // if a marker is being dragged -> do nothing
            boolean markerDragging = _tbv.getDelegate().isMarkerDraggingInProgress();
            if (markerDragging) {
                return;
            }

            // check the intervals and maybe start a drag
            List<Interval> intervals = _tbv.getDelegate().getIntervalsAt(e.getDragOrigin().x, e.getDragOrigin().y);
            // start drag only if ALT is pressed
            if (intervals.size() > 0 && e.getTriggerEvent().isAltDown()) {
                Interval interval = intervals.get(0);
                e.startDrag(null, new StringSelection("Drag " + interval));
                _draggedJobs = new ArrayList<Interval>();
                _draggedJobs.add(interval);
                _draggedJobsOffsets = new ArrayList<Integer>();
                _draggedJobsOffsets.add(0);
                TimeBarRow row = _tbv.getModel().getRowForInterval(interval);
                ((DefaultTimeBarRowModel) row).remInterval(interval);

                // save orig data
                _tbvDragOrigRow = (DefaultTimeBarRowModel) row;
                _tbvDragOrigBegin = interval.getBegin().copy();
                _tbvDragOrigEnd = interval.getEnd().copy();

                return;
            }
        }
    }
}
