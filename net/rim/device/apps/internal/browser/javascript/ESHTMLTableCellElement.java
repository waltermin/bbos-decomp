package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLTableCellElement;

final class ESHTMLTableCellElement extends ESHTMLElement {
   ESHTMLTableCellElement(HTMLTableCellElement element) {
      super(element, Names.HTMLTableCellElement);
   }

   final HTMLTableCellElement getTableCellElement() {
      return (HTMLTableCellElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.cellIndex) {
         return Value.makeIntegerValue(this.getTableCellElement().getCellIndex());
      } else if (name == Names.abbr) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getAbbr());
      } else if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getAlign());
      } else if (name == Names.axis) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getAxis());
      } else if (name == Names.bgColor) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getBgColor());
      } else if (name == Names.ch) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getCh());
      } else if (name == Names.chOff) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getChOff());
      } else if (name == Names.colSpan) {
         return Value.makeIntegerValue(this.getTableCellElement().getColSpan());
      } else if (name == Names.headers) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getHeaders());
      } else if (name == Names.height) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getHeight());
      } else if (name == Names.noWrap) {
         return Value.makeBooleanValue(this.getTableCellElement().getNoWrap());
      } else if (name == Names.rowSpan) {
         return Value.makeIntegerValue(this.getTableCellElement().getRowSpan());
      } else if (name == Names.scope) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getScope());
      } else if (name == Names.vAlign) {
         return JavaScriptEngine.makeStringValue(this.getTableCellElement().getVAlign());
      } else {
         return name == Names.width ? JavaScriptEngine.makeStringValue(this.getTableCellElement().getWidth()) : super.requestFieldValue(name);
      }
   }
}
