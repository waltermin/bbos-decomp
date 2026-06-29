package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLOptGroupElement;

final class ESHTMLOptGroupElement extends ESHTMLElement {
   ESHTMLOptGroupElement(HTMLOptGroupElement element) {
      super(element, Names.HTMLOptGroupElement);
   }

   final HTMLOptGroupElement getOptGroupElement() {
      return (HTMLOptGroupElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.disabled) {
         return Value.makeBooleanValue(this.getOptGroupElement().getDisabled());
      } else {
         return name == Names.label ? JavaScriptEngine.makeStringValue(this.getOptGroupElement().getLabel()) : super.requestFieldValue(name);
      }
   }
}
