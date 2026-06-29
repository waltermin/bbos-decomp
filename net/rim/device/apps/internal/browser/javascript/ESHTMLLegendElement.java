package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLLegendElement;

final class ESHTMLLegendElement extends ESHTMLElement {
   ESHTMLLegendElement(HTMLLegendElement element) {
      super(element, Names.HTMLLegendElement);
   }

   final HTMLLegendElement getLegendElement() {
      return (HTMLLegendElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.form) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getLegendElement().getForm());
      } else if (name == Names.accessKey) {
         return JavaScriptEngine.makeStringValue(this.getLegendElement().getAccessKey());
      } else {
         return name == Names.align ? JavaScriptEngine.makeStringValue(this.getLegendElement().getAlign()) : super.requestFieldValue(name);
      }
   }
}
