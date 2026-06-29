package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLHtmlElement;

final class HTMLHtml extends HTMLGenericElement implements HTMLHtmlElement {
   public HTMLHtml(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 47;
   }

   @Override
   public final String getVersion() {
      return this.getAttributeValue(195);
   }

   @Override
   public final void setVersion(String version) {
      this.setAttributeValue(195, version);
   }
}
