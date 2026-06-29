package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLLegendElement;

final class HTMLLegend extends HTMLGenericElement implements HTMLLegendElement {
   public HTMLLegend(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 56;
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
   public final String getAlign() {
      return this.getAttributeValue(85);
   }

   @Override
   public final void setAlign(String align) {
      this.setAttributeValue(85, align);
   }
}
