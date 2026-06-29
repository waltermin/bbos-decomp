package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLBodyElement;

final class ESHTMLBodyElement extends ESHTMLElement {
   ESHTMLBodyElement(HTMLBodyElement element) {
      super(element, Names.HTMLBodyElement);
   }

   final HTMLBodyElement getBodyElement() {
      return (HTMLBodyElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.aLink) {
         return JavaScriptEngine.makeStringValue(this.getBodyElement().getALink());
      } else if (name == Names.background) {
         return JavaScriptEngine.makeStringValue(this.getBodyElement().getBackground());
      } else if (name == Names.bgColor) {
         return JavaScriptEngine.makeStringValue(this.getBodyElement().getBgColor());
      } else if (name == Names.link) {
         return JavaScriptEngine.makeStringValue(this.getBodyElement().getLink());
      } else if (name == Names.text) {
         return JavaScriptEngine.makeStringValue(this.getBodyElement().getText());
      } else {
         return name == Names.vLink ? JavaScriptEngine.makeStringValue(this.getBodyElement().getVLink()) : super.requestFieldValue(name);
      }
   }
}
