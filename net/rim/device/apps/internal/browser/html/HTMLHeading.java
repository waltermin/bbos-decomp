package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLHeadingElement;

final class HTMLHeading extends HTMLGenericElement implements HTMLHeadingElement {
   private int _tag;

   public HTMLHeading(int tag, HTMLDOMInternalRepresentation dom, int nodeId) {
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
}
