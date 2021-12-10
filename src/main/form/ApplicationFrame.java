package main.form;

import java.awt.*;
import javax.swing.*;

public class ApplicationFrame extends JFrame
{
    public final DisplayForm displayForm;
    public final ControlForm controlForm;

    private final int width = 750;
    private final int height = 1000;

    public ApplicationFrame()
    {
        displayForm = new DisplayForm();
        controlForm = new ControlForm();

        displayForm.setViewModeText(Tutorial.getText());

        setTitle("XML Programming");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setSize(width, height);

        displayForm.setPreferredSize(new Dimension(width, height * 2 / 3));
        controlForm.setPreferredSize(new Dimension(width, height / 3));

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;

        c.weighty = 0.67;
        c.gridy = 0;
        getContentPane().add(displayForm, c);

        c.weighty = 0.33;
        c.gridy = 1;
        getContentPane().add(controlForm.mainPanel, c);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
