package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLPreElement;

final class ESHTMLPreElement extends ESHTMLElement {
   ESHTMLPreElement(HTMLPreElement element) {
      super(element, Names.HTMLPreElement);
   }

   final HTMLPreElement getPreElement() {
      return (HTMLPreElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.width ? Value.makeIntegerValue(this.getPreElement().getWidth()) : super.requestFieldValue(name);
   }
}
