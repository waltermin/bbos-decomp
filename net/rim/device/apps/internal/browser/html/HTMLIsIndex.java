package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLIsIndexElement;

final class HTMLIsIndex extends HTMLGenericElement implements HTMLIsIndexElement {
   public HTMLIsIndex(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }

   @Override
   public final int getTagNameInt() {
      return 53;
   }

   @Override
   public final HTMLFormElement getForm() {
      return null;
   }

   @Override
   public final String getPrompt() {
      return this.getAttributeValue(167);
   }

   @Override
   public final void setPrompt(String prompt) {
      this.setAttributeValue(167, prompt);
   }
}
