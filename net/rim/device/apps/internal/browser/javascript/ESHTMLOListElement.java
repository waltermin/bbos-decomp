package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLOListElement;

final class ESHTMLOListElement extends ESHTMLElement {
   ESHTMLOListElement(HTMLOListElement element) {
      super(element, Names.HTMLOListElement);
   }

   final HTMLOListElement getOListElement() {
      return (HTMLOListElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.compact) {
         return Value.makeBooleanValue(this.getOListElement().getCompact());
      } else if (name == Names.start) {
         return Value.makeIntegerValue(this.getOListElement().getStart());
      } else {
         return name == Names.type ? JavaScriptEngine.makeStringValue(this.getOListElement().getType()) : super.requestFieldValue(name);
      }
   }
}
