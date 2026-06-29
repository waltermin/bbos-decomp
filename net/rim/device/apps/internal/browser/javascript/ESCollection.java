package net.rim.device.apps.internal.browser.javascript;

import net.rim.device.apps.internal.browser.html.HTMLCollectionImpl;
import net.rim.ecmascript.runtime.RedirectedObject;
import net.rim.ecmascript.runtime.Value;
import org.w3c.dom.Node;
import org.w3c.dom.html2.HTMLCollection;

class ESCollection extends RedirectedObject {
   protected HTMLCollectionImpl _collection;

   public ESCollection(HTMLCollection collection) {
      this._collection = (HTMLCollectionImpl)collection;
   }

   @Override
   public long requestFieldValue(String name) {
      if (name == Names.length) {
         return Value.makeIntegerValue(this._collection.getLength());
      }

      Object nodeList = this._collection.namedItems(name);
      return nodeList == null ? Value.DEFAULT : JavaScriptEngine.getInstance().lookupElementToESObjectLong(nodeList);
   }

   long requestNamedItems(String value) {
      Object nodeList = this._collection.namedItems(value);
      return JavaScriptEngine.getInstance().lookupElementToESObjectLong(nodeList);
   }

   long requestItem(int index) {
      Node node = this._collection.item(index);
      return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node);
   }

   @Override
   public long requestElementValue(long element) {
      switch (Value.getType(element)) {
         case 0:
            Node node = this._collection.item(Value.getIntegerValue(element));
            if (node != null) {
               return JavaScriptEngine.getInstance().lookupElementToESObjectLong(node);
            }
            break;
         case 5:
            Object nodeList = this._collection.namedItems(Value.getStringValue(element));
            if (nodeList != null) {
               return JavaScriptEngine.getInstance().lookupElementToESObjectLong(nodeList);
            }
      }

      return Value.DEFAULT;
   }

   public int getCollectionLength() {
      return this._collection.getLength();
   }
}
