package main.controller;

import main.domain.CPU;
import main.domain.NodeContainer;
import main.domain.Part;
import main.domain.VGA;
import main.form.*;
import main.repository.Repository;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;

public class Controller
{
    ApplicationFrame frame;

    private final Repository repository;
    private final DisplayForm displayForm;
    private final ControlForm controlForm;
    private final JLabel statusBar;
    private final DefaultMutableTreeNode editModeTreeRoot;

    private DefaultMutableTreeNode selectedTreeNode;
    private String currentFilePath;
    private String currentFileName;
    private boolean isSaved;

    private String validationResultDTD;
    private String validationResultXSD;

    public Controller()
    {
        frame = new ApplicationFrame();

        repository = Repository.getInstance();
        displayForm = frame.displayForm;
        controlForm = frame.controlForm;
        statusBar = frame.controlForm.getStatusBar();
        editModeTreeRoot = displayForm.getEditModeTreeRoot();

        selectedTreeNode = editModeTreeRoot;
        currentFilePath = "";
        currentFileName = "";
        isSaved = true;

        validationResultDTD = "";

        displayForm.tabbedPane.addChangeListener(e -> {

            switch (displayForm.tabbedPane.getSelectedIndex())
            {
                case 0 -> {
                    controlForm.setStatus(ControlForm.Status.LOADED);
                }
                case 1 -> {
                    controlForm.setStatus(ControlForm.Status.EDITABLE);
                }
                case 2 -> {
                    controlForm.setStatus(ControlForm.Status.LOADED);
                    validation();
                }
            }
        });

        displayForm.getEditModeTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        displayForm.getEditModeTree().addTreeSelectionListener(e -> {
            selectedTreeNode = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent());

            if (!(selectedTreeNode.getUserObject() instanceof NodeContainer))
            {
                controlForm.setStatus(ControlForm.Status.LOADED);
                return;
            }

            controlForm.setStatus(ControlForm.Status.EDITABLE);

//            Node domNode = ((NodeContainer) selectedTreeNode.getUserObject()).domNode;
//            System.out.println("[NODE]");
//            System.out.println("    Name   " + domNode.getNodeName());
//            System.out.println("    Value  " + domNode.getNodeValue());
//            System.out.println("    Text   " + domNode.getTextContent());
//            System.out.println();
        });

        printStatus("File not loaded", Color.red);

        addKeyBind(controlForm.buttonLoad(), "1", "LOAD", load);
        addKeyBind(controlForm.buttonMake(), "2", "MAKE", make);
        addKeyBind(controlForm.buttonFind(), "3", "FIND", find);
        addKeyBind(controlForm.buttonSave(), "4", "SAVE", save);
        addKeyBind(controlForm.buttonPrint(), "5", "PRINT", print);
        addKeyBind(controlForm.buttonInsert(), "6", "INSERT", insert);
        addKeyBind(controlForm.buttonUpdate(), "7", "UPDATE", update);
        addKeyBind(controlForm.buttonDelete(), "8", "DELETE", delete);
        addKeyBind(controlForm.buttonExit(), "9", "EXIT", exit);

