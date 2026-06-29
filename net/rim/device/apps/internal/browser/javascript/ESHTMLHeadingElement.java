package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLHeadingElement;

final class ESHTMLHeadingElement extends ESHTMLElement {
   ESHTMLHeadingElement(HTMLHeadingElement element) {
      super(element, Names.HTMLHeadingElement);
   }

   final HTMLHeadingElement getHeadingElement() {
      return (HTMLHeadingElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.align ? JavaScriptEngine.makeStringValue(this.getHeadingElement().getAlign()) : super.requestFieldValue(name);
   }
}
