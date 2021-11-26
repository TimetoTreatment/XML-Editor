package main.form;

import java.awt.*;
import javax.swing.*;

public class ApplicationFrame extends JFrame
{
    private DisplayPanel displayPanel;
    private JPanel controlPanel;

    public ApplicationFrame()
    {
        displayPanel = new DisplayPanel();
        controlPanel = new ControlPanel(displayPanel).mainPanel;

        displayPanel.setText("XML Programming Project");

        setTitle("XML Programming");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(480, 640);
        displayPanel.setMaximumSize(new Dimension(480, 640 / 3));
        controlPanel.setMaximumSize(new Dimension(480, 640 * 2 / 3));

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        c.weighty = 0.7;
        c.gridy = 0;
        getContentPane().add(displayPanel, c);

        c.weighty = 0.3;
        c.gridy = 1;
        getContentPane().add(controlPanel, c);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
