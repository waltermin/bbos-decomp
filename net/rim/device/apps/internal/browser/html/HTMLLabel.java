package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLLabelElement;

final class HTMLLabel extends HTMLGenericElement implements HTMLLabelElement {
   public HTMLLabel(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 55;
   }

   @Override
   public final HTMLFormElement getForm() {
      return null;
   }

   @Override
   public final String getAccessKey() {
      return this.getAttributeValue(83);
   }

   @Override
   public final void setAccessKey(String accessKey) {
      this.setAttributeValue(83, accessKey);
   }

   @Override
   public final String getHtmlFor() {
      return this.getAttributeValue(120);
   }

   @Override
   public final void setHtmlFor(String htmlFor) {
      this.setAttributeValue(120, htmlFor);
   }
}
