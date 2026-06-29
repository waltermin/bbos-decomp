package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLFrameSetElement;

final class HTMLFrameSet extends HTMLGenericElement implements HTMLFrameSetElement {
   public HTMLFrameSet(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 38;
   }

   @Override
   public final String getCols() {
      return this.getAttributeValue(107);
   }

   @Override
   public final void setCols(String cols) {
      this.setAttributeValue(107, cols);
   }

   @Override
   public final String getRows() {
      return this.getAttributeValue(171);
   }

   @Override
   public final void setRows(String rows) {
      this.setAttributeValue(171, rows);
   }
}
