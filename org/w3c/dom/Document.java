package org.w3c.dom;

public interface Document extends Node {
   DocumentType getDoctype();

   DOMImplementation getImplementation();

   Element getDocumentElement();

   Element createElement(String var1);

   DocumentFragment createDocumentFragment();

   Text createTextNode(String var1);

   Comment createComment(String var1);

   CDATASection createCDATASection(String var1);

   ProcessingInstruction createProcessingInstruction(String var1, String var2);

   Attr createAttribute(String var1);

   EntityReference createEntityReference(String var1);

   NodeList getElementsByTagName(String var1);

   Node importNode(Node var1, boolean var2);

   Element createElementNS(String var1, String var2);

   Attr createAttributeNS(String var1, String var2);

   NodeList getElementsByTagNameNS(String var1, String var2);

   Element getElementById(String var1);
}
