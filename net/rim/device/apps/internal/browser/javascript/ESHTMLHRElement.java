package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLHRElement;

final class ESHTMLHRElement extends ESHTMLElement {
   ESHTMLHRElement(HTMLHRElement element) {
      super(element, Names.HTMLHRElement);
   }

   final HTMLHRElement getHRElement() {
      return (HTMLHRElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getHRElement().getAlign());
      } else if (name == Names.noShade) {
         return Value.makeBooleanValue(this.getHRElement().getNoShade());
      } else if (name == Names.size) {
         return JavaScriptEngine.makeStringValue(this.getHRElement().getSize());
      } else {
         return name == Names.width ? JavaScriptEngine.makeStringValue(this.getHRElement().getWidth()) : super.requestFieldValue(name);
      }
   }
}
