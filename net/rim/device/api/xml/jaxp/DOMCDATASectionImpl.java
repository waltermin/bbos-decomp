package net.rim.device.api.xml.jaxp;

import org.w3c.dom.CDATASection;

class DOMCDATASectionImpl extends DOMTextImpl implements CDATASection {
   @Override
   public String getNodeName() {
      return "#cdata-section";
   }

   DOMCDATASectionImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }
}
