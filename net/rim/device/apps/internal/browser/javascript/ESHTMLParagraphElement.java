package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLParagraphElement;

final class ESHTMLParagraphElement extends ESHTMLElement {
   ESHTMLParagraphElement(HTMLParagraphElement element) {
      super(element, Names.HTMLParagraphElement);
   }

   final HTMLParagraphElement getParagraphElement() {
      return (HTMLParagraphElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.align ? JavaScriptEngine.makeStringValue(this.getParagraphElement().getAlign()) : super.requestFieldValue(name);
   }
}
