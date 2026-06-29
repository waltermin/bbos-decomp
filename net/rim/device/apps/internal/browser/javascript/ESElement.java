package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.ESObject;
import org.w3c.dom.Element;

class ESElement extends ESNode {
   ESElement(Element element) {
      this(element, Names.Element);
   }

   ESElement(Element element, String name) {
      this(element, name, JavaScriptEngine.getInstance()._elementPrototype);
   }

   ESElement(Element element, String name, ESObject prototype) {
      super(element, name, prototype);
   }

   Element getElement() {
      return (Element)this.getNode();
   }
}
