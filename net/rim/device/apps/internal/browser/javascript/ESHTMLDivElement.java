package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLDivElement;

final class ESHTMLDivElement extends ESHTMLElement {
   ESHTMLDivElement(HTMLDivElement element) {
      super(element, Names.HTMLDivElement);
   }

   final HTMLDivElement getDivElement() {
      return (HTMLDivElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.align ? JavaScriptEngine.makeStringValue(this.getDivElement().getAlign()) : super.requestFieldValue(name);
   }
}
