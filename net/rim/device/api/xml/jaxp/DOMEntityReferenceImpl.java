package net.rim.device.api.xml.jaxp;

import org.w3c.dom.EntityReference;

class DOMEntityReferenceImpl extends DOMNodeImpl implements EntityReference {
   DOMEntityReferenceImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeName() {
      return super._ir.getEntityReferenceName(super._node);
   }

   @Override
   public void setNodeValue(String nodeValue) {
   }
}
