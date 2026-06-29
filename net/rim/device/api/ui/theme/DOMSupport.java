package net.rim.device.api.ui.theme;

import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DOMSupport {
   private static final String FALSE = "false";
   private static final String TRUE = "true";

   private DOMSupport() {
   }

   public static Element getFirstChildNamed(String tagName, Node parent) throws IOException {
      NodeList childNodes = parent.getChildNodes();
      int i = indexOfElement(tagName, childNodes);
      if (i == -1) {
         throw new IOException("no child element named " + tagName);
      } else {
         return (Element)childNodes.item(i);
      }
   }

   public static int indexOfElement(String tagName, NodeList nodes) {
      return indexOfElement(tagName, nodes, 0);
   }

   public static int indexOfElement(String tagName, NodeList nodes, int fromIndex) {
      fromIndex = Math.max(0, fromIndex);

      for (int i = fromIndex; i < nodes.getLength(); i++) {
         if (nodes.item(i).getNodeType() == 1 && tagName.equals(nodes.item(i).getNodeName())) {
            return i;
         }
      }

      return -1;
   }

   public static String getString(Element elem, String attrName) throws IOException {
      if (!elem.hasAttribute(attrName)) {
         throw new IOException("attribute " + attrName + " not specified");
      } else {
         return elem.getAttribute(attrName);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static int getInt(Element elem, String attrName) throws IOException {
      try {
         return Integer.parseInt(getString(elem, attrName));
      } catch (Throwable var5) {
         throw new IOException(nfe.getMessage());
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static int getColor(Element elem, String attrName) throws IOException {
      try {
         return Integer.parseInt(getString(elem, attrName).substring(1), 16);
      } catch (Throwable var5) {
         throw new IOException(nfe.getMessage());
      }
   }

   public static boolean getBoolean(Element elem, String attrName) throws IOException {
      String value = getString(elem, attrName);
      if ("false".equals(value)) {
         return false;
      } else if ("true".equals(value)) {
         return true;
      } else {
         throw new IOException("invalid value " + value + " for " + attrName);
      }
   }
}
