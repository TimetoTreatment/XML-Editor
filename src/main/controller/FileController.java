package main.controller;

import main.domain.CPU;
import main.domain.Part;
import main.domain.VGA;
import main.repository.MemoryRepository;
import main.form.DisplayPanel;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Stack;

public class FileController
{
    private MemoryRepository memoryRepository;
    private DisplayPanel displayPanel;
    private JLabel statusBar;

    private String plainText;
    private DefaultMutableTreeNode treeRoot;
    private DefaultTreeModel treeModel;

    private FileController(DisplayPanel displayPanel, JLabel statusBar)
    {
        this.memoryRepository = MemoryRepository.getInstance();
        this.displayPanel = displayPanel;
        this.statusBar = statusBar;

        treeRoot = displayPanel.getEditModeTreeRoot();
        treeModel = displayPanel.getEditModeTreeModel();
    }

    public boolean load(String path)
    {
        if (memoryRepository.load(path) == false)
            return false;

        print();

        statusBar.setText("\"" + path + "\" is loaded.");
        statusBar.setForeground(Color.black);

        displayPanel.setEditModeEnable(true);

        treeModel.reload();

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

        displayPanel.setViewModeText("Menu 4: Save \"" + newPath + "\"");
        return true;
    }

    public void print()
    {
        traverse();

        displayPanel.setViewModeText(plainText);
    }

    public void traverse()
    {
        plainText = createPlainText();
        createTree();
    }

    public String createPlainText()
    {
        String uri = memoryRepository.getDocument().getDocumentURI();

        Stack<Node> nodeStack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();

        nodeStack.push(memoryRepository.getDocument().getDocumentElement());
        indentStack.push(0);

        String contents = "<h1 style=\"text-align:center\"> :: " + uri.substring(uri.lastIndexOf('/') + 1, uri.length()) + " ::</h1><br/>";

        while (!nodeStack.empty())
        {
            Node node = nodeStack.pop();
            Integer indent = indentStack.pop();
            NodeList childNodes = node.getChildNodes();
            NamedNodeMap attributes = node.getAttributes();

            String blank = "";

            for (int i = 0; i < indent; i++)
                blank += "&nbsp;";

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
                case Node.TEXT_NODE:
                    contents += blank + node.getNodeValue();
                    break;

                case Node.COMMENT_NODE:
                    contents += "<font color=\"green\">" + blank + node.getNodeValue() + "</font>";
                    break;

                case Node.ATTRIBUTE_NODE:
                    contents += "<font color=\"#0078FF\">" + blank + node.getNodeName() + "</font><font color=\"#A8A8A8\"> = </font><font color=\"black\">" + node.getNodeValue() + "</font>";
                    break;
            }

            contents += "<br/>";

            if (node.getNodeType() != Node.ATTRIBUTE_NODE)
            {
                Node subNode;

                for (int i = childNodes.getLength() - 1; i >= 0; i--)
                {
                    subNode = childNodes.item(i);

                    if ((subNode.getNodeType() == Node.ATTRIBUTE_NODE || subNode.getNodeType() == Node.TEXT_NODE) && (subNode.getNodeValue().isBlank()))
                        continue;

                    nodeStack.push(subNode);
                    indentStack.push(indent + 4);
                }

                if (attributes != null)
                {
                    for (int i = attributes.getLength() - 1; i >= 0; i--)
                    {
                        subNode = attributes.item(i);

                        if ((subNode.getNodeType() == Node.ATTRIBUTE_NODE || subNode.getNodeType() == Node.TEXT_NODE) && (subNode.getNodeValue().isBlank()))
                            continue;

                        nodeStack.push(subNode);
                        indentStack.push(indent + 4);
                    }
                }
            }
        }

        return contents;
    }

    public boolean createTree()
    {
        String uri = memoryRepository.getDocument().getDocumentURI();

        Stack<Node> nodeStack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();
        Stack<DefaultMutableTreeNode> treeNodeStack = new Stack<>();

        nodeStack.push(memoryRepository.getDocument().getDocumentElement());
        indentStack.push(0);

        treeNodeStack.push(treeRoot);

        DefaultMutableTreeNode treeNewNode;

        while (!nodeStack.empty())
        {
            Node node = nodeStack.pop();
            Integer indent = indentStack.pop();
            NodeList childNodes = node.getChildNodes();
            NamedNodeMap attributes = node.getAttributes();

            DefaultMutableTreeNode treeNode = treeNodeStack.pop();

            switch (node.getNodeType())
            {
                case Node.DOCUMENT_NODE:
                case Node.ENTITY_NODE:
                case Node.ELEMENT_NODE:
                case Node.ENTITY_REFERENCE_NODE:
                    treeNode.setUserObject(node.getNodeName());
                    break;

                case Node.CDATA_SECTION_NODE:
                case Node.TEXT_NODE:

                case Node.COMMENT_NODE:
                    treeNode.setUserObject(node.getNodeValue());
                    break;

                case Node.ATTRIBUTE_NODE:
                    treeNode.setUserObject(node.getNodeName() + " = " + node.getNodeValue());

                    break;
            }

            if (node.getNodeType() != Node.ATTRIBUTE_NODE)
            {
                Node subNode;

                if (attributes != null)
                {
                    for (int i = 0; i < attributes.getLength(); i++)
                    {
                        subNode = attributes.item(i);

                        if ((subNode.getNodeType() == Node.ATTRIBUTE_NODE || subNode.getNodeType() == Node.TEXT_NODE) && (subNode.getNodeValue().isBlank()))
                            continue;

                        nodeStack.push(subNode);
                        indentStack.push(indent + 4);

                        treeNewNode = new DefaultMutableTreeNode();
                        treeNodeStack.push(treeNewNode);
                        treeNode.add(treeNewNode);
                    }
                }

                for (int i = 0; i < childNodes.getLength(); i++)
                {
                    subNode = childNodes.item(i);

                    if ((subNode.getNodeType() == Node.ATTRIBUTE_NODE || subNode.getNodeType() == Node.TEXT_NODE) && (subNode.getNodeValue().isBlank()))
                        continue;

                    nodeStack.push(subNode);
                    indentStack.push(indent + 4);

                    treeNewNode = new DefaultMutableTreeNode();
                    treeNodeStack.push(treeNewNode);
                    treeNode.add(treeNewNode);
                }
            }
        }

        return true;
    }


    public boolean insert(String str)
    {
        displayPanel.setViewModeText("The \"" + str + "\" insertion is complete.");
        return true;
    }

    public boolean update(String str)
    {
        displayPanel.setViewModeText("The \"" + str + "\" update is complete.");
        return true;
    }

    public boolean delete(String str)
    {
        displayPanel.setViewModeText("The \"" + str + "\" deletion is complete.");
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
