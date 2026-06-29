package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLLinkElement;

final class ESHTMLLinkElement extends ESHTMLElement {
   ESHTMLLinkElement(HTMLLinkElement element) {
      super(element, Names.HTMLLinkElement);
   }

   final HTMLLinkElement getLinkElement() {
      return (HTMLLinkElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.disabled) {
         return Value.makeBooleanValue(this.getLinkElement().getDisabled());
      } else if (name == Names.charset) {
         return JavaScriptEngine.makeStringValue(this.getLinkElement().getCharset());
      } else if (name == Names.href) {
         return JavaScriptEngine.makeStringValue(this.getLinkElement().getHref());
      } else if (name == Names.hreflang) {
         return JavaScriptEngine.makeStringValue(this.getLinkElement().getHreflang());
      } else if (name == Names.media) {
         return JavaScriptEngine.makeStringValue(this.getLinkElement().getMedia());
      } else if (name == Names.rel) {
         return JavaScriptEngine.makeStringValue(this.getLinkElement().getRel());
      } else if (name == Names.rev) {
         return JavaScriptEngine.makeStringValue(this.getLinkElement().getRev());
      } else if (name == Names.target) {
         return JavaScriptEngine.makeStringValue(this.getLinkElement().getTarget());
      } else {
         return name == Names.type ? JavaScriptEngine.makeStringValue(this.getLinkElement().getType()) : super.requestFieldValue(name);
      }
   }
}
