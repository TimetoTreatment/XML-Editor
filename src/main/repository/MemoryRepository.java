package main.repository;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringWriter;

public class MemoryRepository
{
    private Document document = null;

    MemoryRepository()
    {

    }

    public Document getDocument()
    {
        return document;
    }

    public void setDocument(Document document)
    {
        this.document = document;
    }

    public void save(String path)
    {
        OutputFormat format = new OutputFormat(document);
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut, format);
        try
        {
            serial.asDOMSerializer();
            serial.serialize(document);
            FileWriter fw = new FileWriter(path);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(stringOut.toString());
            bw.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean load(String path)
    {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            factory.setValidating(true);

            document = builder.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(document == null)
            return false;

        return true;
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
