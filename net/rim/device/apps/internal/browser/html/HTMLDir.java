package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLDirectoryElement;

final class HTMLDir extends HTMLGenericElement implements HTMLDirectoryElement {
   public HTMLDir(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 29;
   }

   @Override
   public final boolean getCompact() {
      return this.getAttributeValueAsBoolean(109, false);
   }

   @Override
   public final void setCompact(boolean compact) {
      this.setAttributeValue(109, compact ? 1 : 0);
   }
}
