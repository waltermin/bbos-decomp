package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLTableSectionElement;

final class ESHTMLTableSectionElement extends ESHTMLElement {
   ESHTMLTableSectionElement(HTMLTableSectionElement element) {
      super(element, Names.HTMLTableSectionElement, JavaScriptEngine.getInstance()._htmlTableSectionElementPrototype);
   }

   final HTMLTableSectionElement getTableSectionElement() {
      return (HTMLTableSectionElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getTableSectionElement().getAlign());
      } else if (name == Names.ch) {
         return JavaScriptEngine.makeStringValue(this.getTableSectionElement().getCh());
      } else if (name == Names.chOff) {
         return JavaScriptEngine.makeStringValue(this.getTableSectionElement().getChOff());
      } else if (name == Names.vAlign) {
         return JavaScriptEngine.makeStringValue(this.getTableSectionElement().getVAlign());
      } else {
         return name == Names.rows
            ? JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTableSectionElement().getRows())
            : super.requestFieldValue(name);
      }
   }
}
