package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLUListElement;

final class ESHTMLUListElement extends ESHTMLElement {
   ESHTMLUListElement(HTMLUListElement element) {
      super(element, Names.HTMLUListElement);
   }

   final HTMLUListElement getUListElement() {
      return (HTMLUListElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.compact) {
         return Value.makeBooleanValue(this.getUListElement().getCompact());
      } else {
         return name == Names.type ? JavaScriptEngine.makeStringValue(this.getUListElement().getType()) : super.requestFieldValue(name);
      }
   }
}
