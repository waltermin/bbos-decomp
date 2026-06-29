package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLLinkElement;

final class HTMLLink extends HTMLGenericElement implements HTMLLinkElement {
   public HTMLLink(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 58;
   }

   @Override
   public final boolean getDisabled() {
      return this.getAttributeValueAsBoolean(117, false);
   }

   @Override
   public final void setDisabled(boolean disabled) {
      this.setAttributeValue(117, disabled ? 1 : 0);
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
   public final String getHref() {
      return this.getAttributeValue(125);
   }

   @Override
   public final void setHref(String href) {
      this.setAttributeValue(125, href);
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
   public final String getMedia() {
      return this.getAttributeValue(139);
   }

   @Override
   public final void setMedia(String media) {
      this.setAttributeValue(139, media);
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
}
