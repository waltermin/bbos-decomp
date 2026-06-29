package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLFontElement;

final class ESHTMLFontElement extends ESHTMLElement {
   ESHTMLFontElement(HTMLFontElement element) {
      super(element, Names.HTMLFontElement);
   }

   final HTMLFontElement getFontElement() {
      return (HTMLFontElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.color) {
         return JavaScriptEngine.makeStringValue(this.getFontElement().getColor());
      } else if (name == Names.face) {
         return JavaScriptEngine.makeStringValue(this.getFontElement().getFace());
      } else {
         return name == Names.size ? JavaScriptEngine.makeStringValue(this.getFontElement().getSize()) : super.requestFieldValue(name);
      }
   }
}
