package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLStyleElement;

final class ESHTMLStyleElement extends ESHTMLElement {
   ESHTMLStyleElement(HTMLStyleElement element) {
      super(element, Names.HTMLStyleElement);
   }

   final HTMLStyleElement getStyleElement() {
      return (HTMLStyleElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.disabled) {
         return Value.makeBooleanValue(this.getStyleElement().getDisabled());
      } else if (name == Names.media) {
         return JavaScriptEngine.makeStringValue(this.getStyleElement().getMedia());
      } else {
         return name == Names.type ? JavaScriptEngine.makeStringValue(this.getStyleElement().getType()) : super.requestFieldValue(name);
      }
   }
}
