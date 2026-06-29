package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLHeadElement;

final class ESHTMLHeadElement extends ESHTMLElement {
   ESHTMLHeadElement(HTMLHeadElement element) {
      super(element, Names.HTMLHeadElement);
   }

   final HTMLHeadElement getHeadElement() {
      return (HTMLHeadElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.profile ? JavaScriptEngine.makeStringValue(this.getHeadElement().getProfile()) : super.requestFieldValue(name);
   }
}
