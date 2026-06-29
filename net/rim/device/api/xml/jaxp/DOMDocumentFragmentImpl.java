package net.rim.device.api.xml.jaxp;

import org.w3c.dom.DocumentFragment;

class DOMDocumentFragmentImpl extends DOMNodeImpl implements DocumentFragment {
   @Override
   public String getNodeName() {
      return "#document-fragment";
   }

   DOMDocumentFragmentImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }
}
