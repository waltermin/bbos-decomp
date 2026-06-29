package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLObjectElement;

final class ESHTMLObjectElement extends ESHTMLElement {
   ESHTMLObjectElement(HTMLObjectElement element) {
      super(element, Names.HTMLObjectElement);
   }

   final HTMLObjectElement getObjectElement() {
      return (HTMLObjectElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.form) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getObjectElement().getForm());
      } else if (name == Names.code) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getCode());
      } else if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getAlign());
      } else if (name == Names.archive) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getArchive());
      } else if (name == Names.border) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getBorder());
      } else if (name == Names.codebase) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getCodeBase());
      } else if (name == Names.codetype) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getCodeType());
      } else if (name == Names.data) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getData());
      } else if (name == Names.declare) {
         return Value.makeBooleanValue(this.getObjectElement().getDeclare());
      } else if (name == Names.height) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getHeight());
      } else if (name == Names.hspace) {
         return Value.makeIntegerValue(this.getObjectElement().getHspace());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getName());
      } else if (name == Names.standby) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getStandby());
      } else if (name == Names.tabIndex) {
         return Value.makeIntegerValue(this.getObjectElement().getTabIndex());
      } else if (name == Names.type) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getType());
      } else if (name == Names.useMap) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getUseMap());
      } else if (name == Names.vspace) {
         return Value.makeIntegerValue(this.getObjectElement().getVspace());
      } else if (name == Names.width) {
         return JavaScriptEngine.makeStringValue(this.getObjectElement().getWidth());
      } else {
         return name == Names.contentDocument
            ? JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getObjectElement().getContentDocument())
            : super.requestFieldValue(name);
      }
   }
}
