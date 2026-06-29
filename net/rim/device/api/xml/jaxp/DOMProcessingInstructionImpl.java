package net.rim.device.api.xml.jaxp;

import org.w3c.dom.ProcessingInstruction;

class DOMProcessingInstructionImpl extends DOMNodeImpl implements ProcessingInstruction {
   DOMProcessingInstructionImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public String getNodeName() {
      return this.getTarget();
   }

   @Override
   public String getNodeValue() {
      return this.getData();
   }

   @Override
   public void setNodeValue(String nodeValue) {
      this.setData(nodeValue);
   }

   @Override
   public String getTarget() {
      return super._ir.getPITarget(super._node);
   }

   @Override
   public String getData() {
      return super._ir.getPIData(super._node);
   }

   @Override
   public void setData(String data) {
      super._ir.notReadOnly(super._node);
      super._ir.setPIData(super._node, data);
   }
}
