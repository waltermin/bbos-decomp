package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.Convert;
import net.rim.ecmascript.runtime.ThrownValue;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Attr;

class ESAttr extends ESNode {
   ESAttr(Attr attr) {
      super(attr, Names.Attr);
      this.addField(Names.name, 29, JavaScriptEngine.makeStringValue(attr.getName()));
      this.addField(Names.specified, 29, Value.makeBooleanValue(attr.getSpecified()));
      this.addField(Names.ownerElement, 29, JavaScriptEngine.getInstance().lookupElementToESObjectLong(attr.getOwnerElement()));
   }

   Attr getAttr() {
      return (Attr)this.getNode();
   }

   @Override
   public long requestFieldValue(String name) {
      return name == Names.value ? JavaScriptEngine.makeStringValue(this.getAttr().getValue()) : super.requestFieldValue(name);
   }

   static Attr getAttr(long value) throws ThrownValue {
      Object obj = Convert.toObject(value);
      if (!(obj instanceof ESAttr)) {
         throw ThrownValue.typeError("Object not Attr");
      } else {
         return ((ESAttr)obj).getAttr();
      }
   }
}
