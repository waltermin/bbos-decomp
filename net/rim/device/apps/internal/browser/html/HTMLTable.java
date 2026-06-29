package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLElement;
import org.w3c.dom.html2.HTMLTableCaptionElement;
import org.w3c.dom.html2.HTMLTableElement;
import org.w3c.dom.html2.HTMLTableSectionElement;

final class HTMLTable extends HTMLGenericElement implements HTMLTableElement {
   public HTMLTable(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 84;
   }

   @Override
   public final HTMLTableCaptionElement getCaption() {
      return null;
   }

   @Override
   public final void setCaption(HTMLTableCaptionElement caption) {
   }

   @Override
   public final HTMLTableSectionElement getTHead() {
      return null;
   }

   @Override
   public final void setTHead(HTMLTableSectionElement tHead) {
   }

   @Override
   public final HTMLTableSectionElement getTFoot() {
      return null;
   }

   @Override
   public final void setTFoot(HTMLTableSectionElement tFoot) {
   }

   @Override
   public final HTMLCollection getRows() {
      return null;
   }

   @Override
   public final HTMLCollection getTBodies() {
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
   public final String getBorder() {
      return this.getAttributeValue(92);
   }

   @Override
   public final void setBorder(String border) {
      this.setAttributeValue(92, border);
   }

   @Override
   public final String getCellPadding() {
      return this.getAttributeValue(93);
   }

   @Override
   public final void setCellPadding(String cellPadding) {
      this.setAttributeValue(93, cellPadding);
   }

   @Override
   public final String getCellSpacing() {
      return this.getAttributeValue(94);
   }

   @Override
   public final void setCellSpacing(String cellSpacing) {
      this.setAttributeValue(94, cellSpacing);
   }

   @Override
   public final String getFrame() {
      return this.getAttributeValue(121);
   }

   @Override
   public final void setFrame(String frame) {
      this.setAttributeValue(121, frame);
   }

   @Override
   public final String getRules() {
      return this.getAttributeValue(173);
   }

   @Override
   public final void setRules(String rules) {
      this.setAttributeValue(173, rules);
   }

   @Override
   public final String getSummary() {
      return this.getAttributeValue(185);
   }

   @Override
   public final void setSummary(String summary) {
      this.setAttributeValue(185, summary);
   }

   @Override
   public final String getWidth() {
      return this.getAttributeValue(198);
   }

   @Override
   public final void setWidth(String width) {
      this.setAttributeValue(198, width);
   }

   @Override
   public final HTMLElement createTHead() {
      return null;
   }

   @Override
   public final void deleteTHead() {
   }

   @Override
   public final HTMLElement createTFoot() {
      return null;
   }

   @Override
   public final void deleteTFoot() {
   }

   @Override
   public final HTMLElement createCaption() {
      return null;
   }

   @Override
   public final void deleteCaption() {
   }

   @Override
   public final HTMLElement insertRow(int index) {
      return null;
   }

   @Override
   public final void deleteRow(int index) {
   }
}
