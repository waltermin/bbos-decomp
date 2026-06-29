package net.rim.device.api.xml.jaxp;

import net.rim.device.api.util.IntVector;
import net.rim.device.api.util.ToIntHashtable;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class DOMDocumentTypeImpl$NamedNodeMapShim implements NamedNodeMap {
   DOMInternalRepresentation _ir;
   ToIntHashtable _hash;
   IntVector _nodes;

   DOMDocumentTypeImpl$NamedNodeMapShim(DOMInternalRepresentation ir, ToIntHashtable hash, IntVector nodes) {
      this._ir = ir;
      this._hash = hash;
      this._nodes = nodes;
   }

   @Override
   public Node getNamedItem(String name) {
      return this._ir.getNode(this._hash.get(name));
   }

   @Override
   public Node getNamedItemNS(String namespaceURI, String localName) {
      return null;
   }

   @Override
   public Node setNamedItem(Node arg) {
      throw new DOMException((short)7, "");
   }

   @Override
   public Node removeNamedItem(String name) {
      throw new DOMException((short)7, "");
   }

   @Override
   public Node item(int index) {
      try {
         return this._ir.getNode(this._nodes.elementAt(index));
      } finally {
         ;
      }
   }

   @Override
   public int getLength() {
      return this._nodes.size();
   }

   @Override
   public Node setNamedItemNS(Node arg) {
      throw new DOMException((short)7, "");
   }

   @Override
   public Node removeNamedItemNS(String namespaceURI, String localName) {
      throw new DOMException((short)7, "");
   }
}
