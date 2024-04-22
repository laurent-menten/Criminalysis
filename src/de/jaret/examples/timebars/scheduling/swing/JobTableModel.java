package de.jaret.examples.timebars.scheduling.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.jaret.examples.timebars.scheduling.model.Job;

public class JobTableModel extends AbstractTableModel{

    private List<Job> _jobs = new ArrayList<Job>();
    
    public void addJob(Job job) {
        _jobs.add(job);
        fireTableRowsInserted(_jobs.size(), _jobs.size());
    }
    
    public Job getJob(int index) {
        return _jobs.get(index);
    }
    
    public void removeJob(Job job) {
        int index = _jobs.indexOf(job);
        if (index != -1) {
            _jobs.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public int getColumnCount() {
        return 5;
    }

    public int getRowCount() {
        return _jobs.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Job job = _jobs.get(rowIndex);
        
        switch (columnIndex) {
        case 0:
            return job.getName();
        case 1:
            return job.getPriority();
        case 2:
            return job.getBegin().toDisplayString();
        case 3:
            return job.getEnd().toDisplayString();
        case 4:
            return job.getSeconds()/60;
        default:
            break;
        }
        return null;
    }
    
    @Override
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Name";
        case 1:
            return "Priority";
        case 2:
            return "Begin";
        case 3:
            return "End";
        case 4:
            return "Minutes";
        default:
            break;
        }
        return null;
    }

}
