package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLFrameElement;

final class ESHTMLFrameElement extends ESHTMLElement {
   ESHTMLFrameElement(HTMLFrameElement element) {
      super(element, Names.HTMLFrameElement);
   }

   final HTMLFrameElement getFrameElement() {
      return (HTMLFrameElement)this.getNode();
   }

   @Override
   public final long requestFieldValue(String name) {
      if (name == Names.frameBorder) {
         return JavaScriptEngine.makeStringValue(this.getFrameElement().getFrameBorder());
      } else if (name == Names.longDesc) {
         return JavaScriptEngine.makeStringValue(this.getFrameElement().getLongDesc());
      } else if (name == Names.marginHeight) {
         return JavaScriptEngine.makeStringValue(this.getFrameElement().getMarginHeight());
      } else if (name == Names.marginWidth) {
         return JavaScriptEngine.makeStringValue(this.getFrameElement().getMarginWidth());
      } else if (name == Names.name) {
         return JavaScriptEngine.makeStringValue(this.getFrameElement().getName());
      } else if (name == Names.noResize) {
         return Value.makeBooleanValue(this.getFrameElement().getNoResize());
      } else if (name == Names.scrolling) {
         return JavaScriptEngine.makeStringValue(this.getFrameElement().getScrolling());
      } else if (name == Names.src) {
         return JavaScriptEngine.makeStringValue(this.getFrameElement().getSrc());
      } else {
         return name == Names.contentDocument
            ? JavaScriptEngine.getInstance().lookupElementToESObjectLong(this.getFrameElement().getContentDocument())
            : super.requestFieldValue(name);
      }
   }
}
