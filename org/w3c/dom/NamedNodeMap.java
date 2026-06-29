package org.w3c.dom;

public interface NamedNodeMap {
   Node getNamedItem(String var1);

   Node setNamedItem(Node var1);

   Node removeNamedItem(String var1);

   Node item(int var1);

   int getLength();

   Node getNamedItemNS(String var1, String var2);

   Node setNamedItemNS(Node var1);

   Node removeNamedItemNS(String var1, String var2);
}
