package main.domain;

import org.w3c.dom.Node;

public class NodeContainer
{
    public final Node domNode;

    public NodeContainer(Node domNode)
    {
        this.domNode = domNode;
    }

    @Override
    public String toString()
    {
        String name = "";

        switch (domNode.getNodeType())
        {
            case Node.DOCUMENT_NODE, Node.ENTITY_NODE, Node.ELEMENT_NODE, Node.ENTITY_REFERENCE_NODE -> name = "[E] " + domNode.getNodeName();
            case Node.CDATA_SECTION_NODE, Node.TEXT_NODE -> name = "[T] " + domNode.getNodeValue();
            case Node.COMMENT_NODE -> name = "[C] " + domNode.getNodeValue();
            case Node.ATTRIBUTE_NODE -> name = "[A] " + domNode.getNodeName() + " = " + domNode.getNodeValue();
        }

        return name;
    }
}
