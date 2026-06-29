package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLButtonElement;

final class ESHTMLButtonElement extends ESHTMLElement {
   ESHTMLButtonElement(HTMLButtonElement element) {
      super(element, Names.HTMLButtonElement);
   }

   final HTMLButtonElement getButton() {
      return (HTMLButtonElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.form) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getButton().getForm());
      } else if (name == Names.accessKey) {
         return JavaScriptEngine.makeStringValue(this.getButton().getAccessKey());
      } else if (name == Names.disabled) {
         return Value.makeBooleanValue(this.getButton().getDisabled());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getButton().getName());
      } else if (name == Names.tabIndex) {
         return Value.makeIntegerValue(this.getButton().getTabIndex());
      } else if (name == Names.type) {
         return JavaScriptEngine.makeStringValue(this.getButton().getType());
      } else {
         return name == Names.value ? JavaScriptEngine.makeStringValue(this.getButton().getValue()) : super.requestFieldValue(name);
      }
   }
}
