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
    public JTabbedPane tabbedPane;
    private final JTextPane textArea;
    private final JTree editModeTree;
    private final DefaultMutableTreeNode editModeTreeRoot;

    DisplayForm()
    {
        setLayout(new BorderLayout());

        DefaultMutableTreeNode dummyRoot = new DefaultMutableTreeNode();

        editModeTreeRoot = new DefaultMutableTreeNode();

        dummyRoot.setUserObject("");
        dummyRoot.add(editModeTreeRoot);

        editModeTree = new JTree(dummyRoot);

        tabbedPane = new JTabbedPane();
        textArea = new JTextPane();
        JScrollPane viewModePane = new JScrollPane(textArea);
        JScrollPane editModePane = new JScrollPane(editModeTree);

        tabbedPane.add("View Mode", viewModePane);
        tabbedPane.add("Edit Mode", editModePane);

        viewModePane.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 10),
                new EmptyBorder(0, 10, 0, 0)));

        editModePane.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 10),
                new EmptyBorder(0, 10, 0, 0)));

        viewModePane.setBackground(Color.white);
        editModePane.setBackground(Color.white);

        textArea.setEditable(false);
        textArea.setContentType("text/html");

        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        editModeTree.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));

        ((HTMLDocument) textArea.getDocument()).getStyleSheet().addRule("body {" +
                "font-family: " + textArea.getFont().getFamily() + "; " +
                "font-size: " + textArea.getFont().getSize() + "pt; }");
        tabbedPane.setEnabledAt(1, false);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void setViewModeText(String str)
    {
        String htmlText = "<html>" + str + "</html>";
        textArea.setText(htmlText);
        textArea.setCaretPosition(0);
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

    public void setEditModeEnable(boolean b)
    {
        tabbedPane.setEnabledAt(1, b);
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

    public void reload()
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

}
