package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLLabelElement;

final class ESHTMLLabelElement extends ESHTMLElement {
   ESHTMLLabelElement(HTMLLabelElement element) {
      super(element, Names.HTMLLabelElement);
   }

   final HTMLLabelElement getLabelElement() {
      return (HTMLLabelElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.form) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getLabelElement().getForm());
      } else if (name == Names.accessKey) {
         return JavaScriptEngine.makeStringValue(this.getLabelElement().getAccessKey());
      } else {
         return name == Names.htmlFor ? JavaScriptEngine.makeStringValue(this.getLabelElement().getHtmlFor()) : super.requestFieldValue(name);
      }
   }
}
