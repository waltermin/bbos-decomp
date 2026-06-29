package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLTextAreaElement;

final class ESHTMLTextAreaElement extends ESHTMLElement {
   ESHTMLTextAreaElement(HTMLTextAreaElement element) {
      super(element, Names.HTMLTextAreaElement, JavaScriptEngine.getInstance()._htmlTextAreaPrototype);
   }

   final HTMLTextAreaElement getTextArea() {
      return (HTMLTextAreaElement)this.getNode();
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      Application.getApplication().invokeAndWait(new ESHTMLTextAreaElement$1(this, name, value));
      return true;
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.defaultValue) {
         return JavaScriptEngine.makeStringValue(this.getTextArea().getDefaultValue());
      } else if (name == Names.form) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTextArea().getForm());
      } else if (name == Names.accessKey) {
         return JavaScriptEngine.makeStringValue(this.getTextArea().getAccessKey());
      } else if (name == Names.cols) {
         return Value.makeIntegerValue(this.getTextArea().getCols());
      } else if (name == Names.disabled) {
         return Value.makeBooleanValue(this.getTextArea().getDisabled());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getTextArea().getName());
      } else if (name == Names.readOnly) {
         return Value.makeBooleanValue(this.getTextArea().getReadOnly());
      } else if (name == Names.rows) {
         return Value.makeIntegerValue(this.getTextArea().getRows());
      } else if (name == Names.tabIndex) {
         return Value.makeIntegerValue(this.getTextArea().getTabIndex());
      } else if (name == Names.type) {
         return JavaScriptEngine.makeStringValue(this.getTextArea().getType());
      } else {
         return name == Names.value ? JavaScriptEngine.makeStringValue(this.getTextArea().getValue()) : super.requestFieldValue(name);
      }
   }
}
