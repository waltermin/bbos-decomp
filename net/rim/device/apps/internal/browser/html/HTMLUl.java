package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLUListElement;

final class HTMLUl extends HTMLGenericElement implements HTMLUListElement {
   public HTMLUl(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 95;
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
   public final String getType() {
      return this.getAttributeValue(190);
   }

   @Override
   public final void setType(String type) {
      this.setAttributeValue(190, type);
   }
}
