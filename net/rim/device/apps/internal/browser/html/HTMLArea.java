package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLAreaElement;

final class HTMLArea extends HTMLGenericElement implements HTMLAreaElement {
   public HTMLArea(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 10;
   }

   @Override
   public final String getAccessKey() {
      return this.getAttributeValue(83);
   }

   @Override
   public final void setAccessKey(String accessKey) {
      this.setAttributeValue(83, accessKey);
   }

   @Override
   public final String getAlt() {
      return this.getAttributeValue(87);
   }

   @Override
   public final void setAlt(String alt) {
      this.setAttributeValue(87, alt);
   }

   @Override
   public final String getCoords() {
      return this.getAttributeValue(111);
   }

   @Override
   public final void setCoords(String coords) {
      this.setAttributeValue(111, coords);
   }

   @Override
   public final String getHref() {
      return this.getAttributeValue(125);
   }

   @Override
   public final void setHref(String href) {
      this.setAttributeValue(125, href);
   }

   @Override
   public final boolean getNoHref() {
      return this.getAttributeValueAsBoolean(143, false);
   }

   @Override
   public final void setNoHref(boolean noHref) {
      this.setAttributeValue(143, noHref ? 1 : 0);
   }

   @Override
   public final String getShape() {
      return this.getAttributeValue(178);
   }

   @Override
   public final void setShape(String shape) {
      this.setAttributeValue(178, shape);
   }

   @Override
   public final int getTabIndex() {
      return 0;
   }

   @Override
   public final void setTabIndex(int tabIndex) {
   }

   @Override
   public final String getTarget() {
      return this.getAttributeValue(187);
   }

   @Override
   public final void setTarget(String target) {
      this.setAttributeValue(187, target);
   }
}