        controlForm.buttonDelete().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        controlForm.buttonFind().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "FIND");
        controlForm.buttonSave().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "SAVE");
    }

    private void addKeyBind(JButton button, String key, String mapKey, Action action)
    {
        button.addActionListener(action);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), mapKey);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD" + key), mapKey);
        button.getActionMap().put(mapKey, action);
    }

    private void printStatus(String message, Color color)
    {
        statusBar.setText(message);
        statusBar.setForeground(color);
        System.out.println("[STATUS]");
        System.out.println(message + "\n");
    }

    private void reloadFile()
    {
        printStatus("\"" + currentFileName + "\" is loaded.", Color.black);

        displayForm.setTreeTitle(currentFileName);

        print();

        if (displayForm.tabbedPane.getSelectedIndex() == 1)
            controlForm.setStatus(ControlForm.Status.EDITABLE);
        else
            controlForm.setStatus(ControlForm.Status.LOADED);

        displayForm.setFeaturesEnable(true);
        isSaved = true;
    }

    private final Action load = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (!isSaved)
            {
                int iResult = displayForm.showConfirmDialog("XML Project", "Do you want to save the changes?");

                if (iResult != JOptionPane.OK_OPTION && iResult != JOptionPane.NO_OPTION)
                    return;

                if (iResult == JOptionPane.OK_OPTION)
                    save();
            }

            LoadDialog dialog = new LoadDialog();

            if (!dialog.isOK)
                return;

            String path = dialog.getPath();

            if (path == null || path.isBlank())
                return;

            if (!repository.load(path))
            {
                switch (repository.getStatus())
                {
                    case FILE_NOT_FOUND -> {
                        displayForm.showMessageDialog("XML Project: FILE NOT FOUND", "Could not find \"" + path + "\"");
                        return;
                    }
                    case PARSE_FATAL_ERROR -> {
                        displayForm.showMessageDialog("XML Project: FATAL ERROR", "Could not parse \"" + path + "\"\n\n" + repository.getErrorMsg());
                        return;
                    }
                    case PARSE_ERROR -> {
                        validationResultDTD += "[ERROR: DTD]\n" + repository.getErrorMsg();
                    }
                }
            }

            currentFilePath = path;
            if (currentFilePath.lastIndexOf('\\') != -1)
                currentFileName = currentFilePath.substring(currentFilePath.lastIndexOf('\\') + 1);
            else
                currentFileName = currentFilePath;

            reloadFile();
        }
    };

    private final Action make = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (!isSaved)
            {
                int iResult = displayForm.showConfirmDialog("XML Project", "Do you want to save the changes?");

                if (iResult != JOptionPane.OK_OPTION && iResult != JOptionPane.NO_OPTION)
                    return;

                if (iResult == JOptionPane.OK_OPTION)
                    save();
            }

            int iResult = displayForm.showOptionDialog(
                    "XML Project",
                    "Select type(s) to include.",
                    new Object[]{
                            "VGA and CPU",
                            "Only VGA",
                            "Only CPU",
                            "Empty file"});

            if (iResult == -1)
                return;

            boolean includeVGA;
            boolean includeCPU;

            if (iResult == 0)
            {
                includeVGA = true;
                includeCPU = true;
            }
            else if (iResult == 1)
            {
                includeVGA = true;
                includeCPU = false;
            }
            else if (iResult == 2)
            {
                includeVGA = false;
                includeCPU = true;
            }
            else // if (iResult == 3)
            {
                includeVGA = false;
                includeCPU = false;
            }

            Part.resetCurrentId();

            Document doc = new DocumentImpl();
            DocumentType documentType = doc.getImplementation().createDocumentType("Parts", null, "PartManager.dtd");
            doc.appendChild(documentType);

            repository.setDocument(doc);

            Element root;
            Attr newAttribute;
            Comment newComment;

            if (!includeCPU && !includeVGA)
                root = doc.createElement("Root");
            else
            {
                root = doc.createElement("Parts");

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
            }

            if (includeVGA)
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

            if (includeCPU)
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

            currentFilePath = "";
            currentFileName = "New File";

            reloadFile();

            isSaved = false;
        }
    };

    private final Action find = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            FindDialog dialog = new FindDialog();

            if (!dialog.isOK)
                return;

            String keyword = dialog.textField.getText();
            boolean matchCase = dialog.matchCaseCheckBox.isSelected();
            boolean matchWord = dialog.matchWordCheckBox.isSelected();

            if (keyword == null || keyword.isBlank())
                return;

            String target;

            if (!matchCase)
                target = keyword.toLowerCase();
            else
                target = keyword;

            ArrayList<TreePath> treePaths = new ArrayList<>();

            Enumeration<TreeNode> treeEnum = editModeTreeRoot.depthFirstEnumeration();

            while (treeEnum.hasMoreElements())
            {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeEnum.nextElement();
                Node domNode = ((NodeContainer) treeNode.getUserObject()).domNode;

                String nodeName = domNode.getNodeName();
                String nodeValue = domNode.getNodeValue();

                if (nodeName.charAt(0) == '#')
                    nodeName = "";

                if (!matchCase)
                    nodeName = nodeName.toLowerCase();

                if (nodeValue != null && !matchCase)
                    nodeValue = nodeValue.toLowerCase();

                if (matchWord)
                {
                    if (nodeName.equals(target) || (nodeValue != null && nodeValue.equals(target)))
                        treePaths.add(new TreePath(treeNode.getPath()));
                }
                else
                {
                    if (nodeName.contains(target) || (nodeValue != null && nodeValue.contains(target)))
                        treePaths.add(new TreePath(treeNode.getPath()));
                }
            }

            displayForm.setSelectionPaths(treePaths.toArray(new TreePath[0]));

            if (treePaths.size() == 0)
                printStatus("Could not find \"" + target + "\"", Color.magenta);
            else
                printStatus("Found " + treePaths.size() + " result(s) matching \"" + target + "\".", Color.black);

        }
    };

    private final Action save = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            SaveDialog dialog = new SaveDialog(currentFilePath);

            if (!dialog.isOK)
                return;

            String newPath = dialog.getSavePath();

            if (newPath == null || newPath.isBlank())
                return;

            currentFilePath = newPath;

            if (currentFilePath.lastIndexOf('\\') == -1)
                currentFileName = newPath;
            else
                currentFileName = newPath.substring(newPath.lastIndexOf('\\') + 1);

            repository.save(currentFilePath);

            displayForm.setTreeTitle(currentFilePath);
            print();

            printStatus("File \"" + currentFileName + "\" is saved.", Color.black);

            isSaved = true;
        }
    };

    private final Action print = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            refreshViewMode();
            refreshEditMode();
        }
    };

    private final Action insert = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            InsertDialog dialog = new InsertDialog();

            if (!dialog.isOK)
                return;

            String name = dialog.getName();
            String value = dialog.getValue();
            InsertDialog.NodeType type = dialog.getNodeType();

            NodeContainer nodeContainer = (NodeContainer) selectedTreeNode.getUserObject();
            Node domNode = nodeContainer.domNode;

            Document doc = repository.getDocument();

            Node newNode = null;

            if (type == InsertDialog.NodeType.ELEMENT)
            {
                newNode = doc.createElement(name);

                if (value != null && !value.isBlank())
                    newNode.setTextContent(value);

                domNode.appendChild(newNode);
            }
            else if (type == InsertDialog.NodeType.ATTRIBUTE)
            {
                newNode = doc.createAttribute(name);

                ((Attr) newNode).setValue(value);
                ((Element) domNode).setAttributeNode((Attr) newNode);
            }
            else if (type == InsertDialog.NodeType.COMMENT)
            {
                newNode = doc.createComment(value);
                domNode.appendChild(newNode);
            }
            else if (type == InsertDialog.NodeType.TEXT)
            {
                newNode = doc.createTextNode(value);
                domNode.appendChild(newNode);
            }

            refreshViewMode();
            refreshEditMode();

            Enumeration<TreeNode> treeEnum = editModeTreeRoot.depthFirstEnumeration();

            while (treeEnum.hasMoreElements())
            {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeEnum.nextElement();

                if (((NodeContainer) treeNode.getUserObject()).domNode == newNode)
                {
                    displayForm.setSelectionPath(new TreePath(treeNode.getPath()));
                    break;
                }
            }

            isSaved = false;
        }
    };

    Action update = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {

            DefaultMutableTreeNode currentTreeNode = selectedTreeNode;
            Node currentDomNode = ((NodeContainer) currentTreeNode.getUserObject()).domNode;
            Node newDomNode = null;

            UpdateDialog dialog = new UpdateDialog(currentDomNode);

            if (!dialog.isOK)
                return;

            String name = dialog.getName();
            String value = dialog.getValue();

            String prevName = currentTreeNode.getUserObject().toString();

            Document doc = repository.getDocument();

            switch (currentDomNode.getNodeType())
            {
                case Node.ELEMENT_NODE -> {
                    if (name.indexOf(':') != -1)
                        newDomNode = doc.renameNode(currentDomNode, name.substring(0, name.indexOf(':')), name);
                    else
                        newDomNode = doc.renameNode(currentDomNode, null, name);

                    currentTreeNode.setUserObject(new NodeContainer(newDomNode));
                }
                case Node.ATTRIBUTE_NODE -> {
                    newDomNode = doc.renameNode(currentDomNode, null, name);
                    newDomNode.setNodeValue(value);

                    currentTreeNode.setUserObject(new NodeContainer(newDomNode));
                }

                case Node.COMMENT_NODE, Node.TEXT_NODE -> {
                    currentDomNode.setNodeValue(value);
                    newDomNode = currentDomNode;
                }
            }

            refreshViewMode();
            refreshEditMode();

            Enumeration<TreeNode> treeEnum = editModeTreeRoot.depthFirstEnumeration();

            while (treeEnum.hasMoreElements())
            {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeEnum.nextElement();

                if (((NodeContainer) treeNode.getUserObject()).domNode == newDomNode)
                {
                    displayForm.setSelectionPath(new TreePath(treeNode.getPath()));
                    break;
                }
            }

            isSaved = false;

            printStatus("\"" + prevName + "\" is updated.", Color.black);
        }
    };

    Action delete = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            DefaultMutableTreeNode currentTreeNode = selectedTreeNode;

            if (selectedTreeNode == editModeTreeRoot)
            {
                JOptionPane.showMessageDialog(null, "Cannot delete root node", "XML Project", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Node currentDomNode = ((NodeContainer) currentTreeNode.getUserObject()).domNode;
            Node parentNode;

            String nodeStr = currentTreeNode.toString();

            if (currentDomNode.getNodeType() == Node.ATTRIBUTE_NODE)
            {
                parentNode = ((Attr) currentDomNode).getOwnerElement();
                ((Element) parentNode).removeAttributeNode((Attr) currentDomNode);
            }
            else
            {
                parentNode = currentDomNode.getParentNode();
                parentNode.removeChild(currentDomNode);
            }

            refreshViewMode();
            refreshEditMode();

            Enumeration<TreeNode> treeEnum = editModeTreeRoot.depthFirstEnumeration();

            while (treeEnum.hasMoreElements())
            {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeEnum.nextElement();

                if (((NodeContainer) treeNode.getUserObject()).domNode == parentNode)
                {
                    displayForm.expandTree(new TreePath(treeNode.getPath()));
                    break;
                }
            }

            isSaved = false;

            printStatus("\"" + nodeStr + "\" is deleted.", Color.black);
        }
    };

    Action exit = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (!isSaved)
            {
                int iResult = displayForm.showConfirmDialog("XML Project", "Do you want to save the changes?");

                if (iResult != JOptionPane.OK_OPTION && iResult != JOptionPane.NO_OPTION)
                    return;

                if (iResult == JOptionPane.OK_OPTION)
                    save();
            }

            System.exit(0);
        }
    };

    void print()
    {
        print.actionPerformed(null);
    }

    void save()
    {
        save.actionPerformed(null);
    }

    private String createPlainText()
    {
        Stack<Node> nodeStack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();

        nodeStack.push(repository.getDocument().getDocumentElement());
        indentStack.push(0);

        String contents = "<h1 style=\"text-align:center\"> :: " + currentFileName + " ::</h1><br/>";

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

    private void createTree()
    {
        Stack<Node> nodeStack = new Stack<>();
        Stack<Integer> indentStack = new Stack<>();
        Stack<DefaultMutableTreeNode> treeNodeStack = new Stack<>();

        nodeStack.push(repository.getDocument().getDocumentElement());
        indentStack.push(0);

        editModeTreeRoot.removeAllChildren();
        treeNodeStack.push(editModeTreeRoot);

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

    }

    private void refreshViewMode()
    {
        String plainText = createPlainText();
        displayForm.setViewModeText(plainText);
    }

    private void refreshEditMode()
    {
        createTree();

        displayForm.reload();
        displayForm.expandTree();
    }

    private Node nodeFactory(Document doc, Part part)
    {
        Element item;
        Element specs;
        Element element;

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

        if (part instanceof VGA partVGA)
        {
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
        else if (part instanceof CPU partCPU)
        {
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

    private void validation()
    {
        validationResultXSD = "";

        Document doc = repository.getDocument();
        NamedNodeMap attributes = doc.getDocumentElement().getAttributes();

        if (attributes == null)
        {
            displayForm.setValidationText("No XSD was found.");
            return;
        }

        Node schemaLocationNode = attributes.getNamedItem("xsi:schemaLocation");

        if (schemaLocationNode == null)
        {
            displayForm.setValidationText("No XSD was found.");
            return;
        }

        String location = schemaLocationNode.getNodeValue();

        if (!location.contains(" "))
        {
            displayForm.setValidationText("No XSD was found.");
            return;
        }

        String xmlPath;
        String xsdPath;
        String workingPath = "";

        if (currentFilePath.lastIndexOf('\\') != -1)
            workingPath = currentFilePath.substring(0, currentFilePath.lastIndexOf('\\')) + "\\";

        xsdPath = workingPath + location.substring(location.indexOf(' ') + 1);
        xmlPath = workingPath + "ValidationTemp" + (int) (1000 * Math.random());
        repository.save(xmlPath);

        try
        {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.setErrorHandler(new ErrorHandler()
            {
                @Override
                public void warning(SAXParseException e)
                {
                    validationResultXSD += "[ERROR: XSD]\n" + e.getMessage() + "\n\n";
                }

                @Override
                public void error(SAXParseException e)
                {
                    validationResultXSD += "[ERROR: XSD]\n" + e.getMessage() + "\n\n";
                }

                @Override
                public void fatalError(SAXParseException e)
                {
                    validationResultXSD += "[ERROR: XSD]\n" + e.getMessage() + "\n\n";
                }
            });
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        String validationResult = validationResultDTD + validationResultXSD;

        if (validationResult.isBlank())
            displayForm.setValidationText("No error was found.");
        else
            displayForm.setValidationText(validationResultDTD + "\n" + validationResultXSD);

        new File(xmlPath).delete();
    }
}
