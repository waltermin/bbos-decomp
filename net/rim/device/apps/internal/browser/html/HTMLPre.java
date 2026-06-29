package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLPreElement;

final class HTMLPre extends HTMLGenericElement implements HTMLPreElement {
   public HTMLPre(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 71;
   }

   @Override
   public final int getWidth() {
      return this.getAttributeValueAsInt(198);
   }

   @Override
   public final void setWidth(int width) {
      this.setAttributeValue(198, width);
   }
}
