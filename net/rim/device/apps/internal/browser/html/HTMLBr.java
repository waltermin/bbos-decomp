package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLBRElement;

final class HTMLBr extends HTMLGenericElement implements HTMLBRElement {
   public HTMLBr(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 18;
   }

   @Override
   public final String getClear() {
      return this.getAttributeValue(102);
   }

   @Override
   public final void setClear(String clear) {
      this.setAttributeValue(102, clear);
   }
}
