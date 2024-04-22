package de.jaret.examples.timebars.scheduling.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jaret.examples.timebars.scheduling.model.Job;
import de.jaret.examples.timebars.scheduling.model.ScheduleTimeBarModel;
import de.jaret.examples.timebars.scheduling.swing.renderer.JobRenderer;
import de.jaret.examples.timebars.scheduling.swing.renderer.ScheduleingTitleRenderer;
import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.misc.Pair;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarSelectionListener;
import de.jaret.util.ui.timebars.model.TimeBarSelectionModel;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.BoxTimeScaleRenderer;
import de.jaret.util.ui.timebars.swing.renderer.ITitleRenderer;

public class SchedulingPanel extends JPanel {

    JTable _jobTable;
    JobTableModel _jobTableModel;

    TimeBarViewer _tbv;
    ScheduleTimeBarModel _model;

    protected List<Job> _draggedJobs;
    protected List<Integer> _draggedJobsOffsets;

    protected JaretDate _tbvDragOrigBegin;
    protected JaretDate _tbvDragOrigEnd;
    protected DefaultTimeBarRowModel _tbvDragOrigRow;
    
    public SchedulingPanel() {
        setLayout(new BorderLayout());
        // split pane for tbv/table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.85);
        add(splitPane, BorderLayout.CENTER);

        // TBV
        _model = createTBVModel();
        _tbv = new TimeBarViewer();
        _tbv.setModel(_model);
        splitPane.setTopComponent(_tbv);

        // do some configurations on the timebarviewer
        _tbv.setDrawRowGrid(true);
        _tbv.setYAxisWidth(150);
        _tbv.setTimeScaleRenderer(new BoxTimeScaleRenderer());
        _tbv.setTimeScalePosition(TimeBarViewerInterface.TIMESCALE_POSITION_TOP);
        _tbv.setInitialDisplayRange(new JaretDate(), 24 * 60 * 60);

        // Interval modificator preventing intervals from beeing overlapped by shifting or sizing
        _tbv.addIntervalModificator(new PreventOverlapIntervalModificator());

        // register the renderer
        _tbv.registerTimeBarRenderer(Job.class, new JobRenderer());
        
        setUpDND(_tbv);

        createActions(_tbv);

        // table
        _jobTableModel = createJobTableModel();
        _jobTable = new JTable(_jobTableModel);
        JScrollPane scroll = new JScrollPane(_jobTable);
        splitPane.setBottomComponent(scroll);

        // add dnd support
        DragSource dragSource = DragSource.getDefaultDragSource();
        DragGestureListener dgl = new JobTableDragGestureListener();
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(_jobTable, DnDConstants.ACTION_MOVE,
                dgl);

        // controls at the bottom
        JPanel controlPanel = createControlPanel(24 * 60 * 60);
        add(controlPanel, BorderLayout.SOUTH);

        
        // setup title renderer
        // use the component directly
        ITitleRenderer trenderer = new ScheduleingTitleRenderer();
        _tbv.setTitleRenderer(trenderer);
        _tbv.setUseTitleRendererComponentInPlace(true);
        
    }

