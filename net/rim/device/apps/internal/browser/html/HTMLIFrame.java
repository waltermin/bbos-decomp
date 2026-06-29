package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.Document;
import org.w3c.dom.html2.HTMLIFrameElement;

final class HTMLIFrame extends HTMLGenericElement implements HTMLIFrameElement {
   public HTMLIFrame(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 49;
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
   public final String getFrameBorder() {
      return this.getAttributeValue(122);
   }

   @Override
   public final void setFrameBorder(String frameBorder) {
      this.setAttributeValue(122, frameBorder);
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
   public final String getLongDesc() {
      return this.getAttributeValue(135);
   }

   @Override
   public final void setLongDesc(String longDesc) {
      this.setAttributeValue(135, longDesc);
   }

   @Override
   public final String getMarginHeight() {
      return this.getAttributeValue(136);
   }

   @Override
   public final void setMarginHeight(String marginHeight) {
      this.setAttributeValue(136, marginHeight);
   }

   @Override
   public final String getMarginWidth() {
      return this.getAttributeValue(137);
   }

   @Override
   public final void setMarginWidth(String marginWidth) {
      this.setAttributeValue(137, marginWidth);
   }

   @Override
   public final String getName() {
      return this.getAttributeValue(142);
   }

   @Override
   public final void setName(String name) {
      this.setAttributeValue(142, name);
   }

   @Override
   public final String getScrolling() {
      return this.getAttributeValue(176);
   }

   @Override
   public final void setScrolling(String scrolling) {
      this.setAttributeValue(176, scrolling);
   }

   @Override
   public final String getSrc() {
      return this.getAttributeValue(181);
   }

   @Override
   public final void setSrc(String src) {
      this.setAttributeValue(181, src);
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
   public final Document getContentDocument() {
      return this.getOwnerDocument();
   }
}
