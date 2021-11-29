package main.form;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class InsertDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox<String> comboBoxType;

    public String getName()
    {
        return textFieldName.getText();
    }

    public String getValue()
    {
        return textFieldValue.getText();
    }

    public NodeType getNodeType()
    {
        return type;
    }

    private JTextField textFieldName;
    private JTextField textFieldValue;
    private JLabel label2;
    private JLabel label1;
    private NodeType type = NodeType.ELEMENT;
    public boolean isOK = false;

    public InsertDialog()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        buttonOK.setEnabled(false);

        comboBoxType.addItem("Element");
        comboBoxType.addItem("Attribute");
        comboBoxType.addItem("Comment");
        comboBoxType.addItem("Text");

        comboBoxType.addActionListener(e -> {
            switch (comboBoxType.getSelectedIndex())
            {
                case 0 -> {
                    type = NodeType.ELEMENT;
                    textFieldName.setEnabled(true);
                    textFieldValue.setEnabled(true);
                    textFieldName.setText("");
                    label1.setEnabled(true);
                    label2.setText("Text");
                }
                case 1 -> {
                    type = NodeType.ATTRIBUTE;
                    textFieldName.setEnabled(true);
                    textFieldValue.setEnabled(true);
                    textFieldName.setText("");
                    label1.setEnabled(true);
                    label2.setText("Value");
                }
                case 2 -> {
                    type = NodeType.COMMENT;
                    textFieldName.setEnabled(false);
                    textFieldValue.setEnabled(true);
                    textFieldName.setText("#comment");
                    label1.setEnabled(false);
                    label2.setText("Value");
                }
                case 3 -> {
                    type = NodeType.TEXT;
                    textFieldName.setEnabled(false);
                    textFieldValue.setEnabled(true);
                    textFieldName.setText("#text");
                    label1.setEnabled(false);
                    label2.setText("Text");
                }
            }
        });

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
        textFieldName.requestFocus();
        setVisible(true);
    }

    public enum NodeType
    {
        ELEMENT,
        ATTRIBUTE,
        COMMENT,
        TEXT,
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
}