    /**
     * Setup some actions on the timebar viewer.
     * 
     * @param tbv
     */
    private void createActions(TimeBarViewer tbv) {
        // add a popup menu for the header
        Action action = new AbstractAction("Clear work center schedule") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("run " + getValue(NAME));
                TimeBarRow row = _tbv.getPopUpInformation().getLeft();
                clearRow(row);
            }

        };
        JPopupMenu pop = new JPopupMenu("Operations");
        pop.add(action);
        _tbv.setHeaderContextMenu(pop);

        // add a popup menu for EventIntervals
        action = new AbstractAction("Unschedule") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("run " + getValue(NAME));
                unscheduleSelected();
            }
        };
        pop = new JPopupMenu("Operations");
        pop.add(action);
        action = new AbstractAction("Push back") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("run " + getValue(NAME));
                Pair<TimeBarRow, JaretDate> info = _tbv.getPopUpInformation();
                List<Interval> intervals = info.getLeft().getIntervals(info.getRight());
                if (intervals.size() == 1) {
                    System.out.println("pushback on "+intervals.get(0));
                }
            }
            
        };
        pop.add(action);
        _tbv.registerPopupMenu(Job.class, pop);
    }

    private void unscheduleSelected() {
        List<Interval> intervals = new ArrayList<Interval>(_tbv.getSelectionModel().getSelectedIntervals());
        for (Interval interval : intervals) {
            TimeBarRow row = _model.getRowForInterval(interval);
            ((DefaultTimeBarRowModel)row).remInterval(interval);
            _jobTableModel.addJob((Job)interval);
        }
    }
    private void clearRow(TimeBarRow row) {
        List<Interval> intervals = new ArrayList<Interval>(row.getIntervals());
        for (Interval interval : intervals) {
            ((DefaultTimeBarRowModel)row).remInterval(interval);
            _jobTableModel.addJob((Job)interval);
        }
    }
    
    
    /**
     * Setup the droptarget and the drag source on the timebar viewer.
     * 
     * @param tbv
     */
    private void setUpDND(final TimeBarViewer tbv) {

        // create and setup drop target

        DropTarget dropTarget = new DropTarget();
        tbv.setDropTarget(dropTarget);

        try {
            dropTarget.addDropTargetListener(new DropTargetListener() {

                public void dropActionChanged(DropTargetDragEvent evt) {
                }

                public void drop(DropTargetDropEvent evt) {
                    if (_draggedJobs != null) {
                        TimeBarRow overRow = tbv.getRowForXY(evt.getLocation().x, evt.getLocation().y);
                        if (overRow != null) {
                            for (Job job : _draggedJobs) {
                                ((DefaultTimeBarRowModel) overRow).addInterval(job);
                                _jobTableModel.removeJob(job);
                            }
                            tbv.setGhostIntervals(null, null);
                            evt.dropComplete(true);
                            evt.getDropTargetContext().dropComplete(true);
                            // TODO mystic problem with drop success
                            _tbvDragOrigRow = null; // mark the drag successful ...
                        }
                        tbv.deHighlightRow();
                    }
                    
                }

                public void dragOver(DropTargetDragEvent evt) {
                    TimeBarRow overRow = tbv.getRowForXY(evt.getLocation().x, evt.getLocation().y);
                    if (overRow != null) {
                        tbv.highlightRow(overRow);

                        JaretDate curDate = tbv.dateForXY(evt.getLocation().x, evt.getLocation().y);
                        correctAndScheduleJobs(_draggedJobs, curDate);

                        // tell the timebar viewer
                        tbv.setGhostIntervals(_draggedJobs, _draggedJobsOffsets);
                        tbv.setGhostOrigin(evt.getLocation().x, evt.getLocation().y);
                        if (dropAllowed(_draggedJobs, overRow)) {
                            evt.acceptDrag(DnDConstants.ACTION_MOVE);
                        } else {
                            evt.rejectDrag();
                            tbv.setGhostIntervals(null, null);
                        }
                    } else {
                        tbv.deHighlightRow();
                    }
                }

                public void dragExit(DropTargetEvent evt) {
                    tbv.deHighlightRow();
                }

                public void dragEnter(DropTargetDragEvent evt) {
                }
            });
        } catch (TooManyListenersException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
        
        // add drag source
        DragSource dragSource = DragSource.getDefaultDragSource();
        DragGestureListener dgl = new TimeBarViewerDragGestureListener();
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(_tbv._diagram,
                DnDConstants.ACTION_MOVE, dgl);

        dragSource.addDragSourceListener(new DragSourceListener() {
            
            public void dropActionChanged(DragSourceDragEvent dsde) {
                // TODO Auto-generated method stub
                
            }
            
            public void dragOver(DragSourceDragEvent dsde) {
                // TODO Auto-generated method stub
                
            }
            
            public void dragExit(DragSourceEvent dse) {
                // TODO Auto-generated method stub
                
            }
            
            public void dragEnter(DragSourceDragEvent dsde) {
                // TODO Auto-generated method stub
                
            }
            
            public void dragDropEnd(DragSourceDropEvent dsde) {
                if (!dsde.getDropSuccess() && _tbvDragOrigRow != null) {
                    // drag did not suceed -> restore original position
                    Job job = _draggedJobs.get(0);
                    job.setBegin(_tbvDragOrigBegin);
                    job.setEnd(_tbvDragOrigEnd);
                    _tbvDragOrigRow.addInterval(job);
                    _tbvDragOrigRow = null;
                } 
                _tbv.setGhostIntervals(null, null);
            }
        });
        
        
    }

    class TimeBarViewerDragGestureListener implements DragGestureListener {
        public void dragGestureRecognized(DragGestureEvent e) {
            Component c = e.getComponent();
            System.out.println("component " + c);
            System.out.println(e.getDragOrigin());

            boolean markerDragging = _tbv.getDelegate().isMarkerDraggingInProgress();
            if (markerDragging) {
                return;
            }

            List<Interval> intervals = _tbv.getDelegate().getIntervalsAt(e.getDragOrigin().x, e.getDragOrigin().y);
            if (intervals.size() > 0 && e.getTriggerEvent().isAltDown()) {
                Interval interval = intervals.get(0);
                e.startDrag(null, new StringSelection("Drag " + ((Job) interval).getName()));
                _draggedJobs = new ArrayList<Job>();
                _draggedJobs.add((Job)interval);
                _draggedJobsOffsets = new ArrayList<Integer>();
                _draggedJobsOffsets.add(0);
                TimeBarRow row = _model.getRowForInterval(interval);
                ((DefaultTimeBarRowModel)row).remInterval(interval);
                
                // save orig data
                _tbvDragOrigRow = (DefaultTimeBarRowModel)row;
                _tbvDragOrigBegin = interval.getBegin().copy();
                _tbvDragOrigEnd = interval.getEnd().copy();
                
                return;
            }
//            Point origin = e.getDragOrigin();
//            if (_tbv.getDelegate().getYAxisRect().contains(origin)) {
//                TimeBarRow row = _tbv.getRowForXY(origin.x, origin.y);
//                if (row != null) {
//                    e.startDrag(null, new StringSelection("Drag ROW " + row));
//                }
//            }

        }
    }
    
    
    
    /**
     * Correct the dates of the dragged jobs and do a simple forward scheduling.
     * 
     * @param draggedJobs
     * @param curDate
     */
    private void correctAndScheduleJobs(List<Job> draggedJobs, JaretDate curDate) {
        for (int i = 0; i < draggedJobs.size(); i++) {
            Interval interval = draggedJobs.get(i);
            int secs = interval.getSeconds();
            interval.setBegin(curDate.copy());
            interval.setEnd(curDate.copy().advanceSeconds(secs));
            curDate = interval.getEnd().copy();
        }
    }

    /**
     * Check that none of the dragged jobs overlaps with another interval in the row. Brute force approach.
     * 
     * @param draggedJobs
     * @param row
     * @return
     */
    private boolean dropAllowed(List<Job> draggedJobs, TimeBarRow row) {
        for (Job job : draggedJobs) {
            for (Interval interval : row.getIntervals()) {
                if (job.intersects(interval)) {
                    return false;
                }
            }
        }

        return true;
    }

    class JobTableDragGestureListener implements DragGestureListener {
        public void dragGestureRecognized(DragGestureEvent e) {
            Component c = e.getComponent();
            System.out.println("component " + c);
            System.out.println(e.getDragOrigin());

            _draggedJobs = new ArrayList<Job>();
            _draggedJobsOffsets = new ArrayList<Integer>();
            int[] indizes = _jobTable.getSelectedRows();
            if (indizes.length > 0) {
                for (int i : indizes) {
                    _draggedJobs.add(_jobTableModel.getJob(i));
                    _draggedJobsOffsets.add(0);
                }
                e.startDrag(null, new StringSelection("Drag " + indizes.length + " intervals"));
            }
        }
    }

    private JPanel createControlPanel(int initialSeconds) {
        JPanel panel = new JPanel();
        // simple layout
        panel.setLayout(new FlowLayout());

        // unschedule button
        final JButton unscheduleButton = new JButton("Unschedule");
        unscheduleButton.setEnabled(false);
        _tbv.getSelectionModel().addTimeBarSelectionListener(new TimeBarSelectionListener() {
            
            public void selectionChanged(TimeBarSelectionModel selectionModel) {
                unscheduleButton.setEnabled(selectionModel.hasIntervalSelection()); 
            }
            
            public void elementRemovedFromSelection(TimeBarSelectionModel selectionModel, Object element) {
                unscheduleButton.setEnabled(selectionModel.hasIntervalSelection()); 
            }
            
            public void elementAddedToSelection(TimeBarSelectionModel selectionModel, Object element) {
                unscheduleButton.setEnabled(selectionModel.hasIntervalSelection()); 
            }
        });
        unscheduleButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent arg0) {
                unscheduleSelected();
            }
        });
        panel.add(unscheduleButton);
        
        
        /// scaling slider
        final double min = 1; // minimum value for seconds displayed
        final double max = 3 * 365 * 24 * 60 * 60; // max nummber of seconds displayed (3 years in seconds)
        final double slidermax = 1000; // slider maximum (does not really matter)
        final JSlider _timeScaleSlider = new JSlider(0, (int) slidermax);

        _timeScaleSlider.setPreferredSize(new Dimension(_timeScaleSlider.getPreferredSize().width * 4, _timeScaleSlider
                .getPreferredSize().height));
        panel.add(_timeScaleSlider);

        final double b = 1.0 / 100.0; // additional factor to reduce the absolut values in the exponent
        final double faktor = (min - max) / (1 - Math.pow(2, slidermax * b)); // factor for the exp function
        final double c = (min - faktor);

        int initialSliderVal = calcInitialSliderVal(c, b, faktor, initialSeconds);
        _timeScaleSlider.setValue((int) (slidermax-initialSliderVal));

        _timeScaleSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                final double x = slidermax - (double) _timeScaleSlider.getValue(); // reverse x
                double seconds = c + faktor * Math.pow(2, x * b); // calculate the seconds to display
                _tbv.setSecondsDisplayed((int) seconds, true);
            }
        });

        return panel;
    }

    private int calcInitialSliderVal(double c, double b, double faktor, int seconds) {

        double x = 1 / b * log2((seconds - c) / faktor);

        return (int) x;
    }

    private double log2(double a) {
        return Math.log(a) / Math.log(2);
    }

    protected ScheduleTimeBarModel createTBVModel() {
        ScheduleTimeBarModel model = new ScheduleTimeBarModel();

        for (int i = 0; i < 20; i++) {
            model.addRow("WorkCenter #" + i);
        }

        return model;
    }

    protected JobTableModel createJobTableModel() {
        JobTableModel model = new JobTableModel();

        for (int i = 0; i < 200; i++) {
            Job job = createRandomJob("Job #" + i);
            model.addJob(job);
        }

        return model;
    }

    protected Job createRandomJob(String name) {
        JaretDate startDate = new JaretDate();
        startDate.advanceMinutes(Math.random() * 24 * 60 * 10); // 10 days range
        int duration = (int) ((Math.random() * 7 + 1) * 60); // min 1h max 8h duration
        int priority = (int) (Math.random() * 4);
        Job job = new Job(name, startDate, duration, priority);
        return job;
    }

    private class PreventOverlapIntervalModificator extends DefaultIntervalModificator {

        @Override
        public boolean newBeginAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
            boolean result = true;
            for (Interval i : row.getIntervals()) {
                if (i != interval && i.contains(newBegin)) {
                    result = false;
                    break;
                }
            }

            return result;
        }

        @Override
        public boolean newEndAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
            boolean result = true;
            for (Interval i : row.getIntervals()) {
                if (i != interval && i.contains(newBegin)) {
                    result = false;
                    break;
                }
            }

            return result;
        }

        @Override
        public boolean shiftAllowed(TimeBarRow row, Interval interval, JaretDate newBegin) {
            boolean result = true;
            for (Interval i : row.getIntervals()) {
                if (i != interval && i.contains(newBegin)) {
                    result = false;
                    break;
                }
            }

            return result;
        }

        @Override
        public boolean isApplicable(TimeBarRow row, Interval interval) {
            return true;
        }

    }

}
