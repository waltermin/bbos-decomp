package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLTableSectionElement;

final class HTMLTableSection extends HTMLGenericElement implements HTMLTableSectionElement {
   private int _tag;

   public HTMLTableSection(int tag, HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
      this._tag = tag;
   }

   @Override
   public final int getTagNameInt() {
      return this._tag;
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
   public final HTMLCollection getRows() {
      return null;
   }

   @Override
   public final HTMLElement insertRow(int index) {
      return null;
   }

   @Override
   public final void deleteRow(int index) {
   }
}
