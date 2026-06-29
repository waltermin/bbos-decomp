package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLTextAreaElement;

final class HTMLTextArea extends HTMLInput implements HTMLTextAreaElement {
   private String _defaultValue;

   public HTMLTextArea(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(87, dom, nodeId);
   }

   @Override
   public final int getCols() {
      return this.getAttributeValueAsInt(107);
   }

   @Override
   public final void setCols(int cols) {
      this.setAttributeValue(107, cols);
   }

   @Override
   public final int getRows() {
      return this.getAttributeValueAsInt(171);
   }

   @Override
   public final void setRows(int rows) {
      this.setAttributeValue(171, rows);
   }

   @Override
   public final String getDefaultValue() {
      return this._defaultValue == null ? "" : this._defaultValue;
   }

   @Override
   public final void setDefaultValue(String defaultValue) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
