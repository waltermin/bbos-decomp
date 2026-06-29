package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLTableElement;

final class ESHTMLTableElement extends ESHTMLElement {
   ESHTMLTableElement(HTMLTableElement element) {
      super(element, Names.HTMLTableElement, JavaScriptEngine.getInstance()._htmlTableElementPrototype);
   }

   final HTMLTableElement getTableElement() {
      return (HTMLTableElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.caption) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTableElement().getCaption());
      } else if (name == Names.tHead) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTableElement().getTHead());
      } else if (name == Names.tFoot) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTableElement().getTFoot());
      } else if (name == Names.rows) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTableElement().getRows());
      } else if (name == Names.tBodies) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getTableElement().getTBodies());
      } else if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getAlign());
      } else if (name == Names.bgColor) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getBgColor());
      } else if (name == Names.border) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getBorder());
      } else if (name == Names.cellPadding) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getCellPadding());
      } else if (name == Names.cellSpacing) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getCellSpacing());
      } else if (name == Names.frame) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getFrame());
      } else if (name == Names.rules) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getRules());
      } else if (name == Names.summary) {
         return JavaScriptEngine.makeStringValue(this.getTableElement().getSummary());
      } else {
         return name == Names.width ? JavaScriptEngine.makeStringValue(this.getTableElement().getWidth()) : super.requestFieldValue(name);
      }
   }
}
