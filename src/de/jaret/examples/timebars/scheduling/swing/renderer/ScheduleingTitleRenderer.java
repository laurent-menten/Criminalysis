package de.jaret.examples.timebars.scheduling.swing.renderer;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import de.jaret.util.ui.timebars.swing.renderer.ITitleRenderer;

public class ScheduleingTitleRenderer implements ITitleRenderer{
    JPanel _component;
    
    public ScheduleingTitleRenderer() {
        _component = new JPanel();
        _component.setLayout(new BorderLayout());
        JButton b = new JButton("Button");
        _component.add(b, BorderLayout.NORTH);
        JComboBox box = new JComboBox(new String[]{"first", "second", "third"});
        _component.add(box, BorderLayout.SOUTH);
    }
    
    public JComponent getTitleRendererComponent(TimeBarViewer tbv) {
        return _component;
    }

}
