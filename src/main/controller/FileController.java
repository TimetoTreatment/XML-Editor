package main.controller;

import main.domain.CPU;
import main.domain.Part;
import main.domain.VGA;
import main.repository.MemoryRepository;
import main.form.DisplayPanel;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.util.Stack;

public class FileController
{
    MemoryRepository memoryRepository;
    DisplayPanel displayPanel;
    JLabel statusBar;

    private FileController(DisplayPanel displayPanel, JLabel statusBar)
    {
        this.memoryRepository = MemoryRepository.getInstance();
        this.displayPanel = displayPanel;
        this.statusBar = statusBar;
    }

    public boolean load(String path)
    {
        memoryRepository.load(path);

        print();

        statusBar.setText("\"" + path + "\" is loaded.");
        statusBar.setForeground(Color.black);

        return true;
    }

    public boolean make(String filename)
    {
        Document doc = memoryRepository.getDocument();

        if (doc == null)
        {
            doc = new DocumentImpl();
            memoryRepository.setDocument(doc);
        }

        Element root = doc.createElement("Parts");

        root.appendChild(nodeFactory(doc, new VGA("GeForce RTX 3090", "NVIDIA", "GeForce RTX 30", "2020-09-24", "1499", "USD", "10496", "24", "GDDR6X", "350", "3")));
        root.appendChild(nodeFactory(doc, new VGA("GeForce RTX 3080", "NVIDIA", "GeForce RTX 30", "2020-09-17", "699000", "KRW", "8704", "10", "GDDR6X", "320", "2")));

        doc.appendChild(root);

        return true;
    }

    public boolean save(String newPath)
    {
        memoryRepository.save(newPath);

        displayPanel.setText("Menu 4: Save \"" + newPath + "\"");
        return true;
    }

    public boolean print()
    {
        String uri = memoryRepository.getDocument().getDocumentURI();

        String contents = "<h1 style=\"text-align:center\"> :: " + uri.substring(uri.lastIndexOf('/') + 1, uri.length()) + " ::</h1><br/>";
        Stack<Node> nodeStack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();

        nodeStack.push(memoryRepository.getDocument().getDocumentElement());
        indentStack.push(0);

        while (!nodeStack.empty())
        {
            Node node = nodeStack.pop();
            Integer indent = indentStack.pop();
            NodeList childNodes = node.getChildNodes();
            NamedNodeMap attributes = node.getAttributes();

            String blank = "";

            for (int i = 0; i < indent; i++)
                blank += "&nbsp;";

            boolean continueFlag = false;

            switch (node.getNodeType())
            {
                case Node.DOCUMENT_NODE:
                case Node.ENTITY_NODE:
                    contents += blank + node.getNodeName();
                    break;

                case Node.ELEMENT_NODE:
                case Node.ENTITY_REFERENCE_NODE:
                    contents += "<font color=\"blue\">" + blank + node.getNodeName() + "</font>";
                    break;

                case Node.CDATA_SECTION_NODE:
                    contents += blank + node.getNodeValue();
                    break;

                case Node.COMMENT_NODE:
                    contents += "<font color=\"green\">" + blank + node.getNodeValue() + "</font>";
                    break;

                case Node.TEXT_NODE:
                    if (node.getNodeValue().isBlank() == true)
                    {
                        continueFlag = true;
                        break;
                    }
                    contents += blank + node.getNodeValue();
                    break;

                case Node.ATTRIBUTE_NODE:
                    if (node.getNodeValue().isBlank() == true)
                    {
                        continueFlag = true;
                        break;
                    }
                    contents += "<font color=\"#0078FF\">" + blank + node.getNodeName() + "</font><font color=\"#A8A8A8\"> = </font><font color=\"black\">" + node.getNodeValue() + "</font>";
                    break;
            }

            if (continueFlag == true)
                continue;

            contents += "<br/>";

            if (node.getNodeType() != Node.ATTRIBUTE_NODE)
            {
                for (int i = childNodes.getLength() - 1; i >= 0; i--)
                {
                    nodeStack.push(childNodes.item(i));
                    indentStack.push(indent + 4);
                }

                if (attributes != null)
                {
                    for (int i = attributes.getLength() - 1; i >= 0; i--)
                    {
                        nodeStack.push(attributes.item(i));
                        indentStack.push(indent + 4);
                    }
                }
            }
        }

        displayPanel.setText(contents);

        return true;
    }

    public boolean insert(String str)
    {
        displayPanel.setText("The \"" + str + "\" insertion is complete.");
        return true;
    }

    public boolean update(String str)
    {
        displayPanel.setText("The \"" + str + "\" update is complete.");
        return true;
    }

    public boolean delete(String str)
    {
        displayPanel.setText("The \"" + str + "\" deletion is complete.");
        return true;
    }

    private Node nodeFactory(Document doc, Part part)
    {
        Element item = doc.createElement("VGA:VGA");
        Element specs = doc.createElement("VGA:specs");
        Element element;

        element = doc.createElement("part:name");
        element.setTextContent(part.name);
        item.appendChild(element);

        element = doc.createElement("part:manufacturer");
        element.setTextContent(part.manufacturer);
        item.appendChild(element);

        element = doc.createElement("part:series");
        element.setTextContent(part.series);
        item.appendChild(element);

        element = doc.createElement("part:launch_date");
        element.setTextContent(part.launch_date);
        item.appendChild(element);

        element = doc.createElement("part:msrp");
        element.setTextContent(part.msrp);
        item.appendChild(element);

        if (part instanceof VGA)
        {
            VGA partVGA = (VGA) part;
            element = doc.createElement("VGA:core_num");
            element.setTextContent(partVGA.core_num);
            specs.appendChild(element);

            element = doc.createElement("VGA:memory_size");
            element.setTextContent(partVGA.memory_size);
            specs.appendChild(element);

            element = doc.createElement("VGA:power_consumption");
            element.setTextContent(partVGA.power_consumption);
            specs.appendChild(element);

            element = doc.createElement("VGA:slot_size");
            element.setTextContent(partVGA.slot_size);
            specs.appendChild(element);
        }
        else if (part instanceof CPU)
        {

        }

        item.appendChild(specs);

        return item;
    }

    // Singleton
    private static volatile FileController instance;

    public static FileController getInstance(DisplayPanel displayPanel, JLabel statusBar)
    {
        FileController result = instance;
        if (result != null)
            return result;

        synchronized (FileController.class)
        {
            if (instance == null)
                instance = new FileController(displayPanel, statusBar);

            return instance;
        }
    }
}
