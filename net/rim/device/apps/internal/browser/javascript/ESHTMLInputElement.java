package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLInputElement;

final class ESHTMLInputElement extends ESHTMLElement {
   private NodeList _elements;

   public ESHTMLInputElement(HTMLInputElement input) {
      super(input, Names.Input, JavaScriptEngine.getInstance()._inputPrototype);
      this.addField(Names.name, 28, JavaScriptEngine.makeStringValue(input.getName()));
      this.addField(Names.type, 29, JavaScriptEngine.makeStringValue(input.getType()));
      HTMLFormElement form = input.getForm();
      this.addField(Names.form, 29, JavaScriptEngine.getInstance().lookupElementToESObjectLong(form));
   }

   final HTMLInputElement getInputElement() {
      return (HTMLInputElement)this.getNode();
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      Application.getApplication().invokeAndWait(new ESHTMLInputElement$1(this, name, value));
      return true;
   }

   @Override
   public final long requestElementValue(long element) {
      if (Value.getType(element) == 0) {
         int index = Value.getIntegerValue(element);
         if (index == 0) {
            return Value.makeObjectValue(this);
         }

         if (this._elements != null) {
            Node node = this._elements.item(index);
            if (node != null) {
               return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node);
            }
         }
      }

      return Value.DEFAULT;
   }

   @Override
   public final long requestFieldValue(String name) {
      HTMLInputElement item = this.getInputElement();
      if (name == Names.defaultValue) {
         return JavaScriptEngine.makeStringValue(item.getDefaultValue());
      } else if (name == Names.defaultChecked) {
         return Value.makeBooleanValue(item.getDefaultChecked());
      } else if (name == Names.accept) {
         return JavaScriptEngine.makeStringValue(item.getAccept());
      } else if (name == Names.accessKey) {
         return JavaScriptEngine.makeStringValue(item.getAccessKey());
      } else if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(item.getAlign());
      } else if (name == Names.alt) {
         return JavaScriptEngine.makeStringValue(item.getAlt());
      } else if (name == Names.checked) {
         return Value.makeBooleanValue(item.getChecked());
      } else if (name == Names.disabled) {
         return Value.makeBooleanValue(item.getDisabled());
      } else if (name == Names.maxLength) {
         return Value.makeIntegerValue(item.getMaxLength());
      } else if (name == Names.readOnly) {
         return Value.makeBooleanValue(item.getReadOnly());
      } else if (name == Names.size) {
         return Value.makeIntegerValue(item.getSize());
      } else if (name == Names.src) {
         return JavaScriptEngine.makeStringValue(item.getSrc());
      } else if (name == Names.tabIndex) {
         return Value.makeIntegerValue(item.getTabIndex());
      } else if (name == Names.useMap) {
         return JavaScriptEngine.makeStringValue(item.getUseMap());
      } else if (name == Names.value) {
         return JavaScriptEngine.makeStringValue(item.getValue());
      } else if (name == Names.length) {
         return this._elements == null ? Value.makeIntegerValue(1) : Value.makeLongValue(this._elements.getLength());
      } else {
         return super.requestFieldValue(name);
      }
   }

   final void setElementArray(NodeList nodeList) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
