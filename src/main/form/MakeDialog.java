package main.form;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class MakeDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField;
    private JCheckBox CheckBoxVGA;
    private JCheckBox CheckBoxCPU;

    public boolean isOK = false;

    public MakeDialog()
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

        // ESC
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

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

    public boolean isCheckedVGA()
    {
        return CheckBoxVGA.isSelected();
    }

    public boolean isCheckedCPU()
    {
        return CheckBoxCPU.isSelected();
    }

    public String getFileName()
    {
        return textField.getText();
    }
}
