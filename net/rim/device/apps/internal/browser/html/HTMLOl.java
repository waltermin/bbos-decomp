package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLOListElement;

final class HTMLOl extends HTMLGenericElement implements HTMLOListElement {
   public HTMLOl(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 65;
   }

   @Override
   public final boolean getCompact() {
      return this.getAttributeValueAsBoolean(109, false);
   }

   @Override
   public final void setCompact(boolean compact) {
      this.setAttributeValue(109, compact ? 1 : 0);
   }

   @Override
   public final int getStart() {
      return this.getAttributeValueAsInt(183);
   }

   @Override
   public final void setStart(int start) {
      this.setAttributeValue(183, start);
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
