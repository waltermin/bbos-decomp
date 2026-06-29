package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLParamElement;

final class ESHTMLParamElement extends ESHTMLElement {
   ESHTMLParamElement(HTMLParamElement element) {
      super(element, Names.HTMLParamElement);
   }

   final HTMLParamElement getParamElement() {
      return (HTMLParamElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getParamElement().getName());
      } else if (name == Names.type) {
         return JavaScriptEngine.makeStringValue(this.getParamElement().getType());
      } else if (name == Names.value) {
         return JavaScriptEngine.makeStringValue(this.getParamElement().getValue());
      } else {
         return name == Names.valueType ? JavaScriptEngine.makeStringValue(this.getParamElement().getValueType()) : super.requestFieldValue(name);
      }
   }
}
