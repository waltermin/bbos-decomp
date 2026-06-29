package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLTableCaptionElement;

final class ESHTMLTableCaptionElement extends ESHTMLElement {
   ESHTMLTableCaptionElement(HTMLTableCaptionElement element) {
      super(element, Names.HTMLTableCaptionElement);
   }

   final HTMLTableCaptionElement getTableCaptionElement() {
      return (HTMLTableCaptionElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.align ? JavaScriptEngine.makeStringValue(this.getTableCaptionElement().getAlign()) : super.requestFieldValue(name);
   }
}
