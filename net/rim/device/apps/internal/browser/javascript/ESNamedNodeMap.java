package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class ESNamedNodeMap extends RedirectedObject {
   private NamedNodeMap _nodeMap;

   ESNamedNodeMap(NamedNodeMap nodeMap) {
      super(Names.NamedNodeMap, JavaScriptEngine.getInstance()._namedNodeMapPrototype);
      this._nodeMap = nodeMap;
   }

   final NamedNodeMap getNamedNodeMap() {
      return this._nodeMap;
   }

   @Override
   public final long requestFieldValue(String name) {
      return name == Names.length ? Value.makeIntegerValue(this._nodeMap.getLength()) : Value.DEFAULT;
   }

   @Override
   public final long requestElementValue(long element) {
      switch (Value.getType(element)) {
         case 0:
            Node node = this._nodeMap.item(Value.getIntegerValue(element));
            return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node);
         default:
            return Value.DEFAULT;
      }
   }
}
