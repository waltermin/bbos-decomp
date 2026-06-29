package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLBaseElement;

final class ESHTMLBaseElement extends ESHTMLElement {
   ESHTMLBaseElement(HTMLBaseElement element) {
      super(element, Names.HTMLBaseElement);
   }

   final HTMLBaseElement getBaseElement() {
      return (HTMLBaseElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.href) {
         return JavaScriptEngine.makeStringValue(this.getBaseElement().getHref());
      } else {
         return name == Names.target ? JavaScriptEngine.makeStringValue(this.getBaseElement().getTarget()) : super.requestFieldValue(name);
      }
   }
}
