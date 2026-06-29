package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLHtmlElement;

final class ESHTMLHtmlElement extends ESHTMLElement {
   ESHTMLHtmlElement(HTMLHtmlElement element) {
      super(element, Names.HTMLHtmlElement);
   }

   final HTMLHtmlElement getHtmlElement() {
      return (HTMLHtmlElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.version ? JavaScriptEngine.makeStringValue(this.getHtmlElement().getVersion()) : super.requestFieldValue(name);
   }
}
