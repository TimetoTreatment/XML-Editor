package main.repository;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringWriter;

public class Repository
{
    private Document document;

    Repository()
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
        format.setIndenting(true);
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
        document = null;

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(path);

        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // Singleton
    private static volatile Repository instance;

    public static Repository getInstance()
    {
        Repository result = instance;
        if (result != null)
            return result;

        synchronized (Repository.class)
        {
            if (instance == null)
                instance = new Repository();

            return instance;
        }
    }
}
