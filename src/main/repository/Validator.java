package main.repository;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;

public class Validator
{
    public enum ErrorType
    {
        VALIDATE,
        ERROR,
        FATAL_ERROR,
        XSD_NOT_FOUND
    }

    private static ErrorType errorType;
    private static ArrayList<String> errorMsgList = new ArrayList<>();

    public static boolean isValidateDTD(Repository repository)
    {
        errorMsgList.clear();
        errorType = ErrorType.VALIDATE;

        String tempPath = "ValidationTempDTD" + (int) (1000 * Math.random());

        repository.save(tempPath);

        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new CustomErrorHandler("DTD"));
            builder.parse(tempPath);

        } catch (Exception e)
        {
            System.out.println("[DOM]\n");
            System.out.println("Parse Error\n\n");
        }

        new File(tempPath).delete();

        return errorType == ErrorType.VALIDATE;
    }

    public static boolean isValidateXSD(Repository repository)
    {
        errorMsgList.clear();
        errorType = ErrorType.VALIDATE;

        Document doc = repository.getDocument();
        NamedNodeMap attributes = doc.getDocumentElement().getAttributes();

        if (attributes == null)
        {
            errorType = ErrorType.XSD_NOT_FOUND;
            return false;
        }

        Node schemaLocationNode = attributes.getNamedItem("xsi:schemaLocation");

        if (schemaLocationNode == null)
        {
            errorType = ErrorType.XSD_NOT_FOUND;
            return false;
        }

        String location = schemaLocationNode.getNodeValue();

        if (location == null || !location.contains(" "))
        {
            errorType = ErrorType.XSD_NOT_FOUND;
            return false;
        }

        String xmlPath;
        String xsdPath;

        xsdPath = location.substring(location.indexOf(' ') + 1);
        xmlPath = "ValidationTempXSD" + (int) (1000 * Math.random());
        repository.save(xmlPath);

        try
        {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.setErrorHandler(new CustomErrorHandler("XSD"));
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (Exception e)
        {
            System.out.println("[XSD]\n");
            System.out.println("Parse Error\n\n");
        }

        new File(xmlPath).delete();

        return errorType == ErrorType.VALIDATE;
    }

    private static class CustomErrorHandler implements ErrorHandler
    {
        String parserType;

        CustomErrorHandler(String parserType)
        {
            this.parserType = parserType;
        }

        @Override
        public void warning(SAXParseException e)
        {
            errorMsgList.add(exceptionInfo(e, parserType, "WARNING"));
            errorType = ErrorType.ERROR;
        }

        @Override
        public void error(SAXParseException e)
        {
            if (e.getMessage().contains("must match DOCTYPE root"))
                return;

            if (e.getMessage().contains("no grammar found"))
                return;

            errorMsgList.add(exceptionInfo(e, parserType, "ERROR"));
            errorType = ErrorType.ERROR;
        }

        @Override
        public void fatalError(SAXParseException e)
        {
            if (e.getMessage().contains("must match DOCTYPE root"))
                return;

            if (e.getMessage().contains("no grammar found"))
                return;

            errorMsgList.add(exceptionInfo(e, parserType, "FATAL_ERROR"));
            errorType = ErrorType.FATAL_ERROR;
        }

        private String exceptionInfo(SAXParseException e, String parserType, String errorType)
        {
            String errorMsg = "";
            errorMsg += "    " + e.getMessage() + "\n";
            errorMsg += "    Line   : " + e.getLineNumber() + "\n";
            errorMsg += "    Column : " + e.getColumnNumber() + "\n";

            return errorMsg;
        }
    }

    public static ArrayList<String> getErrorMsgList()
    {
        return errorMsgList;
    }

    public static ErrorType getErrorType()
    {
        return errorType;
    }
}
