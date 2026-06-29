package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLModElement;

final class ESHTMLModElement extends ESHTMLElement {
   ESHTMLModElement(HTMLModElement element) {
      super(element, Names.HTMLModElement);
   }

   final HTMLModElement getModElement() {
      return (HTMLModElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.cite) {
         return JavaScriptEngine.makeStringValue(this.getModElement().getCite());
      } else {
         return name == Names.dateTime ? JavaScriptEngine.makeStringValue(this.getModElement().getDateTime()) : super.requestFieldValue(name);
      }
   }
}
