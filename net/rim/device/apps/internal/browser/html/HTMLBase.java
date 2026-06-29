package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLBaseElement;

final class HTMLBase extends HTMLGenericElement implements HTMLBaseElement {
   public HTMLBase(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 12;
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
   public final String getTarget() {
      return this.getAttributeValue(187);
   }

   @Override
   public final void setTarget(String target) {
      this.setAttributeValue(187, target);
   }
}
