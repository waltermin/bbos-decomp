package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.html2.HTMLImageElement;

class ESHTMLImageElement extends ESHTMLElement {
   ESHTMLImageElement() {
      super(null, Names.Image, JavaScriptEngine.getInstance()._htmlImagePrototype);
   }

   public ESHTMLImageElement(HTMLImageElement element) {
      super(element, Names.Image, JavaScriptEngine.getInstance()._htmlImagePrototype);
   }

   HTMLImageElement getImageElement() {
      return (HTMLImageElement)this.getNode();
   }

   @Override
   public long requestFieldValue(String name) {
      HTMLImageElement uiElement = (HTMLImageElement)this.getImageElement();
      if (uiElement != null) {
         if (name == Names.align) {
            return JavaScriptEngine.makeStringValue(uiElement.getAlign());
         }

         if (name == Names.alt) {
            return JavaScriptEngine.makeStringValue(uiElement.getAlt());
         }

         if (name == Names.border) {
            return JavaScriptEngine.makeStringValue(uiElement.getBorder());
         }

         if (name == Names.complete) {
            return Value.makeBooleanValue(true);
         }

         if (name == Names.height) {
            return Value.makeIntegerValue(uiElement.getHeight());
         }

         if (name == Names.hspace) {
            return Value.makeIntegerValue(uiElement.getHspace());
         }

         if (name == Names.isMap) {
            return Value.makeBooleanValue(uiElement.getIsMap());
         }

         if (name == Names.longDesc) {
            return JavaScriptEngine.makeStringValue(uiElement.getLongDesc());
         }

         if (name == Names.lowSrc) {
            return JavaScriptEngine.makeStringValue(uiElement.getAttribute(Names.lowSrc));
         }

         if (name == Names.name) {
            return JavaScriptEngine.makeStringValue(uiElement.getName());
         }

         if (name == Names.src) {
            return JavaScriptEngine.makeStringValue(uiElement.getSrc());
         }

         if (name == Names.useMap) {
            return JavaScriptEngine.makeStringValue(uiElement.getUseMap());
         }

         if (name == Names.vspace) {
            return Value.makeIntegerValue(uiElement.getVspace());
         }

         if (name == Names.width) {
            return Value.makeIntegerValue(uiElement.getWidth());
         }
      }

      return super.requestFieldValue(name);
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      try {
         HTMLImageElement uiElement = (HTMLImageElement)this.getImageElement();
         if (uiElement != null && name == Names.src) {
            uiElement.setSrc(Convert.toString(value));
            return true;
         }
      } finally {
         return true;
      }

      return true;
   }
}
