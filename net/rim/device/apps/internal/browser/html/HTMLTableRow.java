package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLTableRowElement;

final class HTMLTableRow extends HTMLGenericElement implements HTMLTableRowElement {
   public HTMLTableRow(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 92;
   }

   @Override
   public final int getRowIndex() {
      return 0;
   }

   @Override
   public final int getSectionRowIndex() {
      return 0;
   }

   @Override
   public final HTMLCollection getCells() {
      return null;
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
   public final String getVAlign() {
      return this.getAttributeValue(192);
   }

   @Override
   public final void setVAlign(String vAlign) {
      this.setAttributeValue(192, vAlign);
   }

   @Override
   public final HTMLElement insertCell(int index) {
      return null;
   }

   @Override
   public final void deleteCell(int index) {
   }
}
