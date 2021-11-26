package main.form;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class DisplayPanel extends JPanel
{
    private JTextArea textArea = new JTextArea();
    private JScrollPane scrollPane = new JScrollPane(textArea);

    DisplayPanel()
    {
        setLayout(new BorderLayout());
        setBorder(new MatteBorder(2, 2, 1, 2, Color.black));

        add(scrollPane, BorderLayout.CENTER);

        textArea.setOpaque(false);

        textArea.setFont(new Font(Font.DIALOG, Font.PLAIN, 16));
        scrollPane.setBorder(new CompoundBorder(new LineBorder(Color.darkGray, 10),
                new EmptyBorder(10, 10, 10, 10)));

        scrollPane.setOpaque(false);
        textArea.setEditable(false);

        scrollPane.getViewport().setOpaque(false);

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    public void setText(String str)
    {
        textArea.setText(str);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int srcX = scrollPane.getX();
        int srcY = scrollPane.getY();
        int dstX = srcX + scrollPane.getWidth();
        int dstY = srcY + scrollPane.getHeight();

        g2.setPaint(new GradientPaint(srcX, srcY, Color.white, dstX, dstY, new Color(127, 127, 127)));
        g2.fillRect(srcX, srcY, dstX - srcX, dstY - srcY);
    }
}
