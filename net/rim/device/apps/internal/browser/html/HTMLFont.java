package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLFontElement;

final class HTMLFont extends HTMLGenericElement implements HTMLFontElement {
   public HTMLFont(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 35;
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
   public final String getSize() {
      return this.getAttributeValue(179);
   }

   @Override
   public final void setSize(String size) {
      this.setAttributeValue(179, size);
   }
}
