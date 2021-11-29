package main.form;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class SaveDialog extends JDialog
{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldPath;
    private String savePath;
    public boolean isOK = false;

    public SaveDialog(String initialPath)
    {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        textFieldPath.setText(initialPath);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // X 클릭 시 onCancel() 호출
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                onCancel();
            }
        });

        // ESCAPE 시 onCancel() 호출
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        if (initialPath == null || initialPath.isBlank())
            buttonOK.setEnabled(false);

        textFieldPath.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(!textFieldPath.getText().isBlank());

                if (initialPath != null && !initialPath.isBlank())
                {
                    if (initialPath.equalsIgnoreCase(textFieldPath.getText()))
                        buttonOK.setText("Save");
                    else
                        buttonOK.setText("Save as");
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e)
            {
                buttonOK.setEnabled(!textFieldPath.getText().isBlank());

                if (initialPath != null && !initialPath.isBlank())
                {
                    if (initialPath.equalsIgnoreCase(textFieldPath.getText()))
                        buttonOK.setText("Save");
                    else
                        buttonOK.setText("Save as");
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });

        setTitle("XML Project");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void onOK()
    {
        savePath = textFieldPath.getText();
        isOK = true;
        dispose();
    }

    private void onCancel()
    {
        isOK = false;
        dispose();
    }

    public String getSavePath()
    {
        return savePath;
    }
}
