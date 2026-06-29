package net.rim.device.api.xml.jaxp;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class DOMAttrImpl extends DOMNodeImpl implements Attr {
   DOMAttrImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public Node getParentNode() {
      return null;
   }

   @Override
   public Node getNextSibling() {
      return null;
   }

   @Override
   public Node getPreviousSibling() {
      return null;
   }

   @Override
   public String getNodeName() {
      return this.getName();
   }

   @Override
   public String getNodeValue() {
      return this.getValue();
   }

   @Override
   public void setNodeValue(String nodeValue) {
      this.setValue(nodeValue);
   }

   @Override
   public String getName() {
      return super._ir.getAttributeQName(super._node);
   }

   @Override
   public String getNamespaceURI() {
      return super._ir.getAttributeURI(super._node);
   }

   @Override
   public String getLocalName() {
      return super._ir.getAttributeLocalName(super._node);
   }

   @Override
   public String getPrefix() {
      return super._ir.getAttributePrefix(super._node);
   }

   @Override
   public boolean getSpecified() {
      return super._ir.getAttributeIsSpecified(super._node);
   }

   @Override
   public String getValue() {
      return super._ir.getAttributeValue(super._node);
   }

   @Override
   public void setValue(String value) {
      super._ir.notReadOnly(super._node);
      super._ir.setAttributeValue(super._node, value);
   }

   @Override
   public Element getOwnerElement() {
      return (Element)super._ir.getNode(super._ir.getParent(super._node));
   }

   @Override
   public void setPrefix(String prefix) {
      super._ir.notReadOnly(super._node);
      DOMInternalRepresentation.isNCName(prefix);
      super._ir
         .setAttributeQName(
            super._node, ((StringBuffer)(new Object())).append(prefix).append(":").append(super._ir.getAttributeLocalName(super._node)).toString()
         );
   }

   @Override
   public boolean isId() {
      return super._ir.getAttributeType(super._node).equals("ID");
   }
}
