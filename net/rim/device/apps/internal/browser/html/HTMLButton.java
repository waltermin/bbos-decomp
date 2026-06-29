package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLButtonElement;

final class HTMLButton extends HTMLInput implements HTMLButtonElement {
   public HTMLButton(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(19, dom, nodeId);
   }

   @Override
   public final boolean getDisabled() {
      return (((HTMLButtonField)super._uiPeer).getStyle() & 45035996273704960L) != 0;
   }

   @Override
   public final void setDisabled(boolean disabled) {
   }

   @Override
   public final boolean autoAppendStrings() {
      return false;
   }
}
