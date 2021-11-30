package main.repository;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

public class Repository
{
    private Document document;
    private String errorMsg;
    private Status status;

    public enum Status
    {
        GOOD,
        FILE_NOT_FOUND,
        PARSE_ERROR,
        PARSE_FATAL_ERROR
    }

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

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public Status getStatus()
    {
        return status;
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
        errorMsg = "";
        document = null;
        status = Status.GOOD;

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            validateDTD(builder);

            document = builder.parse(path);

        } catch (IOException e)
        {
            status = Status.FILE_NOT_FOUND;
        } catch (Exception e)
        {
            status = Status.PARSE_ERROR;
        }

        return status == Status.GOOD;
    }

    private void validateDTD(DocumentBuilder builder)
    {
        builder.setErrorHandler(new ErrorHandler()
        {
            @Override
            public void warning(SAXParseException e)
            {
                errorMsg += "[WARNING]\n";
                exceptionInfo(e);
                status = Status.PARSE_ERROR;
            }

            @Override
            public void error(SAXParseException e)
            {
                if (e.getMessage().contains("must match DOCTYPE root"))
                    return;

                if (e.getMessage().contains("no grammar found"))
                    return;

                exceptionInfo(e);
                status = Status.PARSE_ERROR;
            }

            @Override
            public void fatalError(SAXParseException e)
            {
                exceptionInfo(e);
                status = Status.PARSE_FATAL_ERROR;
            }

            private void exceptionInfo(SAXParseException e)
            {
                errorMsg += "    File   : " + e.getSystemId() + "\n";
                errorMsg += "    Line   : " + e.getLineNumber() + "\n";
                errorMsg += "    Column : " + e.getColumnNumber() + "\n";
                errorMsg += "    Message: " + e.getMessage() + "\n\n";
            }
        });
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
