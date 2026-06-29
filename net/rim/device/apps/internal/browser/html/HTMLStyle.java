package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLStyleElement;

final class HTMLStyle extends HTMLGenericElement implements HTMLStyleElement {
   public HTMLStyle(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 81;
   }

   @Override
   public final boolean getDisabled() {
      return false;
   }

   @Override
   public final void setDisabled(boolean disabled) {
   }

   @Override
   public final String getMedia() {
      return this.getAttributeValue(139);
   }

   @Override
   public final void setMedia(String media) {
      this.setAttributeValue(139, media);
   }

   @Override
   public final String getType() {
      return this.getAttributeValue(190);
   }

   @Override
   public final void setType(String type) {
      this.setAttributeValue(190, type);
   }
}
