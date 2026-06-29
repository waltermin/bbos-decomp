package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLQuoteElement;

final class ESHTMLQuoteElement extends ESHTMLElement {
   ESHTMLQuoteElement(HTMLQuoteElement element) {
      super(element, Names.HTMLQuoteElement);
   }

   final HTMLQuoteElement getQuoteElement() {
      return (HTMLQuoteElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.cite ? JavaScriptEngine.makeStringValue(this.getQuoteElement().getCite()) : super.requestFieldValue(name);
   }
}
