package net.rim.device.apps.internal.browser.javascript;

import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ESNodeList extends RedirectedObject {
   protected NodeList _nodeList;

   public ESNodeList(NodeList nodeList) {
      super(Names.NodeList, JavaScriptEngine.getInstance()._nodeListPrototype);
      this._nodeList = nodeList;
   }

   @Override
   public long requestFieldValue(String name) {
      return name == Names.length ? Value.makeIntegerValue(this._nodeList.getLength()) : Value.DEFAULT;
   }

   long requestItem(int index) {
      Node node = this._nodeList.item(index);
      return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node);
   }

   @Override
   public long requestElementValue(long element) {
      switch (Value.getType(element)) {
         case 0:
            Node node = this._nodeList.item(Value.getIntegerValue(element));
            if (node != null) {
               return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node);
            }
         default:
            return Value.DEFAULT;
      }
   }
}
