package de.jaret.examples.timebars.scheduling.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.jaret.examples.timebars.events.swing.SwingEventExample;

public class SwingSchedulingExample {

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame f = new JFrame(SwingEventExample.class.getName());
        f.setSize(1200, 800);
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        SchedulingPanel panel = new SchedulingPanel();
        f.getContentPane().add(panel, BorderLayout.CENTER);
        
        f.setVisible(true);
        
    }

}
