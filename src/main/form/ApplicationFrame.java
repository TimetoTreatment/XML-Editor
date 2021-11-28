package main.form;

import main.controller.Controller;

import java.awt.*;
import javax.swing.*;

public class ApplicationFrame extends JFrame
{
    private DisplayPanel displayPanel;
    private JPanel controlPanel;

    int width = 750;
    int height = 1000;

    public ApplicationFrame()
    {
        displayPanel = new DisplayPanel();
        controlPanel = new ControlPanel(displayPanel).mainPanel;

        displayPanel.setViewModeText(tutorialText());

        setTitle("XML Programming");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(width, height);

        displayPanel.setPreferredSize(new Dimension(width, height * 2 / 3));
        controlPanel.setPreferredSize(new Dimension(width, height / 3));

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        c.weighty = 0.67;
        c.gridy = 0;
        getContentPane().add(displayPanel, c);

        c.weighty = 0.33;
        c.gridy = 1;
        getContentPane().add(controlPanel, c);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String tutorialText()
    {
        String text = "";

        text += "<h1>XML Programming Project</h1><br/>";

        text += "<h2>How to Use</h2>";
        text += "&nbsp;&nbsp;1. Load or Make XML File.<br/>";
        text += "&nbsp;&nbsp;2. Click \"Edit Mode\" in tab.<br/>";
        text += "&nbsp;&nbsp;3. Edit XML DOM. <br/>";
        text += "&nbsp;&nbsp;4. Save and Exit. <br/><br/>";

        text += "<h2>Legend</h2>";
        text += "&nbsp;&nbsp;<font color=\"blue\"><b>[E]</b> Element <br/>";
        text += "&nbsp;&nbsp;<font color=\"#0078FF\"><b>[A]</b> Attribute <br/>";
        text += "&nbsp;&nbsp;<font color=\"green\"><b>[C]</b> Comment <br/>";
        text += "&nbsp;&nbsp;<font color=\"black\"><b>[T]</b> Text <br/>";

        return text;
    }
}
