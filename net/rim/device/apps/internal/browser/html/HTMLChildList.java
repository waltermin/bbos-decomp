package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class HTMLChildList implements NodeList {
   HTMLDOMInternalRepresentation _ir;
   int _node;

   HTMLChildList(HTMLNode domNode) {
      this._ir = domNode.getIR();
      this._node = domNode.getNode();
   }

   @Override
   public Node item(int index) {
      for (int child = this._ir.getFirstChild(this._node); child != 0; child = this._ir.getNextChild(child)) {
         if (--index < 0) {
            return this._ir.getNode(child);
         }
      }

      return null;
   }

   @Override
   public int getLength() {
      int length = 0;

      for (int child = this._ir.getFirstChild(this._node); child != 0; child = this._ir.getNextChild(child)) {
         length++;
      }

      return length;
   }
}
