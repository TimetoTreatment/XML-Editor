package main.form;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;

public class ControlForm
{
    final ArrayList<JButton> buttons = new ArrayList<>();
    final ArrayList<JButton> editButtons = new ArrayList<>();
    public JPanel mainPanel;
    private JButton a1LoadButton;

    private JButton a2MakeButton;
    private JButton a3FindButton;
    private JButton a4SaveButton;
    private JButton a5PrintButton;
    private JButton a6InsertButton;
    private JButton a7UpdateButton;
    private JButton a8DeleteButton;
    private JButton a9ExitButton;
    private JLabel statusBar;

    public ControlForm()
    {
        buttons.add(a1LoadButton);
        buttons.add(a3FindButton);
        buttons.add(a2MakeButton);
        buttons.add(a4SaveButton);
        buttons.add(a9ExitButton);
        buttons.add(a5PrintButton);
        buttons.add(a6InsertButton);
        buttons.add(a7UpdateButton);
        buttons.add(a8DeleteButton);

        for (var button : buttons)
        {
            button.setForeground(Color.darkGray);
            button.setBackground(Color.white);
            button.setBorder(new LineBorder(Color.black));
            button.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            button.setFocusPainted(false);
        }

        editButtons.add(a3FindButton);
        editButtons.add(a6InsertButton);
        editButtons.add(a7UpdateButton);
        editButtons.add(a8DeleteButton);

        for (var button : editButtons)
            button.setEnabled(false);

        a4SaveButton.setEnabled(false);
        a5PrintButton.setEnabled(false);
        a9ExitButton.setForeground(new Color(192, 64, 64));

        statusBar.setBorder(new EmptyBorder(0, 22, 5, 0));
    }

    public void setPreferredSize(Dimension dimension)
    {
        mainPanel.setPreferredSize(dimension);
    }

    public enum Status
    {
        NOT_LOADED,
        LOADED,
        EDITABLE,
        EDITABLE_CANNOT_DELETE,
    }

    public void setStatus(Status status)
    {
        switch (status)
        {
            case NOT_LOADED -> {
                a4SaveButton.setEnabled(false);
                a5PrintButton.setEnabled(false);

                for (var button : editButtons)
                    button.setEnabled(false);
            }
            case LOADED -> {
                a4SaveButton.setEnabled(true);
                a5PrintButton.setEnabled(true);

                for (var button : editButtons)
                    button.setEnabled(false);
            }
            case EDITABLE -> {
                a4SaveButton.setEnabled(true);
                a5PrintButton.setEnabled(true);

                for (var button : editButtons)
                    button.setEnabled(true);
            }
            case EDITABLE_CANNOT_DELETE -> {
                a4SaveButton.setEnabled(true);
                a5PrintButton.setEnabled(true);

                for (var button : editButtons)
                    button.setEnabled(true);

                a8DeleteButton.setEnabled(false);
            }
        }
    }

    public JButton buttonLoad()
    {
        return a1LoadButton;
    }

    public JButton buttonMake()
    {
        return a2MakeButton;
    }

    public JButton buttonFind()
    {
        return a3FindButton;
    }

    public JButton buttonSave()
    {
        return a4SaveButton;
    }

    public JButton buttonPrint()
    {
        return a5PrintButton;
    }

    public JButton buttonInsert()
    {
        return a6InsertButton;
    }

    public JButton buttonUpdate()
    {
        return a7UpdateButton;
    }

    public JButton buttonDelete()
    {
        return a8DeleteButton;
    }

    public JButton buttonExit()
    {
        return a9ExitButton;
    }

    public JLabel getStatusBar()
    {
        return statusBar;
    }
}
