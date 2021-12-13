package main.controller;

import main.domain.CPU;
import main.domain.NodeContainer;
import main.domain.Part;
import main.domain.VGA;
import main.form.*;
import main.repository.Repository;
import main.repository.Validator;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;


public class Controller
{
    private final ApplicationFrame frame;

    private final Repository repository;
    private final DisplayForm displayForm;
    private final ControlForm controlForm;
    private final JLabel statusBar;
    private final DefaultMutableTreeNode editModeTreeRoot;

    private DefaultMutableTreeNode selectedTreeNode;
    private TreePath[] selectionPaths;
    private String currentFilePath;
    private String currentFileName;
    private boolean isSaved;

    public Controller()
    {
        frame = new ApplicationFrame();

        repository = Repository.getInstance();
        displayForm = frame.displayForm;
        controlForm = frame.controlForm;
        statusBar = frame.controlForm.getStatusBar();
        editModeTreeRoot = displayForm.getEditModeTreeRoot();

        selectedTreeNode = editModeTreeRoot;
        selectionPaths = displayForm.getPaths();
        currentFilePath = "";
        currentFileName = "";
        isSaved = true;

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
                    validateDOM(true, true);
                }
            }
        });

        displayForm.getEditModeTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        displayForm.getEditModeTree().addTreeSelectionListener(e -> {
            selectionPaths = displayForm.getPaths();
            selectedTreeNode = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent());

            if (displayForm.tabbedPane.getSelectedIndex() != 1)
                return;

            if (selectedTreeNode == displayForm.getEditModeTreeRoot())
                controlForm.setStatus(ControlForm.Status.EDITABLE_CANNOT_DELETE);
            else if (selectedTreeNode.getUserObject() instanceof NodeContainer)
                controlForm.setStatus(ControlForm.Status.EDITABLE);
            else
                controlForm.setStatus(ControlForm.Status.LOADED);

//            Node domNode = ((NodeContainer) selectedTreeNode.getUserObject()).domNode;
//            System.out.println("[NODE]");
//            System.out.println("    Name   " + domNode.getNodeName());
//            System.out.println("    Value  " + domNode.getNodeValue());
//            System.out.println("    Text   " + domNode.getTextContent());
//            System.out.println();
        });

        printStatus("File not loaded", Color.red);

        ButtonBind(controlForm.buttonLoad(), "1", "LOAD", load);
        ButtonBind(controlForm.buttonMake(), "2", "MAKE", make);
        ButtonBind(controlForm.buttonFind(), "3", "FIND", find);
        ButtonBind(controlForm.buttonSave(), "4", "SAVE", save);
        ButtonBind(controlForm.buttonPrint(), "5", "PRINT", print);
        ButtonBind(controlForm.buttonInsert(), "6", "INSERT", insert);
        ButtonBind(controlForm.buttonUpdate(), "7", "UPDATE", update);
        ButtonBind(controlForm.buttonDelete(), "8", "DELETE", delete);
        ButtonBind(controlForm.buttonExit(), "9", "EXIT", exit);

        controlForm.buttonDelete().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "DELETE");
        controlForm.buttonFind().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK), "FIND");
        controlForm.buttonSave().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "SAVE");
        controlForm.buttonInsert().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "INSERT");

        controlForm.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_U, 0), "TREE_NODE_UP");
        controlForm.mainPanel.getActionMap().put("TREE_NODE_UP", TreeNodeUp);
        controlForm.mainPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "TREE_NODE_DOWN");
        controlForm.mainPanel.getActionMap().put("TREE_NODE_DOWN", TreeNodeDown);

        displayForm.tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK), "MODE_VIEWER");
        displayForm.tabbedPane.getActionMap().put("MODE_VIEWER", ModeViewer);
        displayForm.tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK), "MODE_EDITOR");
        displayForm.tabbedPane.getActionMap().put("MODE_EDITOR", ModeEditor);
        displayForm.tabbedPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_DOWN_MASK), "MODE_VALIDATOR");
        displayForm.tabbedPane.getActionMap().put("MODE_VALIDATOR", ModeValidator);
    }

    private final Action ModeViewer = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            displayForm.tabbedPane.setSelectedIndex(0);
        }
    };

    private final Action ModeEditor = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            displayForm.tabbedPane.setSelectedIndex(1);
        }
    };

    private final Action ModeValidator = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            displayForm.tabbedPane.setSelectedIndex(2);
        }
    };

    private final Action TreeNodeUp = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Node targetNode1 = ((NodeContainer) selectedTreeNode.getUserObject()).domNode;
            Node targetNode2 = null;

            if (targetNode1.getNodeType() != Node.ATTRIBUTE_NODE)
            {
                if (targetNode1.getNextSibling() != null && targetNode1.getNextSibling().getNodeType() == Node.TEXT_NODE)
                    targetNode2 = targetNode1.getNextSibling();

                Node parent = targetNode1.getParentNode();
                Node newNode1 = targetNode1.cloneNode(true);
                Node newNode2 = null;
                Node prevNode = targetNode1.getPreviousSibling();

                if (targetNode2 != null)
                    newNode2 = targetNode1.getNextSibling().cloneNode(true);

                if (prevNode != null && prevNode.getNodeType() == Node.TEXT_NODE)
                    prevNode = prevNode.getPreviousSibling();

                parent.removeChild(targetNode1);
                parent.insertBefore(newNode1, prevNode);

                if (targetNode2 != null)
                {
                    parent.removeChild(targetNode2);
                    parent.insertBefore(newNode2, prevNode);
                }

                print();

                selectNodes(new ArrayList<>()
                {{
                    add(newNode1);
                }}, false);
            }
        }
    };

    private final Action TreeNodeDown = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            Node targetNode1 = ((NodeContainer) selectedTreeNode.getUserObject()).domNode;
            Node targetNode2 = null;

            if (targetNode1.getNodeType() != Node.ATTRIBUTE_NODE)
            {
                if (targetNode1.getNextSibling() != null && targetNode1.getNextSibling().getNodeType() == Node.TEXT_NODE)
                    targetNode2 = targetNode1.getNextSibling();

                Node parent = targetNode1.getParentNode();
                Node newNode1 = targetNode1.cloneNode(true);
                Node newNode2 = null;
                Node nextElement = findNextElement(targetNode1);

                if (targetNode2 != null)
                {
                    newNode2 = targetNode2.cloneNode(true);
                    parent.removeChild(targetNode2);
                }

                parent.removeChild(targetNode1);

                if (nextElement != null)
                {
                    Node nextnextElement = findNextElement(nextElement);

                    if (nextnextElement != null)
                    {
                        parent.insertBefore(newNode1, nextnextElement);

                        if (newNode2 != null)
                            parent.insertBefore(newNode2, nextnextElement);
                    }
                    else
                    {
                        parent.appendChild(newNode1);

                        if (newNode2 != null)
                            parent.appendChild(newNode2);
                    }
                }
                else // if (nextNode == null)
                {
                    Node firstElement = null;

                    if (parent.getFirstChild() != null)
                        firstElement = parent.getFirstChild();

                    if (firstElement != null && firstElement.getNodeType() == Node.TEXT_NODE)
                        firstElement = findNextElement(parent.getFirstChild());

                    if (firstElement != null)
                    {
                        parent.insertBefore(newNode1, firstElement);

                        if (newNode2 != null)
                            parent.insertBefore(newNode2, firstElement);
                    }
                    else
                    {
                        parent.appendChild(newNode1);

                        if (newNode2 != null)
                            parent.appendChild(newNode2);
                    }
                }

                print();

                selectNodes(new ArrayList<>()
                {{
                    add(newNode1);
                }}, false);
            }
        }
    };

    private Node findNextElement(Node element)
    {
        Node nextElement = element.getNextSibling();

        if (nextElement != null && nextElement.getNodeType() == Node.TEXT_NODE)
            nextElement = nextElement.getNextSibling();

        return nextElement;
    }

    private void ButtonBind(JButton button, String key, String mapKey, Action action)
    {
        button.addActionListener(action);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), mapKey);
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("NUMPAD" + key), mapKey);
        button.getActionMap().put(mapKey, action);
    }

    private void validateDOM(boolean updateTextField, boolean updateStatusBar)
    {
        String allErrorMsg = "";
        int errorCount = 0;

        if (!Validator.isValidateDTD(repository))
        {
            errorCount += Validator.getErrorMsgList().size();

            for (String errorMsg : Validator.getErrorMsgList())
            {
                allErrorMsg += "<font color=\"red\">[DTD]</font>\n";
                allErrorMsg += "<font color=\"black\">" + errorMsg + "</font>\n";
            }
        }

        if (!Validator.isValidateXSD(repository))
        {
            errorCount += Validator.getErrorMsgList().size();

            for (String errorMsg : Validator.getErrorMsgList())
            {
                allErrorMsg += "<font color=\"red\">[XSD]</font>\n";
                allErrorMsg += "<font color=\"black\">" + errorMsg + "</font>\n";
            }
        }

        if (errorCount == 0)
        {
            if (updateTextField)
            {
                allErrorMsg = "<font color=\"blue\">[DTD] </font>";

                if (Validator.isDTDFound)
                    allErrorMsg += "<font color=\"black\">No errors found.</font>\n\n";
                else
                    allErrorMsg += "<font color=\"black\">No DTD found.</font>\n\n";

                allErrorMsg += "<font color=\"blue\">[XSD] </font>";

                if (Validator.isXSDFound)
                    allErrorMsg += "<font color=\"black\">No errors found.</font>\n";
                else
                    allErrorMsg += "<font color=\"black\">No XSD found.</font>\n\n";

                displayForm.setValidationText(allErrorMsg);
            }

            if (updateStatusBar)
                printStatus("No errors found.", Color.black);
        }
        else
        {
            if (updateTextField)
                displayForm.setValidationText(allErrorMsg);

            if (updateStatusBar)
                printStatus("Found " + errorCount + " validation error(s).", Color.red);
        }
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
        if (displayForm.tabbedPane.getSelectedIndex() == 1)
            controlForm.setStatus(ControlForm.Status.EDITABLE);
        else
            controlForm.setStatus(ControlForm.Status.LOADED);

        displayForm.setTreeTitle(currentFileName);
        displayForm.setFeaturesEnable(true);

        print();
        validateDOM(true, false);

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
                displayForm.showMessageDialog("XML Project: FILE NOT FOUND", "Could not found \"" + path + "\"");
                return;
            }

            currentFilePath = path;
            currentFileName = pathToName(path);

            reloadFile();
            printStatus("\"" + currentFileName + "\" has been loaded.", Color.black);
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

            MakeDialog dialog = new MakeDialog();

            if (!dialog.isOK)
                return;

            boolean includeVGA = dialog.isCheckedVGA();
            boolean includeCPU = dialog.isCheckedCPU();
            String path = dialog.getFileName();

            Part.resetCurrentId();

            Document doc = new DocumentImpl();
            Element root;
            Attr newAttribute;
            Comment newComment;

            if (includeCPU || includeVGA)
            {
                DocumentType documentType = doc.getImplementation().createDocumentType("Parts", null, "PartManager.dtd");
                doc.appendChild(documentType);
            }

            repository.setDocument(doc);

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
            }

            doc.appendChild(root);

            currentFilePath = path;
            currentFileName = path;

            if (currentFilePath.isBlank())
                currentFileName = "New File";

            printStatus("\"" + currentFileName + "\" has been created.", Color.black);
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
                printStatus("Could not find \"" + keyword + "\"", Color.magenta);
            else
                printStatus("Found " + treePaths.size() + " result(s) matching \"" + keyword + "\".", Color.black);

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
            currentFileName = pathToName(newPath);

            repository.save(currentFilePath);

            displayForm.setTreeTitle(currentFileName);
            print();

            printStatus("File \"" + currentFileName + "\" has been saved.", Color.black);

            isSaved = true;
        }
    };

    private final Action print = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            refreshViewerTab();
            refreshEditorTab();
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

            ArrayList<Node> newNodeList = new ArrayList<>();

            for (TreePath selectionPath : selectionPaths)
            {
                Document doc = repository.getDocument();

                NodeContainer nodeContainer = (NodeContainer) ((DefaultMutableTreeNode) selectionPath.getLastPathComponent()).getUserObject();
                Node domNode = nodeContainer.domNode;
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

                if (newNode != null)
                    newNodeList.add(newNode);
            }

            print();
            selectNodes(newNodeList, false);

            printStatus("\"" + newNodeList.get(0).getNodeName() + "\" has been inserted.", Color.black);

            isSaved = false;
        }
    };

    final Action update = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {

            DefaultMutableTreeNode currentTreeNode = selectedTreeNode;
            Node currentDomNode = ((NodeContainer) currentTreeNode.getUserObject()).domNode;
            Node newDomNode;

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
                }
            }

            print();

            selectNodes(new ArrayList<>()
            {{
                add(currentDomNode);
            }}, false);

            isSaved = false;

            printStatus("\"" + prevName + "\" has been updated.", Color.black);
        }
    };

    final Action delete = new AbstractAction()
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ArrayList<Node> newNodeList = new ArrayList<>();
            String nodeStr = selectedTreeNode.getUserObject().toString();

            for (TreePath selectionPath : selectionPaths)
            {
                DefaultMutableTreeNode currentTreeNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();

                if (currentTreeNode == editModeTreeRoot)
                {
                    JOptionPane.showMessageDialog(null, "Cannot delete root node", "XML Project", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                Node currentDomNode = ((NodeContainer) currentTreeNode.getUserObject()).domNode;
                Node parentNode;

                if (currentDomNode.getNodeType() == Node.ATTRIBUTE_NODE)
                {
                    parentNode = ((Attr) currentDomNode).getOwnerElement();
                    ((Element) parentNode).removeAttributeNode((Attr) currentDomNode);
                }
                else
                {
                    parentNode = currentDomNode.getParentNode();

                    if (currentDomNode.getPreviousSibling() != null && currentDomNode.getPreviousSibling().getNodeType() == Node.TEXT_NODE)
                        parentNode.removeChild(currentDomNode.getPreviousSibling());

                    else if (currentDomNode.getNextSibling() != null && currentDomNode.getNextSibling().getNodeType() == Node.TEXT_NODE)
                        parentNode.removeChild(currentDomNode.getPreviousSibling());

                    parentNode.removeChild(currentDomNode);
                }

                newNodeList.add(parentNode);
            }

            print();
            selectNodes(newNodeList, true);

            isSaved = false;

            printStatus("\"" + nodeStr + "\" has been deleted.", Color.black);
        }
    };

    final Action exit = new AbstractAction()
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

        String contents = "<h1 style=\"text-align:center\">:: " + currentFileName + " ::</h1><br/>";

        while (!nodeStack.empty())
        {
            Node node = nodeStack.pop();
            Integer indent = indentStack.pop();
            NodeList childNodes = node.getChildNodes();
            NamedNodeMap attributes = node.getAttributes();

            String blank = "";

            for (int i = 0; i < indent; i++)
                blank += " ";

            switch (node.getNodeType())
            {
                case Node.DOCUMENT_NODE, Node.ENTITY_NODE -> contents += blank + node.getNodeName();
                case Node.ELEMENT_NODE, Node.ENTITY_REFERENCE_NODE -> contents += "<font color=\"blue\">" + blank + node.getNodeName() + "</font>";
                case Node.CDATA_SECTION_NODE, Node.TEXT_NODE -> contents += blank + node.getNodeValue();
                case Node.COMMENT_NODE -> contents += "<font color=\"green\">" + blank + node.getNodeValue() + "</font>";
                case Node.ATTRIBUTE_NODE -> contents += "<font color=\"#0078FF\">" + blank + node.getNodeName() + "</font><font color=\"#A8A8A8\"> = </font><font color=\"black\">" + node.getNodeValue() + "</font>";
            }

            contents += "\n";

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

    private void refreshViewerTab()
    {
        String plainText = createPlainText();
        displayForm.setViewModeText(plainText);
    }

    private void refreshEditorTab()
    {
        createTree();

        displayForm.reloadTree();
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

    private void selectNodes(ArrayList<Node> nodeList, boolean expand)
    {
        Enumeration<TreeNode> treeEnum = editModeTreeRoot.depthFirstEnumeration();
        ArrayList<TreePath> treePaths = new ArrayList<>();

        while (treeEnum.hasMoreElements())
        {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeEnum.nextElement();

            for (Node node : nodeList)
            {
                if (((NodeContainer) treeNode.getUserObject()).domNode == node)
                    treePaths.add(new TreePath(treeNode.getPath()));
            }
        }

        if (expand)
        {
            for (TreePath treePath : treePaths) displayForm.expandTree(treePath);
        }

        displayForm.setSelectionPaths(treePaths.toArray(new TreePath[0]));
    }

    private String pathToName(String path)
    {
        if (path.lastIndexOf('\\') != -1)
            return path.substring(path.lastIndexOf('\\') + 1);
        else if (path.lastIndexOf(path.lastIndexOf('/')) != -1)
            return path.substring(path.lastIndexOf('/') + 1);
        else
            return path;
    }
}
