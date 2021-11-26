package main.domain;

import java.io.*;
import java.util.ArrayList;

public class MemoryRepository
{
    private File file;
    private String contents;

    public File getFile()
    {
        return file;
    }

    public void setFile(File file)
    {
        this.file = file;
    }

    public String getContents()
    {
        return contents;
    }

    public void setContents(String contents)
    {
        this.contents = contents;
    }

    // Singleton
    private static volatile MemoryRepository instance;

    public static MemoryRepository getInstance()
    {
        MemoryRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (MemoryRepository.class) {
            if (instance == null) {
                instance = new MemoryRepository();
            }
            return instance;
        }
    }
}
