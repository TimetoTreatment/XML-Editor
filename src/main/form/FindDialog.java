package main.form;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class FindDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JTextField textField;
    public JCheckBox matchCaseCheckBox;
    public JCheckBox matchWordCheckBox;
    public boolean isOK = false;

    public FindDialog()
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

        buttonOK.setEnabled(false);

        textField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(!textField.getText().isBlank());
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(!textField.getText().isBlank());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        setTitle("XML Project");
        pack();
        setLocationRelativeTo(null);
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
}
