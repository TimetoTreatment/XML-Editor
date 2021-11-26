package main.controller;

import main.domain.MemoryRepository;
import main.form.DisplayPanel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileController
{
    MemoryRepository memoryRepository;
    DisplayPanel displayPanel;

    private FileController(DisplayPanel displayPanel)
    {
        this.memoryRepository = MemoryRepository.getInstance();
        this.displayPanel = displayPanel;
    }

    private void printMsg(String message)
    {
        displayPanel.setText(message);
        System.out.println(message);
    }

    public boolean load(String path)
    {
        File file = new File(path);

        FileReader fr;
        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            printMsg("Cannot find \"" + path + "\".");
            return false;
        }

        // START: Save as string
        String contents = "";
        BufferedReader br = new BufferedReader(fr);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                contents += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        memoryRepository.setContents(contents);
        // END: Save as string

        memoryRepository.setFile(file);
        printMsg("\"" + path + "\" is loaded.\n\n" + memoryRepository.getContents());
        return true;
    }

    public boolean make(String path)
    {
        File file = new File(path);

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        memoryRepository.setContents("");
        printMsg("\"" + path + "\" is created.");
        return true;
    }

    public boolean find(String str)
    {
        printMsg("\"" + str + "\" is finding some keywords.");
        return true;
    }

    public boolean save(String newPath)
    {
        try {
            Files.copy(memoryRepository.getFile().toPath(), (new File(newPath)).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        printMsg("Menu 4: Save \"" + newPath + "\"");
        return true;
    }

    public boolean print()
    {
        printMsg("Menu 5: Print\n\n" + memoryRepository.getContents());
        return true;
    }

    public boolean insert(String str)
    {
        printMsg("The \"" + str + "\" insertion is complete.");
        return true;
    }

    public boolean update(String str)
    {
        printMsg("The \"" + str + "\" update is complete.");
        return true;
    }

    public boolean delete(String str)
    {
        printMsg("The \"" + str + "\" deletion is complete.");
        return true;
    }

    // Singleton
    private static volatile FileController instance;

    public static FileController getInstance(DisplayPanel printMsg)
    {
        FileController result = instance;
        if (result != null) {
            return result;
        }
        synchronized (FileController.class) {
            if (instance == null) {
                instance = new FileController(printMsg);
            }
            return instance;
        }
    }
}
