package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLMapElement;

final class ESHTMLMapElement extends ESHTMLElement {
   ESHTMLMapElement(HTMLMapElement element) {
      super(element, Names.HTMLMapElement);
   }

   final HTMLMapElement getMapElement() {
      return (HTMLMapElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.areas) {
         return JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getMapElement().getAreas());
      } else {
         return name == Names.name ? JavaScriptEngine.makeStringValue(this.getMapElement().getName()) : super.requestFieldValue(name);
      }
   }
}
