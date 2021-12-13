package main.form;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;


public class DisplayForm extends JPanel
{
    public final JTabbedPane tabbedPane;
    private final JTextPane textAreaViewMode;
    private final JTextPane textAreaValidation;
    private final JTree editModeTree;
    private final DefaultMutableTreeNode editModeTreeRoot;

    DisplayForm()
    {
        setLayout(new BorderLayout());

        DefaultMutableTreeNode fileNameNode = new DefaultMutableTreeNode();

        editModeTreeRoot = new DefaultMutableTreeNode();

        fileNameNode.setUserObject("");
        fileNameNode.add(editModeTreeRoot);

        editModeTree = new JTree(fileNameNode);

        tabbedPane = new JTabbedPane();
        textAreaViewMode = new JTextPane();
        textAreaValidation = new JTextPane();
        JScrollPane viewModePane = new JScrollPane(textAreaViewMode);
        JScrollPane editModePane = new JScrollPane(editModeTree);
        JScrollPane validationPane = new JScrollPane(textAreaValidation);

        viewModePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        editModePane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        validationPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        tabbedPane.add("Viewer", viewModePane);
        tabbedPane.add("Editor", editModePane);
        tabbedPane.add("Validator", validationPane);

        viewModePane.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 10),
                new EmptyBorder(0, 10, 0, 0)));

        editModePane.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 10),
                new EmptyBorder(0, 10, 0, 0)));

        validationPane.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 10),
                new EmptyBorder(0, 10, 0, 0)));

        viewModePane.setBackground(Color.white);
        editModePane.setBackground(Color.white);
        validationPane.setBackground(Color.white);

        Font monospaceFont = new Font(Font.MONOSPACED, Font.PLAIN, 16);

        textAreaViewMode.setEditable(false);
        textAreaViewMode.setContentType("text/html");
        textAreaValidation.setEditable(false);
        textAreaValidation.setContentType("text/html");

        textAreaViewMode.setFont(monospaceFont);
        textAreaValidation.setFont(monospaceFont);
        editModeTree.setFont(monospaceFont);

        ((HTMLDocument) textAreaViewMode.getDocument()).getStyleSheet().addRule("body {" +
                "font-family: " + monospaceFont.getFamily() + "; " +
                "font-size: " + monospaceFont.getSize() + "pt; }");

        ((HTMLDocument) textAreaValidation.getDocument()).getStyleSheet().addRule("body {" +
                "font-family: " + monospaceFont.getFamily() + "; " +
                "font-size: " + monospaceFont.getSize() + "pt; }");

        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void setViewModeText(String str)
    {
        String htmlText = "<html>" + str + "</html>";
        htmlText = htmlText.replaceAll("\n", "<br/>");
        htmlText = htmlText.replace(" ", "&nbsp;");

        textAreaViewMode.setText(htmlText);
        textAreaViewMode.setCaretPosition(0);
    }

    public void setValidationText(String str)
    {
        String htmlText = "<html>" + str + "</html>";
        htmlText = htmlText.replaceAll("\n", "<br/>");
        htmlText = htmlText.replace(" ", "&nbsp;");

        textAreaValidation.setText(htmlText);
        textAreaValidation.setCaretPosition(0);
    }

    public void expandTree()
    {
        editModeTree.expandRow(1);
        editModeTree.setSelectionRow(1);
    }

    public void expandTree(TreePath treePath)
    {
        editModeTree.expandPath(treePath);
        editModeTree.setSelectionPath(treePath);
    }

    public DefaultMutableTreeNode getEditModeTreeRoot()
    {
        return editModeTreeRoot;
    }

    public void setFeaturesEnable(boolean b)
    {
        tabbedPane.setEnabledAt(1, b);
        tabbedPane.setEnabledAt(2, b);
    }

    public void setSelectionPath(TreePath treePath)
    {
        editModeTree.setSelectionPath(treePath);
    }

    public void setSelectionPaths(TreePath[] treePath)
    {
        editModeTree.setSelectionPaths(treePath);
    }

    public void setTreeTitle(String title)
    {
        ((DefaultMutableTreeNode) editModeTree.getModel().getRoot()).setUserObject(title);
    }

    public void reloadTree()
    {
        ((DefaultTreeModel) editModeTree.getModel()).reload();
    }

    public String showInputDialog(String title, String message)
    {
        String input = "";

        while (input != null && input.isBlank())
        {
            input = JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
        }

        return input;
    }

    public int showOptionDialog(String title, String message, Object[] options)
    {
        return JOptionPane.showOptionDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    public void showMessageDialog(String title, String message)
    {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public int showConfirmDialog(String title, String message)
    {
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public JTree getEditModeTree()
    {
        return editModeTree;
    }

    public TreePath[] getPaths()
    {
        return editModeTree.getSelectionPaths();
    }
}
