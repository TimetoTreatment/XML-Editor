package main.domain;

import org.w3c.dom.Node;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode
{
    Node domNode;

    public TreeNode(Node domNode)
    {
        this.domNode = domNode;
    }

    @Override
    public String toString()
    {
        String str = "";
        
        switch(domNode.getNodeType())
        {
            case Node.DOCUMENT_NODE, Node.ENTITY_NODE, Node.ELEMENT_NODE, Node.ENTITY_REFERENCE_NODE -> str = "[E] " + domNode.getNodeName();
            case Node.CDATA_SECTION_NODE, Node.TEXT_NODE -> str = "[T] " + domNode.getNodeValue();
            case Node.COMMENT_NODE -> str = "[C] " + domNode.getNodeValue();
            case Node.ATTRIBUTE_NODE -> str = "[A] " + domNode.getNodeName() + " = " + domNode.getNodeValue();
        }
        
        return str;
    }
}
