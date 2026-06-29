package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLHRElement;

final class HTMLHr extends HTMLGenericElement implements HTMLHRElement {
   public HTMLHr(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 46;
   }

   @Override
   public final String getAlign() {
      return this.getAttributeValue(85);
   }

   @Override
   public final void setAlign(String align) {
      this.setAttributeValue(85, align);
   }

   @Override
   public final boolean getNoShade() {
      return this.getAttributeValueAsBoolean(145, false);
   }

   @Override
   public final void setNoShade(boolean noShade) {
      this.setAttributeValue(145, noShade ? 1 : 0);
   }

   @Override
   public final String getSize() {
      return this.getAttributeValue(179);
   }

   @Override
   public final void setSize(String size) {
      this.setAttributeValue(85, size);
   }

   @Override
   public final String getWidth() {
      return this.getAttributeValue(198);
   }

   @Override
   public final void setWidth(String width) {
      this.setAttributeValue(198, width);
   }
}
