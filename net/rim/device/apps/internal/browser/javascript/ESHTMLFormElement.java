package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLFormElement;

final class ESHTMLFormElement extends ESHTMLElement {
   private ESCollection _elements;

   public ESHTMLFormElement(HTMLFormElement form) {
      super(form, Names.Form, JavaScriptEngine.getInstance()._formPrototype);
      this._elements = new ESCollection(form.getElements());
      this._elements.addHostFunction(new ESHTMLFormElement$ElementsToStringHostFunction());
      this.addField(Names.elements, 29, Value.makeObjectValue(this._elements));
   }

   final HTMLFormElement getFormElement() {
      return (HTMLFormElement)this.getNode();
   }

   @Override
   public final boolean notifyFieldChanged(String name, long value) {
      try {
         if (name == Names.method) {
            this.getFormElement().setMethod(Convert.toString(value));
         } else if (name == Names.target) {
            this.getFormElement().setTarget(Convert.toString(value));
         } else if (name == Names.action) {
            this.getFormElement().setAction(Convert.toString(value));
         } else if (name == Names.onSubmit) {
            this.getFormElement().setAttribute(name, Convert.toString(value));
         } else if (name == Names.encoding || name == Names.enctype) {
            this.getFormElement().setEnctype(Convert.toString(value));
         } else if (name == Names.name) {
            this.getFormElement().setName(Convert.toString(value));
         } else if (name == Names.acceptCharset) {
            this.getFormElement().setAcceptCharset(Convert.toString(value));
            return true;
         }
      } finally {
         return true;
      }

      return true;
   }

   @Override
   public final long requestElementValue(long element) {
      try {
         return this._elements.getElement(element);
      } finally {
         ;
      }
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.method) {
         return JavaScriptEngine.makeStringValue(this.getFormElement().getMethod());
      } else if (name == Names.target) {
         return JavaScriptEngine.makeStringValue(this.getFormElement().getTarget());
      } else if (name == Names.action) {
         return JavaScriptEngine.makeStringValue(this.getFormElement().getAction());
      } else if (name == Names.onSubmit) {
         return JavaScriptEngine.makeStringValue(this.getFormElement().getAttribute(name));
      } else if (name == Names.encoding || name == Names.enctype) {
         return JavaScriptEngine.makeStringValue(this.getFormElement().getEnctype());
      } else if (name == Names.length) {
         return Value.makeIntegerValue(this.getFormElement().getLength());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getFormElement().getName());
      } else if (name == Names.acceptCharset) {
         return JavaScriptEngine.makeStringValue(this.getFormElement().getAcceptCharset());
      } else {
         long result = this.noRedirectGetField(name);
         if (result != Value.UNDEFINED) {
            return result;
         } else {
            result = this._elements.requestFieldValue(name);
            if (result != Value.DEFAULT && result != Value.UNDEFINED) {
               this.addField(name, 29, result);
               return result;
            } else {
               return super.requestFieldValue(name);
            }
         }
      }
   }
}
