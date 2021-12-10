package main.form;

public class Tutorial
{
    public static String text = "";

    public static String getText()
    {
        if (text.isBlank())
        {
            text += "<h1>XML Programming Project</h1><br/>";

            text += "<h2>Program Mode</h2>";
            text += "  <b>1. Viewer</b><br/>";
            text += "     Print DOM as text.<br/>";
            text += "<div style=\"font-size:4px;\"></div>";
            text += "  <b>2. Editor</b><br/>";
            text += "     Print DOM as tree and enable edit functions.<br/>";
            text += "<div style=\"font-size:4px;\"></div>";
            text += "  <b>3. Validator</b><br/>";
            text += "     Validate current DOM by using DTD or XSD.<br/><br/>";

            text += "<h2>Legend</h2>";
            text += "  <font color=\"blue\"><b>[E]</b> Element <br/>";
            text += "  <font color=\"#0078FF\"><b>[A]</b> Attribute <br/>";
            text += "  <font color=\"green\"><b>[C]</b> Comment <br/>";
            text += "  <font color=\"black\"><b>[T]</b> Text <br/>";
        }

        return text;
    }
}
