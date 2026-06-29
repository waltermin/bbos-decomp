package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLAnchorElement;

final class ESHTMLAnchorElement extends ESHTMLElement {
   ESHTMLAnchorElement(HTMLAnchorElement element) {
      super(element, Names.HTMLAnchorElement, JavaScriptEngine.getInstance()._htmlAnchorElementPrototype);
   }

   final HTMLAnchorElement getAnchorElement() {
      return (HTMLAnchorElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.accessKey) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getAccessKey());
      } else if (name == Names.charset) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getCharset());
      } else if (name == Names.coords) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getCoords());
      } else if (name == Names.href) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getHref());
      } else if (name == Names.hreflang) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getHreflang());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getName());
      } else if (name == Names.rel) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getRel());
      } else if (name == Names.rev) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getRev());
      } else if (name == Names.shape) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getShape());
      } else if (name == Names.tabIndex) {
         return Value.makeIntegerValue(this.getAnchorElement().getTabIndex());
      } else if (name == Names.target) {
         return JavaScriptEngine.makeStringValue(this.getAnchorElement().getTarget());
      } else {
         return name == Names.type ? JavaScriptEngine.makeStringValue(this.getAnchorElement().getType()) : super.requestFieldValue(name);
      }
   }
}
