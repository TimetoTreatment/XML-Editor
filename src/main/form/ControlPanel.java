package main.form;

import main.controller.Controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ControlPanel
{
    Controller controller;

    ArrayList<JButton> buttonList;
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

    String currentPath;
    boolean isSaved = true;

    public ControlPanel(DisplayPanel displayPanel)
    {
        this.controller = Controller.getInstance(displayPanel, statusBar);

        buttonList = new ArrayList<>();
        buttonList.add(a1LoadButton);
        buttonList.add(a3FindButton);
        buttonList.add(a2MakeButton);
        buttonList.add(a4SaveButton);
        buttonList.add(a9ExitButton);
        buttonList.add(a5PrintButton);
        buttonList.add(a6InsertButton);
        buttonList.add(a7UpdateButton);
        buttonList.add(a8DeleteButton);

        for (var button : buttonList)
        {
            button.setForeground(Color.darkGray);
            button.setBackground(Color.white);
            button.setBorder(new LineBorder(Color.black));
            button.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
            button.setFocusPainted(false);
        }

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
        statusBar.setBorder(new EmptyBorder(0, 0, 0, 10));
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
}
