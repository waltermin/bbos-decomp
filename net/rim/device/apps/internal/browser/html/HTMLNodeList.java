package net.rim.device.apps.internal.browser.html;

import java.util.Vector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class HTMLNodeList implements NodeList {
   private Vector _v;

   final void append(Node n) {
      this._v.addElement(n);
   }

   @Override
   public final int getLength() {
      return this._v.size();
   }

   @Override
   public final Node item(int index) {
      try {
         return (Node)this._v.elementAt(index);
      } finally {
         ;
      }
   }

   HTMLNodeList(Vector v) {
      this._v = v;
   }

   HTMLNodeList() {
      this(new Vector());
   }
}
