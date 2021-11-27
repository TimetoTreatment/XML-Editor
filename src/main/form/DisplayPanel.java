package main.form;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class DisplayPanel extends JPanel
{
    private JTabbedPane tabbedPane;
    private JTextPane textArea;
    private JScrollPane viewModePane;
    private JScrollPane editModePane;
    private JTree editModeTree;
    private DefaultMutableTreeNode editModeTreeRoot;

    DisplayPanel()
    {
        editModeTreeRoot = new DefaultMutableTreeNode();
        editModeTree = new JTree(editModeTreeRoot);

        tabbedPane = new JTabbedPane();
        textArea = new JTextPane();
        viewModePane = new JScrollPane(textArea);
        editModePane = new JScrollPane(editModeTree);

        setLayout(new BorderLayout());
        setBorder(new MatteBorder(2, 2, 1, 2, Color.black));

        add(tabbedPane, BorderLayout.CENTER);

        tabbedPane.addTab("View Mode", viewModePane);
        tabbedPane.addTab("Edit Mode", editModePane);

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

        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int selRow = editModeTree.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = editModeTree.getPathForLocation(e.getX(), e.getY());
                if(selRow != -1) {
                    if(e.getClickCount() == 1) {
                        System.out.println("ROW: "+selRow);
                        System.out.println(selPath.getLastPathComponent());
                    }
                    else if(e.getClickCount() == 2) {
                        System.out.println("ROW: "+selRow);
                    }
                }
            }
        };
        editModeTree.addMouseListener(ml);

    }

    public void setViewModeText(String str)
    {
        String htmlText = "<html>" + str + "</html>";
        textArea.setText(htmlText);
        textArea.setCaretPosition(0);
    }

    public DefaultMutableTreeNode getEditModeTreeRoot()
    {
        return editModeTreeRoot;
    }

    public DefaultTreeModel getEditModeTreeModel()
    {
        return ((DefaultTreeModel) editModeTree.getModel());
    }

    public void setEditModeEnable(boolean b)
    {
        tabbedPane.setEnabledAt(1, b);
    }



//    @Override
//    public void paintComponent(Graphics g)
//    {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D) g;
//
//        int srcX = viewModePane.getX();
//        int srcY = viewModePane.getY();
//        int dstX = srcX + viewModePane.getWidth();
//        int dstY = srcY + viewModePane.getHeight();
//
//        g2.setPaint(new GradientPaint(srcX, srcY, Color.white, dstX, dstY, new Color(168, 168, 168)));
//        g2.fillRect(srcX, srcY, dstX - srcX, dstY - srcY);
//    }
}
