package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLFieldSetElement;

final class ESHTMLFieldSetElement extends ESHTMLElement {
   ESHTMLFieldSetElement(HTMLFieldSetElement element) {
      super(element, Names.HTMLFieldSetElement);
   }

   final HTMLFieldSetElement getFieldSetElement() {
      return (HTMLFieldSetElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.form
         ? JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getFieldSetElement().getForm())
         : super.requestFieldValue(name);
   }
}
