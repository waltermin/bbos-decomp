package net.rim.device.api.xml.jaxp;

import org.w3c.dom.Comment;

class DOMCommentImpl extends DOMCharacterDataImpl implements Comment {
   @Override
   public String getNodeName() {
      return "#comment";
   }

   DOMCommentImpl(DOMInternalRepresentation ir, int node) {
      super(ir, node);
   }
}
