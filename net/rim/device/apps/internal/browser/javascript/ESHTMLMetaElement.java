package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLMetaElement;

final class ESHTMLMetaElement extends ESHTMLElement {
   ESHTMLMetaElement(HTMLMetaElement element) {
      super(element, Names.HTMLMetaElement);
   }

   final HTMLMetaElement getMetaElement() {
      return (HTMLMetaElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.content) {
         return JavaScriptEngine.makeStringValue(this.getMetaElement().getContent());
      } else if (name == Names.httpEquiv) {
         return JavaScriptEngine.makeStringValue(this.getMetaElement().getHttpEquiv());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getMetaElement().getName());
      } else {
         return name == Names.scheme ? JavaScriptEngine.makeStringValue(this.getMetaElement().getScheme()) : super.requestFieldValue(name);
      }
   }
}
