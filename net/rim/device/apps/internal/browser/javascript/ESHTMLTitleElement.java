package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLTitleElement;

final class ESHTMLTitleElement extends ESHTMLElement {
   ESHTMLTitleElement(HTMLTitleElement element) {
      super(element, Names.HTMLTitleElement);
   }

   final HTMLTitleElement getTitleElement() {
      return (HTMLTitleElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.title ? JavaScriptEngine.makeStringValue(this.getTitleElement().getTitle()) : super.requestFieldValue(name);
   }
}
