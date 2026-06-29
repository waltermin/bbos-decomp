package net.rim.device.apps.internal.browser.javascript;

import org.w3c.dom.html2.HTMLIFrameElement;

final class ESHTMLIFrameElement extends ESHTMLElement {
   ESHTMLIFrameElement(HTMLIFrameElement element) {
      super(element, Names.HTMLIFrameElement);
   }

   final HTMLIFrameElement getIFrameElement() {
      return (HTMLIFrameElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.align) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getAlign());
      } else if (name == Names.frameBorder) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getFrameBorder());
      } else if (name == Names.height) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getHeight());
      } else if (name == Names.longDesc) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getLongDesc());
      } else if (name == Names.marginHeight) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getMarginHeight());
      } else if (name == Names.marginWidth) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getMarginWidth());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getName());
      } else if (name == Names.scrolling) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getScrolling());
      } else if (name == Names.src) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getSrc());
      } else if (name == Names.width) {
         return JavaScriptEngine.makeStringValue(this.getIFrameElement().getWidth());
      } else {
         return name == Names.contentDocument
            ? JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getIFrameElement().getContentDocument())
            : super.requestFieldValue(name);
      }
   }
}
