package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLMetaElement;

final class HTMLMeta extends HTMLGenericElement implements HTMLMetaElement {
   public HTMLMeta(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 61;
   }

   @Override
   public final String getContent() {
      return this.getAttributeValue(110);
   }

   @Override
   public final void setContent(String content) {
      this.setAttributeValue(110, content);
   }

   @Override
   public final String getHttpEquiv() {
      return this.getAttributeValue(128);
   }

   @Override
   public final void setHttpEquiv(String httpEquiv) {
      this.setAttributeValue(128, httpEquiv);
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
   public final String getScheme() {
      return this.getAttributeValue(174);
   }

   @Override
   public final void setScheme(String scheme) {
      this.setAttributeValue(174, scheme);
   }
}
