package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLOptionElement;

final class ESHTMLOptionElement extends ESHTMLElement {
   public ESHTMLOptionElement(HTMLOptionElement item) {
      super(item, Names.HTMLOptionElement, JavaScriptEngine.getInstance()._optionPrototype);
   }

   public ESHTMLOptionElement(long text, long value, long defaultSelected, long selected) {
      super(
         (HTMLOptionElement)JavaScriptEngine.getInstance().getCurrentWindow()._esDocument._domDoc.createElement("option"),
         Names.HTMLOptionElement,
         JavaScriptEngine.getInstance()._optionPrototype
      );

      try {
         HTMLOptionElement item = this.getOptionElement();
         if (text != Value.UNDEFINED) {
            item.setAttribute(Names.text, Convert.toString(text));
         }

         if (value != Value.UNDEFINED) {
            item.setValue(Convert.toString(value));
         }

         item.setDefaultSelected(Convert.toBoolean(defaultSelected));
         item.setSelected(Convert.toBoolean(selected));
      } finally {
         return;
      }
   }

   final HTMLOptionElement getOptionElement() {
      return (HTMLOptionElement)this.getNode();
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      HTMLOptionElement item = this.getOptionElement();
      if (item != null) {
         Application.getApplication().invokeAndWait(new ESHTMLOptionElement$1(this, name, item, value));
      }

      return true;
   }

   @Override
   public final long requestFieldValue(String name) {
      HTMLOptionElement item = this.getOptionElement();
      if (item == null) {
         if (name == Names.index) {
            return Value.makeIntegerValue(-1);
         }
      } else {
         if (name == Names.index) {
            return Value.makeIntegerValue(item.getIndex());
         }

         if (name == Names.selected) {
            return Value.makeBooleanValue(item.getSelected());
         }

         if (name == Names.value) {
            return JavaScriptEngine.makeStringValue(item.getValue());
         }

         if (name == Names.defaultSelected) {
            return Value.makeBooleanValue(item.getDefaultSelected());
         }

         if (name == Names.text) {
            return JavaScriptEngine.makeStringValue(item.getText());
         }

         if (name == Names.form) {
            return JavaScriptEngine.getInstance().lookupElementToESObjectLong(item.getForm());
         }

         if (name == Names.disabled) {
            return Value.makeBooleanValue(item.getDisabled());
         }

         if (name == Names.label) {
            return JavaScriptEngine.makeStringValue(item.getLabel());
         }
      }

      return super.requestFieldValue(name);
   }
}
