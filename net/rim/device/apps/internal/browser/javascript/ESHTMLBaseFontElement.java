package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLBaseFontElement;

final class ESHTMLBaseFontElement extends ESHTMLElement {
   ESHTMLBaseFontElement(HTMLBaseFontElement element) {
      super(element, Names.HTMLBaseFontElement);
   }

   final HTMLBaseFontElement getBaseFontElement() {
      return (HTMLBaseFontElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.color) {
         return JavaScriptEngine.makeStringValue(this.getBaseFontElement().getColor());
      } else if (name == Names.face) {
         return JavaScriptEngine.makeStringValue(this.getBaseFontElement().getFace());
      } else {
         return name == Names.size ? Value.makeIntegerValue(this.getBaseFontElement().getSize()) : super.requestFieldValue(name);
      }
   }
}
