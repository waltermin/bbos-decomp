package net.rim.device.api.xml.jaxp;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

class DOMTextImpl extends DOMCharacterDataImpl implements Text {
   @Override
   public String getNodeName() {
      return "#text";
   }

   DOMTextImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }

   @Override
   public Text splitText(int offset) {
      super._ir.notReadOnly(super._node);
      Text newNode = (Text)super._ir.getNode(super._ir.splitText(super._node, offset));
      if (newNode == null) {
         throw new DOMException((short)1, "");
      } else {
         return newNode;
      }
   }
}
