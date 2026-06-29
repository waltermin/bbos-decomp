package net.rim.ecmascript.runtime;

import java.util.Vector;

public class ESNamespace extends RedirectedObject {
   private String _prefix;
   private String _uri;

   ESNamespace(String uri, String prefix) {
      super("namespace", GlobalObject.getInstance().namespacePrototype);
      this._uri = uri;
      this._prefix = prefix;
   }

   ESNamespace(String uri, String prefix, boolean nullPrototypeOK) {
      super("namespace", GlobalObject.getInstance().namespacePrototype, nullPrototypeOK);
      this._uri = uri;
      this._prefix = prefix;
   }

   @Override
   public long requestFieldValue(String name) {
      if (name == "uri") {
         return this._uri == null ? Value.NULL : Value.makeStringValue(this._uri);
      } else if (name == "prefix") {
         return this._prefix == null ? Value.UNDEFINED : Value.makeStringValue(this._prefix);
      } else {
         return Value.DEFAULT;
      }
   }

   @Override
   public void enumerateAll(Vector v) {
      super.enumerateAll(v);
      v.addElement("uri");
      v.addElement("prefix");
   }

   @Override
   public int notifyFieldDeleted(String name) {
      return name != "uri" && name != "prefix" ? 2 : 1;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      if (name == "uri") {
         this._uri = Convert.toString(value);
         return false;
      } else if (name == "prefix") {
         this._prefix = Convert.toString(value);
         return false;
      } else {
         return true;
      }
   }

   String getPrefix() {
      return this._prefix;
   }

   String getURI() {
      return this._uri;
   }
}
