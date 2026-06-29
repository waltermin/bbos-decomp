package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLAreaElement;

final class ESHTMLAreaElement extends ESHTMLElement {
   ESHTMLAreaElement(HTMLAreaElement element) {
      super(element, Names.HTMLAreaElement);
   }

   final HTMLAreaElement getAreaElement() {
      return (HTMLAreaElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.accessKey) {
         return JavaScriptEngine.makeStringValue(this.getAreaElement().getAccessKey());
      } else if (name == Names.alt) {
         return JavaScriptEngine.makeStringValue(this.getAreaElement().getAlt());
      } else if (name == Names.coords) {
         return JavaScriptEngine.makeStringValue(this.getAreaElement().getCoords());
      } else if (name == Names.href) {
         return JavaScriptEngine.makeStringValue(this.getAreaElement().getHref());
      } else if (name == Names.nohref) {
         return Value.makeBooleanValue(this.getAreaElement().getNoHref());
      } else if (name == Names.shape) {
         return JavaScriptEngine.makeStringValue(this.getAreaElement().getShape());
      } else if (name == Names.tabIndex) {
         return Value.makeIntegerValue(this.getAreaElement().getTabIndex());
      } else {
         return name == Names.target ? JavaScriptEngine.makeStringValue(this.getAreaElement().getTarget()) : super.requestFieldValue(name);
      }
   }
}
