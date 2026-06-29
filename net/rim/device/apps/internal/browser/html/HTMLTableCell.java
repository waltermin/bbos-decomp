package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLTableCellElement;

final class HTMLTableCell extends HTMLGenericElement implements HTMLTableCellElement {
   private int _tag;

   public HTMLTableCell(int tag, HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
      this._tag = tag;
   }

   @Override
   public final int getTagNameInt() {
      return this._tag;
   }

   @Override
   public final int getCellIndex() {
      return 0;
   }

   @Override
   public final String getAbbr() {
      return this.getAttributeValue(80);
   }

   @Override
   public final void setAbbr(String abbr) {
      this.setAttributeValue(80, abbr);
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
   public final String getAxis() {
      return this.getAttributeValue(89);
   }

   @Override
   public final void setAxis(String axis) {
      this.setAttributeValue(89, axis);
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
   public final int getColSpan() {
      return this.getAttributeValueAsInt(108);
   }

   @Override
   public final void setColSpan(int colSpan) {
      this.setAttributeValue(108, colSpan);
   }

   @Override
   public final String getHeaders() {
      return this.getAttributeValue(123);
   }

   @Override
   public final void setHeaders(String headers) {
      this.setAttributeValue(123, headers);
   }

   @Override
   public final String getHeight() {
      return this.getAttributeValue(124);
   }

   @Override
   public final void setHeight(String height) {
      this.setAttributeValue(124, height);
   }

   @Override
   public final boolean getNoWrap() {
      return this.getAttributeValueAsBoolean(146, false);
   }

   @Override
   public final void setNoWrap(boolean noWrap) {
      this.setAttributeValue(146, noWrap ? 1 : 0);
   }

   @Override
   public final int getRowSpan() {
      return this.getAttributeValueAsInt(172);
   }

   @Override
   public final void setRowSpan(int rowSpan) {
      this.setAttributeValue(172, rowSpan);
   }

   @Override
   public final String getScope() {
      return this.getAttributeValue(175);
   }

   @Override
   public final void setScope(String scope) {
      this.setAttributeValue(175, scope);
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
