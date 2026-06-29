package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLAppletElement;

final class ESHTMLAppletElement extends ESHTMLElement {
   ESHTMLAppletElement(HTMLAppletElement element) {
      super(element, Names.HTMLAppletElement);
   }

   final HTMLAppletElement getAppletElement() {
      return (HTMLAppletElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getAlign());
      } else if (name == Names.alt) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getAlt());
      } else if (name == Names.archive) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getArchive());
      } else if (name == Names.code) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getCode());
      } else if (name == Names.codebase) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getCodeBase());
      } else if (name == Names.height) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getHeight());
      } else if (name == Names.hspace) {
         return Value.makeIntegerValue(this.getAppletElement().getHspace());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getName());
      } else if (name == Names.object) {
         return JavaScriptEngine.makeStringValue(this.getAppletElement().getObject());
      } else if (name == Names.vspace) {
         return Value.makeIntegerValue(this.getAppletElement().getVspace());
      } else {
         return name == Names.width ? JavaScriptEngine.makeStringValue(this.getAppletElement().getWidth()) : super.requestFieldValue(name);
      }
   }
}
