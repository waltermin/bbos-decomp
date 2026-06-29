package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLAnchorElement;

final class HTMLAnchor extends HTMLGenericElement implements HTMLAnchorElement {
   public HTMLAnchor(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 5;
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
   public final String getCharset() {
      return this.getAttributeValue(97);
   }

   @Override
   public final void setCharset(String charset) {
      this.setAttributeValue(97, charset);
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
   }

   @Override
   public final String getHreflang() {
      return this.getAttributeValue(126);
   }

   @Override
   public final void setHreflang(String hreflang) {
      this.setAttributeValue(126, hreflang);
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
   public final String getRel() {
      return this.getAttributeValue(169);
   }

   @Override
   public final void setRel(String rel) {
      this.setAttributeValue(169, rel);
   }

   @Override
   public final String getRev() {
      return this.getAttributeValue(170);
   }

   @Override
   public final void setRev(String rev) {
      this.setAttributeValue(170, rev);
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
      return this.getAttributeValueAsInt(186);
   }

   @Override
   public final void setTabIndex(int tabIndex) {
      this.setAttributeValue(186, tabIndex);
   }

   @Override
   public final String getTarget() {
      return this.getAttributeValue(187);
   }

   @Override
   public final void setTarget(String target) {
      this.setAttributeValue(187, target);
   }

   @Override
   public final String getType() {
      return this.getAttributeValue(190);
   }

   @Override
   public final void setType(String type) {
      this.setAttributeValue(190, type);
   }

   @Override
   public final void blur() {
   }

   @Override
   public final void focus() {
   }
}
