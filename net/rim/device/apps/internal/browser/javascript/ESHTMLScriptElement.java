package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLScriptElement;

final class ESHTMLScriptElement extends ESHTMLElement {
   ESHTMLScriptElement(HTMLScriptElement element) {
      super(element, Names.HTMLScriptElement);
   }

   final HTMLScriptElement getScriptElement() {
      return (HTMLScriptElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.text) {
         return JavaScriptEngine.makeStringValue(this.getScriptElement().getText());
      } else if (name == Names.htmlFor) {
         return JavaScriptEngine.makeStringValue(this.getScriptElement().getHtmlFor());
      } else if (name == Names.event) {
         return JavaScriptEngine.makeStringValue(this.getScriptElement().getEvent());
      } else if (name == Names.charset) {
         return JavaScriptEngine.makeStringValue(this.getScriptElement().getCharset());
      } else if (name == Names.defer) {
         return Value.makeBooleanValue(this.getScriptElement().getDefer());
      } else if (name == Names.src) {
         return JavaScriptEngine.makeStringValue(this.getScriptElement().getSrc());
      } else {
         return name == Names.type ? JavaScriptEngine.makeStringValue(this.getScriptElement().getType()) : super.requestFieldValue(name);
      }
   }
}
