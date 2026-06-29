package net.rim.device.api.xml.jaxp;

import java.util.Vector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DOMNodeListImpl implements NodeList {
   private Vector _v;

   void append(Node n) {
      this._v.addElement(n);
   }

   @Override
   public int getLength() {
      return this._v.size();
   }

   @Override
   public Node item(int index) {
      try {
         return (Node)this._v.elementAt(index);
      } finally {
         ;
      }
   }

   DOMNodeListImpl(Vector v) {
      this._v = v;
   }

   DOMNodeListImpl() {
      this(new Vector());
   }
}
