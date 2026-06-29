package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLHeadElement;

final class HTMLHead extends HTMLGenericElement implements HTMLHeadElement {
   public HTMLHead(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 45;
   }

   @Override
   public final String getProfile() {
      return this.getAttributeValue(166);
   }

   @Override
   public final void setProfile(String profile) {
      this.setAttributeValue(166, profile);
   }
}
