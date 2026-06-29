package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;

class HTMLDocumentType extends HTMLNode implements DocumentType {
   HTMLDocumentType(HTMLDOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeName() {
      return this.getName();
   }

   @Override
   public String getName() {
      return super._ir.getDocumentTypeName(super._node);
   }

   @Override
   public NamedNodeMap getEntities() {
      return new HTMLDocumentType$NamedNodeMapShim(super._ir, super._ir.getEntityHash(), super._ir.getEntities());
   }

   @Override
   public NamedNodeMap getNotations() {
      return new HTMLDocumentType$NamedNodeMapShim(super._ir, super._ir.getNotationHash(), super._ir.getNotations());
   }

   @Override
   public String getPublicId() {
      return super._ir.getDocumentTypePublicId(super._node);
   }

   @Override
   public String getSystemId() {
      return super._ir.getDocumentTypeSystemId(super._node);
   }

   @Override
   public String getInternalSubset() {
      return super._ir.getDocumentTypeBody(super._node);
   }
}
