package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLTableColElement;

final class HTMLTableCol extends HTMLGenericElement implements HTMLTableColElement {
   public HTMLTableCol(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 24;
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
   public final String getCh() {
      return this.getAttributeValue(95);
   }

   @Override
   public final void setCh(String ch) {
      this.setAttributeValue(95, ch);
   }

   @Override
   public final String getChOff() {
      return this.getAttributeValue(96);
   }

   @Override
   public final void setChOff(String chOff) {
      this.setAttributeValue(96, chOff);
   }

   @Override
   public final int getSpan() {
      return this.getAttributeValueAsInt(180);
   }

   @Override
   public final void setSpan(int span) {
      this.setAttributeValue(180, span);
   }

   @Override
   public final String getVAlign() {
      return this.getAttributeValue(192);
   }

   @Override
   public final void setVAlign(String vAlign) {
      this.setAttributeValue(192, vAlign);
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
