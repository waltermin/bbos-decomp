package net.rim.device.api.xml.jaxp;

import org.w3c.dom.Notation;

class DOMNotationImpl extends DOMNodeImpl implements Notation {
   DOMNotationImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeName() {
      return super._ir.getNotationName(super._node);
   }

   @Override
   public String getPublicId() {
      return super._ir.getNotationPublicId(super._node);
   }

   @Override
   public String getSystemId() {
      return super._ir.getNotationSystemId(super._node);
   }
}
