package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLFrameSetElement;

final class ESHTMLFrameSetElement extends ESHTMLElement {
   ESHTMLFrameSetElement(HTMLFrameSetElement element) {
      super(element, Names.HTMLFrameSetElement);
   }

   final HTMLFrameSetElement getFrameSetElement() {
      return (HTMLFrameSetElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.cols) {
         return JavaScriptEngine.makeStringValue(this.getFrameSetElement().getCols());
      } else {
         return name == Names.rows ? JavaScriptEngine.makeStringValue(this.getFrameSetElement().getRows()) : super.requestFieldValue(name);
      }
   }
}
