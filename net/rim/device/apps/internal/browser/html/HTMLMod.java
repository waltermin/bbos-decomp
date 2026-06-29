package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLModElement;

final class HTMLMod extends HTMLGenericElement implements HTMLModElement {
   private int _tag;

   public HTMLMod(int tag, HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
      this._tag = tag;
   }

   @Override
   public final int getTagNameInt() {
      return this._tag;
   }

   @Override
   public final String getCite() {
      return this.getAttributeValue(99);
   }

   @Override
   public final void setCite(String cite) {
      this.setAttributeValue(99, cite);
   }

   @Override
   public final String getDateTime() {
      return this.getAttributeValue(113);
   }

   @Override
   public final void setDateTime(String dateTime) {
      this.setAttributeValue(113, dateTime);
   }
}
