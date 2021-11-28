package main.domain;

import org.w3c.dom.Node;

import javax.swing.tree.DefaultMutableTreeNode;

public class NodeContainer
{
    public Node domNode;
    private String defaultName;
    private String name;

    public NodeContainer(Node domNode)
    {
        this.domNode = domNode;

        updateName();
    }

    @Override
    public String toString()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void updateName()
    {
        switch(domNode.getNodeType())
        {
            case Node.DOCUMENT_NODE, Node.ENTITY_NODE, Node.ELEMENT_NODE, Node.ENTITY_REFERENCE_NODE -> defaultName = "[E] " + domNode.getNodeName();
            case Node.CDATA_SECTION_NODE, Node.TEXT_NODE -> defaultName = "[T] " + domNode.getNodeValue();
            case Node.COMMENT_NODE -> defaultName = "[C] " + domNode.getNodeValue();
            case Node.ATTRIBUTE_NODE -> defaultName = "[A] " + domNode.getNodeName() + " = " + domNode.getNodeValue();
        }

        name = defaultName;
    }
}
