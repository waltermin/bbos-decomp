package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLBaseFontElement;

final class HTMLBaseFont extends HTMLGenericElement implements HTMLBaseFontElement {
   public HTMLBaseFont(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 13;
   }

   @Override
   public final String getColor() {
      return this.getAttributeValue(106);
   }

   @Override
   public final void setColor(String color) {
      this.setAttributeValue(106, color);
   }

   @Override
   public final String getFace() {
      return this.getAttributeValue(119);
   }

   @Override
   public final void setFace(String face) {
      this.setAttributeValue(119, face);
   }

   @Override
   public final int getSize() {
      return this.getAttributeValueAsInt(179);
   }

   @Override
   public final void setSize(int size) {
      this.setAttributeValue(179, size);
   }
}
