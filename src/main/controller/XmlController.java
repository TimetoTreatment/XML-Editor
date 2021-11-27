package main.controller;

import main.form.DisplayPanel;
import main.repository.MemoryRepository;

import javax.swing.*;

public class XmlController
{
    MemoryRepository memoryRepository;
    DisplayPanel displayPanel;
    JLabel statusBar;

    private XmlController(DisplayPanel displayPanel, JLabel statusBar)
    {
        this.memoryRepository = MemoryRepository.getInstance();
        this.displayPanel = displayPanel;
        this.statusBar = statusBar;
    }

    public boolean find(String str)
    {
        displayPanel.setViewModeText("\"" + str + "\" is finding some keywords.");
        return true;
    }

    // Singleton
    private static volatile XmlController instance;

    public static XmlController getInstance(DisplayPanel displayPanel, JLabel statusBar)
    {
        XmlController result = instance;
        if (result != null)
            return result;

        synchronized (XmlController.class)
        {
            if (instance == null)
                instance = new XmlController(displayPanel, statusBar);

            return instance;
        }
    }
}
