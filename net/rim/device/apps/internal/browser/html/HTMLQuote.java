package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLQuoteElement;

final class HTMLQuote extends HTMLGenericElement implements HTMLQuoteElement {
   public HTMLQuote(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 72;
   }

   @Override
   public final String getCite() {
      return this.getAttributeValue(99);
   }

   @Override
   public final void setCite(String cite) {
      this.setAttributeValue(99, cite);
   }
}
