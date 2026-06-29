package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ESObject;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Element;
import org.w3c.dom.html2.HTMLElement;

class ESHTMLElement extends ESElement {
   ESHTMLElement(HTMLElement element) {
      this(element, Names.HTMLElement);
   }

   ESHTMLElement(HTMLElement element, String name) {
      super(element, name);
      this.addField(Names.style, 28, Value.makeObjectValue(new ESHTMLElement$StyleObject(this)));
   }

   ESHTMLElement(HTMLElement element, String name, ESObject prototype) {
      super(element, name, prototype);
      this.addField(Names.style, 28, Value.makeObjectValue(new ESHTMLElement$StyleObject(this)));
   }

   @Override
   public long requestFieldValue(String name) {
      if (name == Names.tagName) {
         return JavaScriptEngine.makeStringValue(((Element)this.getHTMLElement()).getTagName());
      } else if (name == Names.id) {
         return JavaScriptEngine.makeStringValue(((HTMLElement)this.getHTMLElement()).getId());
      } else if (name == Names.title) {
         return JavaScriptEngine.makeStringValue(((HTMLElement)this.getHTMLElement()).getTitle());
      } else if (name == Names.dir) {
         return JavaScriptEngine.makeStringValue(((HTMLElement)this.getHTMLElement()).getDir());
      } else if (name == Names.lang) {
         return JavaScriptEngine.makeStringValue(((HTMLElement)this.getHTMLElement()).getLang());
      } else {
         return name == Names.className ? JavaScriptEngine.makeStringValue(((HTMLElement)this.getHTMLElement()).getClassName()) : super.requestFieldValue(name);
      }
   }

   HTMLElement getHTMLElement() {
      return (HTMLElement)this.getNode();
   }

   static HTMLElement getHTMLElement(long paramValue) throws ThrownValue {
      Object obj = Convert.toObject(paramValue);
      if (obj instanceof ESElement) {
         return (HTMLElement)((ESHTMLElement)obj).getHTMLElement();
      } else {
         throw ThrownValue.referenceError("Parm not a html element");
      }
   }
}
