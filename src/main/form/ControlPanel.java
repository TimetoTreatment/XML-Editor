package main.form;

import main.controller.Controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ControlPanel
{
    Controller controller;

    ArrayList<JButton> buttons = new ArrayList<>();
    ArrayList<JButton> editButtons = new ArrayList<>();
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
    private DisplayPanel displayPanel;

    String currentPath;
    boolean isSaved = true;

    public ControlPanel(DisplayPanel displayPanel)
    {
        this.controller = Controller.getInstance(displayPanel, statusBar);

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

        addKeyBind(a1LoadButton, "1", "LOAD", load);
        addKeyBind(a2MakeButton, "2", "MAKE", make);
        addKeyBind(a3FindButton, "3", "FIND", find);
        addKeyBind(a4SaveButton, "4", "SAVE", save);
        addKeyBind(a5PrintButton, "5", "PRINT", print);
        addKeyBind(a6InsertButton, "6", "INSERT", insert);
        addKeyBind(a7UpdateButton, "7", "UPDATE", update);
        addKeyBind(a8DeleteButton, "8", "DELETE", delete);
        addKeyBind(a9ExitButton, "9", "EXIT", exit);

        statusBar.setForeground(Color.red);
        statusBar.setText("File not loaded");
        statusBar.setBorder(new EmptyBorder(0, 22, 5, 0));

        displayPanel.tabbedPane.addChangeListener(e -> {
            if(displayPanel.tabbedPane.getSelectedIndex()==1)
                setStatus(Status.EDITABLE);
            else
                setStatus(Status.LOADED);
        });
    }

    private void addKeyBind(JButton button, String key, String mapKey, Action action)
    {
        button.addActionListener(action);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), mapKey);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD" + key), mapKey);
        button.getActionMap().put(mapKey, action);
    }

    Action load = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String path = JOptionPane.showInputDialog(null, "Enter the file path", "1. Load", JOptionPane.PLAIN_MESSAGE);

            if (path == null)
                return;

            if (controller.load(path))
                currentPath = path;

            setStatus(Status.LOADED);

            isSaved = true;
        }
    };

    Action make = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Object[] options = {
                    "VGA and CPU",
                    "Only VGA",
                    "Only CPU",
                    "Empty file"};
            int iResult = JOptionPane.showOptionDialog(null,
                    "Select type(s) to include.",
                    "2. Make",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (iResult == -1)
                return;

            if (iResult == 0)
                controller.make(true, true);
            else if (iResult == 1)
                controller.make(true, false);
            else if (iResult == 2)
                controller.make(false, true);
            else if (iResult == 3)
                controller.make(false, false);

            setStatus(Status.LOADED);

            isSaved = false;
        }
    };

    Action find = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String str = JOptionPane.showInputDialog(null, "Enter the keyword", "3. Find", JOptionPane.PLAIN_MESSAGE);

            if (str == null)
                return;

        }
    };

    Action save = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String newPath = JOptionPane.showInputDialog(null, "Enter the filename", "4. Save", JOptionPane.PLAIN_MESSAGE);

            if (newPath == null)
                return;

            controller.save(newPath);
            currentPath = newPath;

            isSaved = true;
        }
    };

    Action print = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            controller.print();
        }
    };

    Action insert = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String str = JOptionPane.showInputDialog(null, "Enter the keyword", "6. Insert", JOptionPane.PLAIN_MESSAGE);

            if (str == null)
                return;

            controller.insert(str);

            isSaved = false;
        }
    };

    Action update = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String str = JOptionPane.showInputDialog(null, "Enter the keyword", "7. Update", JOptionPane.PLAIN_MESSAGE);

            if (str == null)
                return;

            controller.update(str);

            isSaved = false;
        }
    };

    Action delete = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            String str = JOptionPane.showInputDialog(null, "Enter the keyword", "8. Delete", JOptionPane.PLAIN_MESSAGE);

            if (str == null)
                return;

            controller.delete(str);
            isSaved = false;
        }
    };

    Action exit = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (isSaved == false)
            {
                int iResult = JOptionPane.showConfirmDialog(null, "Do you want to save the changes?", "9. Exit", JOptionPane.YES_NO_CANCEL_OPTION);

                if (iResult != JOptionPane.OK_OPTION && iResult != JOptionPane.NO_OPTION)
                    return;

                if (iResult == JOptionPane.OK_OPTION)
                {
                    while (currentPath == null || currentPath.isBlank())
                        currentPath = JOptionPane.showInputDialog(null, "Enter the file name");

                    controller.save(currentPath);
                }
            }

            System.exit(0);
        }
    };

    public enum Status
    {
        LOADED,
        EDITABLE,
    }

    public void setStatus(Status status)
    {
        switch (status)
        {
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
        }
    }
}
