package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLDivElement;

final class HTMLDiv extends HTMLGenericElement implements HTMLDivElement {
   public HTMLDiv(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 30;
   }

   @Override
   public final String getAlign() {
      return this.getAttributeValue(85);
   }

   @Override
   public final void setAlign(String align) {
      this.setAttributeValue(85, align);
   }
}
