package net.rim.device.apps.internal.browser.html;

import net.rim.device.apps.internal.browser.markup.HTMLBinaryConstantsTagProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLElement;

class HTMLGenericElement extends HTMLNode implements HTMLElement, HTMLBinaryConstantsTagProvider, NamedNodeMap {
   public boolean autoAppendStrings() {
      return true;
   }

   public void setAttributeValue(int attribId, int value) {
      super._ir.notReadOnly(super._node);
      super._ir.setAttribute(super._node, super._ir.mapAttributeId(attribId), value);
   }

   public void setAttributeValue(int attribId, String value) {
      super._ir.notReadOnly(super._node);
      super._ir.setAttribute(super._node, super._ir.mapAttributeId(attribId), value);
   }

   public String getAttributeValue(int attribId) {
      return super._ir.getAttributeValue(super._ir.getAttribute(super._node, super._ir.mapAttributeId(attribId)));
   }

   public int getAttributeValueAsInt(int attribId) {
      return super._ir.getAttributeValueAsInt(super._ir.getAttribute(super._node, super._ir.mapAttributeId(attribId)));
   }

   public int getAttributeValueAsPixels(int attribId) {
      return super._ir.getAttributeValueAsPixels(super._ir.getAttribute(super._node, super._ir.mapAttributeId(attribId)));
   }

   public boolean getAttributeValueAsBoolean(int attribId, boolean defaultValue) {
      int id = super._ir.getAttribute(super._node, super._ir.mapAttributeId(attribId));
      return id == 0 ? defaultValue : super._ir.getAttributeValueAsInt(id) != 0;
   }

   @Override
   public String getAttribute(String name) {
      return super._ir.getAttributeValue(super._ir.getAttribute(super._node, name));
   }

   @Override
   public int getTagNameInt() {
      throw null;
   }

   @Override
   public void setAttribute(String name, String value) {
      HTMLDOMInternalRepresentation.isQName(name);
      super._ir.notReadOnly(super._node);
      super._ir.setAttribute(super._node, name, value);
      if ("style:visibility".equals(name)) {
         if ("hidden".equals(value) || "collapse".equals(value)) {
            this.setAdditionalFlags((short)(2048 | this.getFlags() & 1024));
            return;
         }

         if ("visible".equals(value)) {
            this.setAdditionalFlags((short)(this.getFlags() & 1024));
         }
      }
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
      HTMLNode attrNode = (HTMLNode)newAttr;
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
      int iAttr = ((HTMLNode)oldAttr)._node;
      if (super._ir.getParent(iAttr) != super._node) {
         throw new DOMException((short)8, "");
      } else {
         return (Attr)super._ir.getNode(super._ir.removeAttributeNode(super._node, ((HTMLNode)oldAttr)._node));
      }
   }

   @Override
   public NodeList getElementsByTagName(String tagname) {
      return new HTMLNodeList(super._ir.getElementsByTagName(super._node, tagname));
   }

   @Override
   public String getAttributeNS(String namespaceURI, String localName) {
      return super._ir.getAttributeQName(super._ir.getAttributeNS(super._node, namespaceURI, localName));
   }

   @Override
   public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
      HTMLDOMInternalRepresentation.isQName(qualifiedName);
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
      return new HTMLNodeList(super._ir.getElementsByTagNameNS(super._node, namespaceURI, localName));
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
   public String getId() {
      return this.getAttributeValue(129);
   }

   @Override
   public void setId(String id) {
      this.setAttributeValue(129, id);
   }

   @Override
   public String getTitle() {
      return this.getAttributeValue(189);
   }

   @Override
   public void setTitle(String title) {
      this.setAttributeValue(189, title);
   }

   @Override
   public String getLang() {
      return this.getAttributeValue(132);
   }

   @Override
   public void setLang(String lang) {
      this.setAttributeValue(132, lang);
   }

   @Override
   public String getDir() {
      return this.getAttributeValue(116);
   }

   @Override
   public void setDir(String dir) {
      this.setAttributeValue(116, dir);
   }

   @Override
   public String getClassName() {
      return this.getAttributeValue(100);
   }

   @Override
   public void setClassName(String className) {
      this.setAttributeValue(100, className);
   }

   @Override
   public String getTagName() {
      return super._ir.getElementQName(super._node);
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
   public boolean hasAttributes() {
      return super._ir.getAttributes(super._node) != 0;
   }

   public HTMLGenericElement(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public NamedNodeMap getAttributes() {
      return this;
   }

   @Override
   public String getNodeName() {
      return this.getTagName();
   }

   @Override
   public String getLocalName() {
      return super._ir.getElementLocalName(super._node);
   }
}
