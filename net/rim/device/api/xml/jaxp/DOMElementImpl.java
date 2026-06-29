package net.rim.device.api.xml.jaxp;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DOMElementImpl extends DOMNodeImpl implements Element, NamedNodeMap {
   DOMElementImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeName() {
      return this.getTagName();
   }

   @Override
   public NamedNodeMap getAttributes() {
      return this;
   }

   @Override
   public String getTagName() {
      return super._ir.getElementQName(super._node);
   }

   @Override
   public String getNamespaceURI() {
      return super._ir.getElementURI(super._node);
   }

   @Override
   public String getLocalName() {
      return super._ir.getElementLocalName(super._node);
   }

   @Override
   public String getPrefix() {
      return super._ir.getElementPrefix(super._node);
   }

   @Override
   public String getAttribute(String name) {
      return super._ir.getAttributeValue(super._ir.getAttribute(super._node, name));
   }

   @Override
   public void setAttribute(String name, String value) {
      DOMInternalRepresentation.isQName(name);
      super._ir.notReadOnly(super._node);
      super._ir.setAttribute(super._node, name, value);
   }

   @Override
   public void removeAttribute(String name) {
      super._ir.notReadOnly(super._node);
      super._ir.removeAttribute(super._node, name);
   }

   @Override
   public Attr getAttributeNode(String name) {
      return (Attr)super._ir.getNode(super._ir.getAttribute(super._node, name));
   }

   @Override
   public Attr setAttributeNode(Attr newAttr) {
      DOMNodeImpl attrNode = (DOMNodeImpl)newAttr;
      if (super._ir != attrNode._ir) {
         throw new DOMException((short)4, "");
      } else {
         super._ir.notReadOnly(super._node);
         Node parent = super._ir.getNode(super._ir.getParent(attrNode._node));
         if (parent == this) {
            return newAttr;
         } else if (parent != null) {
            throw new DOMException((short)10, "");
         } else {
            return (Attr)super._ir.getNode(super._ir.setAttributeNode(super._node, attrNode._node));
         }
      }
   }

   @Override
   public Attr removeAttributeNode(Attr oldAttr) {
      super._ir.notReadOnly(super._node);
      int iAttr = ((DOMNodeImpl)oldAttr)._node;
      if (super._ir.getParent(iAttr) != super._node) {
         throw new DOMException((short)8, "");
      } else {
         return (Attr)super._ir.getNode(super._ir.removeAttributeNode(super._node, ((DOMNodeImpl)oldAttr)._node));
      }
   }

   @Override
   public NodeList getElementsByTagName(String tagname) {
      return new DOMNodeListImpl(super._ir.getElementsByTagName(super._node, tagname));
   }

   @Override
   public String getAttributeNS(String namespaceURI, String localName) {
      return super._ir.getAttributeQName(super._ir.getAttributeNS(super._node, namespaceURI, localName));
   }

   @Override
   public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
      DOMInternalRepresentation.isQName(qualifiedName);
      super._ir.notReadOnly(super._node);
      super._ir.setAttributeNS(super._node, namespaceURI, qualifiedName, value);
   }

   @Override
   public void removeAttributeNS(String namespaceURI, String localName) {
      super._ir.notReadOnly(super._node);
      super._ir.removeAttributeNS(super._node, namespaceURI, localName);
   }

   @Override
   public Attr getAttributeNodeNS(String namespaceURI, String localName) {
      return (Attr)super._ir.getNode(super._ir.getAttributeNS(super._node, namespaceURI, localName));
   }

   @Override
   public Attr setAttributeNodeNS(Attr newAttr) {
      super._ir.notReadOnly(super._node);
      return this.setAttributeNode(newAttr);
   }

   @Override
   public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
      return new DOMNodeListImpl(super._ir.getElementsByTagNameNS(super._node, namespaceURI, localName));
   }

   @Override
   public boolean hasAttribute(String name) {
      return super._ir.getAttribute(super._node, name) != 0;
   }

   @Override
   public boolean hasAttributeNS(String namespaceURI, String localName) {
      return super._ir.getAttributeNS(super._node, namespaceURI, localName) != 0;
   }

   @Override
   public boolean hasAttributes() {
      return super._ir.getAttributes(super._node) != 0;
   }

   @Override
   public Node getNamedItem(String name) {
      return this.getAttributeNode(name);
   }

   @Override
   public Node setNamedItem(Node arg) {
      super._ir.notReadOnly(super._node);
      if (!(arg instanceof Attr)) {
         throw new DOMException((short)3, "");
      } else {
         return this.setAttributeNode((Attr)arg);
      }
   }

   @Override
   public Node removeNamedItem(String name) {
      super._ir.notReadOnly(super._node);
      int removed = super._ir.removeAttribute(super._node, name);
      if (removed == 0) {
         throw new DOMException((short)8, "");
      } else {
         return super._ir.getNode(removed);
      }
   }

   @Override
   public Node item(int index) {
      for (int attr = super._ir.getElementAttributes(super._node); attr != 0; attr = super._ir.getNextChild(attr)) {
         if (--index < 0) {
            return super._ir.getNode(attr);
         }
      }

      return null;
   }

   @Override
   public int getLength() {
      int length = 0;

      for (int attr = super._ir.getElementAttributes(super._node); attr != 0; attr = super._ir.getNextChild(attr)) {
         length++;
      }

      return length;
   }

   @Override
   public Node getNamedItemNS(String namespaceURI, String localName) {
      return super._ir.getNode(super._ir.getAttributeNS(super._node, namespaceURI, localName));
   }

   @Override
   public Node setNamedItemNS(Node arg) {
      super._ir.notReadOnly(super._node);
      if (!(arg instanceof Attr)) {
         throw new DOMException((short)3, "");
      } else {
         return this.setAttributeNodeNS((Attr)arg);
      }
   }

   @Override
   public Node removeNamedItemNS(String namespaceURI, String localName) {
      super._ir.notReadOnly(super._node);
      int removed = super._ir.removeAttributeNS(super._node, namespaceURI, localName);
      if (removed == 0) {
         throw new DOMException((short)8, "");
      } else {
         return super._ir.getNode(removed);
      }
   }

   @Override
   public void setPrefix(String prefix) {
      super._ir.notReadOnly(super._node);
      DOMInternalRepresentation.isNCName(prefix);
      super._ir.setElementQName(super._node, prefix + ":" + super._ir.getElementLocalName(super._node));
   }
}
