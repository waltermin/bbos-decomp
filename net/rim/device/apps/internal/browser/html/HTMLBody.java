package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLBodyElement;

final class HTMLBody extends HTMLGenericElement implements HTMLBodyElement {
   public HTMLBody(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 17;
   }

   @Override
   public final String getALink() {
      return this.getAttributeValue(86);
   }

   @Override
   public final void setALink(String aLink) {
      this.setAttributeValue(86, aLink);
   }

   @Override
   public final String getBackground() {
      return this.getAttributeValue(90);
   }

   @Override
   public final void setBackground(String background) {
      this.setAttributeValue(90, background);
   }

   @Override
   public final String getBgColor() {
      return this.getAttributeValue(91);
   }

   @Override
   public final void setBgColor(String bgColor) {
      this.setAttributeValue(91, bgColor);
   }

   @Override
   public final String getLink() {
      return this.getAttributeValue(134);
   }

   @Override
   public final void setLink(String link) {
      this.setAttributeValue(134, link);
   }

   @Override
   public final String getText() {
      return this.getAttributeValue(188);
   }

   @Override
   public final void setText(String text) {
      this.setAttributeValue(188, text);
   }

   @Override
   public final String getVLink() {
      return this.getAttributeValue(196);
   }

   @Override
   public final void setVLink(String vLink) {
      this.setAttributeValue(196, vLink);
   }
}
