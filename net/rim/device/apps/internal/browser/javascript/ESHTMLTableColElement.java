package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLTableColElement;

final class ESHTMLTableColElement extends ESHTMLElement {
   ESHTMLTableColElement(HTMLTableColElement element) {
      super(element, Names.HTMLTableColElement);
   }

   final HTMLTableColElement getTableColElement() {
      return (HTMLTableColElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getTableColElement().getAlign());
      } else if (name == Names.ch) {
         return JavaScriptEngine.makeStringValue(this.getTableColElement().getCh());
      } else if (name == Names.chOff) {
         return JavaScriptEngine.makeStringValue(this.getTableColElement().getChOff());
      } else if (name == Names.span) {
         return Value.makeIntegerValue(this.getTableColElement().getSpan());
      } else if (name == Names.vAlign) {
         return JavaScriptEngine.makeStringValue(this.getTableColElement().getVAlign());
      } else {
         return name == Names.width ? JavaScriptEngine.makeStringValue(this.getTableColElement().getWidth()) : super.requestFieldValue(name);
      }
   }
}
