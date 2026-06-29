package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLLIElement;

final class ESHTMLLIElement extends ESHTMLElement {
   ESHTMLLIElement(HTMLLIElement element) {
      super(element, Names.HTMLLIElement);
   }

   final HTMLLIElement getLIElement() {
      return (HTMLLIElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.type) {
         return JavaScriptEngine.makeStringValue(this.getLIElement().getType());
      } else {
         return name == Names.value ? Value.makeIntegerValue(this.getLIElement().getValue()) : super.requestFieldValue(name);
      }
   }
}
