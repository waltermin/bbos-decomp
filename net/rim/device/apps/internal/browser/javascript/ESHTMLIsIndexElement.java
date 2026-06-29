package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLIsIndexElement;

final class ESHTMLIsIndexElement extends ESHTMLElement {
   ESHTMLIsIndexElement(HTMLIsIndexElement element) {
      super(element, Names.HTMLIsIndexElement);
   }

   final HTMLIsIndexElement getIsIndexElement() {
      return (HTMLIsIndexElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.prompt) {
         return JavaScriptEngine.makeStringValue(this.getIsIndexElement().getPrompt());
      } else {
         return name == Names.form
            ? JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getIsIndexElement().getForm())
            : super.requestFieldValue(name);
      }
   }
}
