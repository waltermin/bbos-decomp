package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.api.system.Application;
import net.rim.ecmascript.runtime.ESFunction;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.html2.HTMLCollection;
import org.w3c.dom.html2.HTMLFormElement;
import org.w3c.dom.html2.HTMLOptionElement;
import org.w3c.dom.html2.HTMLOptionsCollection;
import org.w3c.dom.html2.HTMLSelectElement;

final class ESHTMLSelectElement extends ESHTMLElement {
   private ESHTMLSelectElement$OptionArray _options;
   private NodeList _elements;

   public ESHTMLSelectElement(HTMLSelectElement select) {
      super(select, Names.Select, JavaScriptEngine.getInstance()._selectPrototype);
      this.addField(Names.name, 28, JavaScriptEngine.makeStringValue(select.getName()));
      this.addField(Names.type, 29, JavaScriptEngine.makeStringValue(select.getType()));
      HTMLFormElement form = select.getForm();
      this.addField(Names.form, 29, JavaScriptEngine.getInstance().lookupElementToESObjectLong(form));
      this._options = new ESHTMLSelectElement$OptionArray(this, (HTMLCollection)select.getOptions());
      this.addField(Names.options, 28, Value.makeObjectValue(this._options));
   }

   final HTMLSelectElement getSelectElement() {
      return (HTMLSelectElement)this.getNode();
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      if (name == Names.selectedIndex) {
         Application.getApplication().invokeAndWait(new ESHTMLSelectElement$1(this, value));
         return true;
      } else {
         this._options.notifyFieldChanged(name, value);
         return true;
      }
   }

   @Override
   public final long requestElementValue(long element) {
      if (this._elements != null) {
         if (Value.getType(element) == 0) {
            int index = Value.getIntegerValue(element);
            if (index == 0) {
               return Value.makeObjectValue(this);
            }

            Node node = this._elements.item(index);
            if (node != null) {
               return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node);
            }
         }
      } else {
         try {
            return this._options.getElement(element);
         } finally {
            return Value.DEFAULT;
         }
      }

      return Value.DEFAULT;
   }

   @Override
   public final long requestFieldValue(String name) {
      HTMLSelectElement select = this.getSelectElement();
      if (name == Names.selectedIndex) {
         return Value.makeIntegerValue(select.getSelectedIndex());
      }

      if (name == Names.value) {
         HTMLOptionsCollection collection = select.getOptions();
         HTMLOptionElement option = (HTMLOptionElement)collection.item(select.getSelectedIndex());
         String text = null;
         if (option != null) {
            text = option.getValue();
         }

         return JavaScriptEngine.makeStringValue(text);
      } else {
         if (name == Names.length) {
            return this._options.requestFieldValue(name);
         }

         if (name == Names.disabled) {
            return Value.makeBooleanValue(select.getDisabled());
         }

         if (name == Names.multiple) {
            return Value.makeBooleanValue(select.getMultiple());
         }

         if (name == Names.size) {
            return Value.makeIntegerValue(select.getSize());
         }

         if (name == Names.tabIndex) {
            return Value.makeIntegerValue(select.getTabIndex());
         }

         if (name.length() > 2 && name.charAt(0) == 'o' && name.charAt(1) == 'n') {
            String action = select.getAttribute(name);
            if (action != null) {
               try {
                  ESFunction function = JavaScriptEngine.getInstance().getHostFunction(action);
                  if (function != null) {
                     this.addField(name, 1, Value.makeObjectValue(function));
                     return Value.makeObjectValue(function);
                  }
               } finally {
                  return super.requestFieldValue(name);
               }
            }
         }

         return super.requestFieldValue(name);
      }
   }

   final void setElementArray(NodeList nodeList) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }
}
