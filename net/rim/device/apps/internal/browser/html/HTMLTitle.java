package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLTitleElement;

final class HTMLTitle extends HTMLGenericElement implements HTMLTitleElement {
   public HTMLTitle(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 91;
   }

   @Override
   public final String getText() {
      HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();
      return parent.getTitle();
   }

   @Override
   public final void setText(String text) {
      HTMLBrowserContent parent = ((HTMLDocumentImpl)this.getOwnerDocument()).getUiPeer();
      parent.setTitle(text);
   }
}
