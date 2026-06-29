package org.w3c.dom;

public interface Node {
   short ELEMENT_NODE;
   short ATTRIBUTE_NODE;
   short TEXT_NODE;
   short CDATA_SECTION_NODE;
   short ENTITY_REFERENCE_NODE;
   short ENTITY_NODE;
   short PROCESSING_INSTRUCTION_NODE;
   short COMMENT_NODE;
   short DOCUMENT_NODE;
   short DOCUMENT_TYPE_NODE;
   short DOCUMENT_FRAGMENT_NODE;
   short NOTATION_NODE;

   String getNodeName();

   String getNodeValue();

   void setNodeValue(String var1);

   short getNodeType();

   Node getParentNode();

   NodeList getChildNodes();

   Node getFirstChild();

   Node getLastChild();

   Node getPreviousSibling();

   Node getNextSibling();

   NamedNodeMap getAttributes();

   Document getOwnerDocument();

   Node insertBefore(Node var1, Node var2);

   Node replaceChild(Node var1, Node var2);

   Node removeChild(Node var1);

   Node appendChild(Node var1);

   boolean hasChildNodes();

   Node cloneNode(boolean var1);

   void normalize();

   boolean isSupported(String var1, String var2);

   String getNamespaceURI();

   String getPrefix();

   void setPrefix(String var1);

   String getLocalName();

   boolean hasAttributes();
}
