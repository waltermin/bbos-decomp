package net.rim.device.apps.internal.browser.html;

import org.w3c.dom.html2.HTMLFieldSetElement;
import org.w3c.dom.html2.HTMLFormElement;

final class HTMLFieldSet extends HTMLGenericElement implements HTMLFieldSetElement {
   private HTMLFormElement _form;

   public final void setForm(HTMLFormElement form) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   @Override
   public final HTMLFormElement getForm() {
      return this._form;
   }

   @Override
   public final int getTagNameInt() {
      return 34;
   }

   public HTMLFieldSet(HTMLDOMInternalRepresentation dom, int nodeId) {
      super(dom, nodeId);
   }
}
