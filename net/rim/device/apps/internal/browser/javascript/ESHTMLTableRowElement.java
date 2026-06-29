package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLTableRowElement;

final class ESHTMLTableRowElement extends ESHTMLElement {
   ESHTMLTableRowElement(HTMLTableRowElement element) {
      super(element, Names.HTMLTableRowElement, JavaScriptEngine.getInstance()._htmlTableRowElementPrototype);
   }

   final HTMLTableRowElement getTableRowElement() {
      return (HTMLTableRowElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.rowIndex) {
         return Value.makeIntegerValue(this.getTableRowElement().getRowIndex());
      } else if (name == Names.sectionRowIndex) {
         return Value.makeIntegerValue(this.getTableRowElement().getSectionRowIndex());
      } else if (name == Names.cells) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTableRowElement().getCells());
      } else if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getTableRowElement().getAlign());
      } else if (name == Names.bgColor) {
         return JavaScriptEngine.makeStringValue(this.getTableRowElement().getBgColor());
      } else if (name == Names.ch) {
         return JavaScriptEngine.makeStringValue(this.getTableRowElement().getCh());
      } else if (name == Names.chOff) {
         return JavaScriptEngine.makeStringValue(this.getTableRowElement().getChOff());
      } else {
         return name == Names.vAlign ? JavaScriptEngine.makeStringValue(this.getTableRowElement().getVAlign()) : super.requestFieldValue(name);
      }
   }
}
