package net.rim.device.api.xml.jaxp;

import org.w3c.dom.Entity;

class DOMEntityImpl extends DOMNodeImpl implements Entity {
   DOMEntityImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeName() {
      return super._ir.getEntityName(super._node);
   }

   @Override
   public String getPublicId() {
      return super._ir.getEntityPublicId(super._node);
   }

   @Override
   public String getSystemId() {
      return super._ir.getEntitySystemId(super._node);
   }

   @Override
   public String getNotationName() {
      return super._ir.getEntityNotationName(super._node);
   }
}
