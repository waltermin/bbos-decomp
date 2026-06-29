package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLBRElement;

final class ESHTMLBRElement extends ESHTMLElement {
   ESHTMLBRElement(HTMLBRElement element) {
      super(element, Names.HTMLBRElement);
   }

   final HTMLBRElement getBRElement() {
      return (HTMLBRElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.clear ? JavaScriptEngine.makeStringValue(this.getBRElement().getClear()) : super.requestFieldValue(name);
   }
}
