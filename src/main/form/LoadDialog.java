package main.form;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class LoadDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldPath;
    private JButton buttonBrowse;
    public boolean isOK = false;

    public LoadDialog()
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());
        buttonBrowse.addActionListener(e -> onBrowse());

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

        textFieldPath.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(!textFieldPath.getText().isBlank());
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(!textFieldPath.getText().isBlank());
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

    private void onBrowse()
    {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

        int iResult = fileChooser.showOpenDialog(null);

        if (iResult != JFileChooser.APPROVE_OPTION)
            return;

        textFieldPath.setText(fileChooser.getSelectedFile().getPath());
    }

    public String getPath()
    {
        return textFieldPath.getText();
    }
}
