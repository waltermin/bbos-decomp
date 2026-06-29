package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLParagraphElement;

final class HTMLParagraph extends HTMLGenericElement implements HTMLParagraphElement {
   public HTMLParagraph(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 68;
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
