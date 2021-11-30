package main.form;

import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class UpdateDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldName;
    private JTextField textFieldValue;
    private JLabel labelName;
    private JLabel labelValue;

    public boolean isOK = false;

    public UpdateDialog(Node node)
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        textFieldName.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(textFieldName.getText().length() != 0 && !Character.isDigit(textFieldName.getText().charAt(0)));
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(textFieldName.getText().length() != 0 && !Character.isDigit(textFieldName.getText().charAt(0)));
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });

        setTitle("XML Project");
        pack();
        setLocationRelativeTo(null);

        switch(node.getNodeType())
        {
            case Node.ELEMENT_NODE -> {
                textFieldValue.setEnabled(false);
                labelValue.setEnabled(false);
            }

            case Node.ATTRIBUTE_NODE -> {
                textFieldName.setText(node.getNodeName());
                textFieldValue.setText(node.getNodeValue());
                textFieldValue.requestFocus();
            }

            case Node.TEXT_NODE , Node.COMMENT_NODE -> {
                textFieldName.setEnabled(false);
                labelName.setEnabled(false);
            }
        }

        setVisible(true);
    }

    private void onOK()
    {
        isOK = true;
        dispose();
    }

    private void onCancel()
    {
        isOK = false;
        dispose();
    }

    public String getName()
    {
        return textFieldName.getText();
    }

    public String getValue()
    {
        return textFieldValue.getText();
    }
}
