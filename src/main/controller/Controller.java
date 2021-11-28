package main.controller;

import main.domain.CPU;
import main.domain.Part;
import main.domain.NodeContainer;
import main.domain.VGA;
import main.form.ControlPanel;
import main.repository.MemoryRepository;
import main.form.DisplayPanel;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Stack;

public class Controller
{
    private MemoryRepository memoryRepository;
    private DisplayPanel displayPanel;
    private JLabel statusBar;

    private String plainText;
    private DefaultMutableTreeNode treeRoot;

    private Controller(DisplayPanel displayPanel, JLabel statusBar)
    {
        this.memoryRepository = MemoryRepository.getInstance();
        this.displayPanel = displayPanel;
        this.statusBar = statusBar;

        treeRoot = displayPanel.getEditModeTreeRoot();
    }

    public boolean load(String path)
    {
        if (memoryRepository.load(path) == false)
            return false;

        print();

        statusBar.setText("\"" + path + "\" is loaded.");
        statusBar.setForeground(Color.black);

        displayPanel.setEditModeEnable(true);
        displayPanel.reload();

        return true;
    }

    public boolean make(boolean VGA, boolean CPU)
    {
        Document doc = new DocumentImpl();

        doc.setDocumentURI("New File");
        memoryRepository.setDocument(doc);

        statusBar.setText("\"New File\" is loaded.");
        statusBar.setForeground(Color.black);

        Element root = doc.createElement("Parts");

        Attr newAttribute;
        Comment newComment;

        newAttribute = doc.createAttribute("xmlns");
        newAttribute.setValue("http://PartManager.com");
        root.setAttributeNode(newAttribute);

        newAttribute = doc.createAttribute("xmlns:CPU");
        newAttribute.setValue("http://PartManager.com/parts/CPU");
        root.setAttributeNode(newAttribute);

        newAttribute = doc.createAttribute("xmlns:VGA");
        newAttribute.setValue("http://PartManager.com/parts/VGA");
        root.setAttributeNode(newAttribute);

        newAttribute = doc.createAttribute("xmlns:part");
        newAttribute.setValue("http://PartManager.com/part");
        root.setAttributeNode(newAttribute);

        newAttribute = doc.createAttribute("xmlns:xsi");
        newAttribute.setValue("http://www.w3.org/2001/XMLSchema-instance");
        root.setAttributeNode(newAttribute);

        newAttribute = doc.createAttribute("xsi:schemaLocation");
        newAttribute.setValue("http://PartManager.com PartManager.xsd");
        root.setAttributeNode(newAttribute);

        if (VGA == true)
        {
            newComment = doc.createComment("=================");
            root.appendChild(newComment);
            newComment = doc.createComment("== List of VGA ==");
            root.appendChild(newComment);
            newComment = doc.createComment("=================");
            root.appendChild(newComment);
            newComment = doc.createComment("NVIDIA, GeForce RTX 30 series");
            root.appendChild(newComment);

            root.appendChild(nodeFactory(doc, new VGA("GeForce RTX 3090", "NVIDIA", "GeForce RTX 30", "2020-09-24", "1499", "USD", "10496", "24", "GDDR6X", "350", "3")));
            root.appendChild(nodeFactory(doc, new VGA("GeForce RTX 3080", "NVIDIA", "GeForce RTX 30", "2020-09-17", "699000", "KRW", "8704", "10", "GDDR6X", "320", "2")));
        }

        if (CPU == true)
        {
            newComment = doc.createComment("=================");
            root.appendChild(newComment);
            newComment = doc.createComment("== List of CPU ==");
            root.appendChild(newComment);
            newComment = doc.createComment("=================");
            root.appendChild(newComment);
            newComment = doc.createComment("Intel, 9th Gen");
            root.appendChild(newComment);

            root.appendChild(nodeFactory(doc, new CPU("Core i3-9100", "Intel", "Core i3", "2018-10-19", "122", "USD", "4", "3.6", "65", "UHD 630")));
            root.appendChild(nodeFactory(doc, new CPU("Core i5-9600K", "Intel", "Core i5", "2018-10-19", "262", "USD", "6", "3.7", "95", "UHD 630")));
        }

        doc.appendChild(root);

        print();

        displayPanel.setEditModeEnable(true);
        displayPanel.reload();

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
                case Node.DOCUMENT_NODE, Node.ENTITY_NODE -> contents += blank + node.getNodeName();

                case Node.ELEMENT_NODE, Node.ENTITY_REFERENCE_NODE -> contents += "<font color=\"blue\">" + blank + node.getNodeName() + "</font>";

                case Node.CDATA_SECTION_NODE, Node.TEXT_NODE -> contents += blank + node.getNodeValue();

                case Node.COMMENT_NODE -> contents += "<font color=\"green\">" + blank + node.getNodeValue() + "</font>";

                case Node.ATTRIBUTE_NODE -> contents += "<font color=\"#0078FF\">" + blank + node.getNodeName() + "</font><font color=\"#A8A8A8\"> = </font><font color=\"black\">" + node.getNodeValue() + "</font>";
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
        Stack<Node> nodeStack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();
        Stack<DefaultMutableTreeNode> treeNodeStack = new Stack<>();

        nodeStack.push(memoryRepository.getDocument().getDocumentElement());
        indentStack.push(0);

        treeRoot.removeAllChildren();
        treeNodeStack.push(treeRoot);

        DefaultMutableTreeNode treeNewNode;

        while (!nodeStack.empty())
        {
            Node node = nodeStack.pop();
            Integer indent = indentStack.pop();
            NodeList childNodes = node.getChildNodes();
            NamedNodeMap attributes = node.getAttributes();

            DefaultMutableTreeNode treeNode = treeNodeStack.pop();

            treeNode.setUserObject(new NodeContainer(node));

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
        DefaultMutableTreeNode currentTreeNode = displayPanel.getSelectedNodeContainer();
        Node currentDomNode = ((NodeContainer) currentTreeNode.getUserObject()).domNode;
        Node newDomNode;

        if (currentDomNode.getNodeValue() == null)
        {
            if (str.indexOf(':') != -1)
                newDomNode = currentDomNode.getOwnerDocument().renameNode(currentDomNode, str.substring(0, str.indexOf(':')), str);
            else
                newDomNode = currentDomNode.getOwnerDocument().renameNode(currentDomNode, null, str);

            currentTreeNode.setUserObject(new NodeContainer(newDomNode));
        }
        else
            currentDomNode.setNodeValue(str);

        ((NodeContainer) currentTreeNode.getUserObject()).updateName();
        displayPanel.reload();

        return true;
    }

    public boolean delete(String str)
    {
        displayPanel.setViewModeText("The \"" + str + "\" deletion is complete.");

        return true;
    }

    private Node nodeFactory(Document doc, Part part)
    {
        Element item;
        Element specs;
        Element element;
        Attr attribute;

        if (part instanceof VGA)
        {
            item = doc.createElement("VGA:VGA");
            specs = doc.createElement("VGA:specs");
        }
        else // if (part instanceof CPU)
        {
            item = doc.createElement("CPU:CPU");
            specs = doc.createElement("CPU:specs");
        }

        item.setAttribute("id", part.id);

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
        element.setAttribute("unit", "USD");
        item.appendChild(element);

        if (part instanceof VGA)
        {
            VGA partVGA = (VGA) part;
            element = doc.createElement("VGA:core_num");
            element.setTextContent(partVGA.core_num);
            specs.appendChild(element);

            element = doc.createElement("VGA:memory_size");
            element.setTextContent(partVGA.memory_size);
            element.setAttribute("type", partVGA.memory_type);
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
            CPU partCPU = (CPU) part;
            element = doc.createElement("CPU:core_num");
            element.setTextContent(partCPU.core_num);
            specs.appendChild(element);

            element = doc.createElement("CPU:frequency");
            element.setTextContent(partCPU.frequency);
            specs.appendChild(element);

            element = doc.createElement("CPU:power_consumption");
            element.setTextContent(partCPU.power_consumption);
            specs.appendChild(element);

            element = doc.createElement("CPU:integrated_graphics");
            element.setTextContent(partCPU.integrated_graphics);
            specs.appendChild(element);
        }

        item.appendChild(specs);

        return item;
    }

    // Singleton
    private static volatile Controller instance;

    public static Controller getInstance(DisplayPanel displayPanel, JLabel statusBar)
    {
        Controller result = instance;
        if (result != null)
            return result;

        synchronized (Controller.class)
        {
            if (instance == null)
                instance = new Controller(displayPanel, statusBar);

            return instance;
        }
    }

}
