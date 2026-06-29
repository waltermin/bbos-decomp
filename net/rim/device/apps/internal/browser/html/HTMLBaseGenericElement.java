package net.rim.device.apps.internal.browser.html;

final class HTMLBaseGenericElement extends HTMLGenericElement {
   private int _tag;

   public HTMLBaseGenericElement(int tag, HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
      this._tag = tag;
   }

   @Override
   public final int getTagNameInt() {
      return this._tag;
   }
}
