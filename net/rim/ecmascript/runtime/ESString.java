package net.rim.ecmascript.runtime;

import java.util.Vector;

public class ESString extends RedirectedObject {
   private String _value;

   public ESString(String value) {
      super("String", GlobalObject.getInstance().stringPrototype);
      this._value = value;
   }

   ESString(String value, boolean nullPrototypeOK) {
      super("String", GlobalObject.getInstance().stringPrototype, nullPrototypeOK);
      this._value = value;
   }

   @Override
   public long requestFieldValue(String name) {
      if (name == "length") {
         return Value.makeIntegerValue(this._value.length());
      }

      try {
         char ch = name.charAt(0);
         if (ch < '0' || ch > '9') {
            return Value.DEFAULT;
         }

         int i = Integer.parseInt(name);
         if (i >= 0 && i < this._value.length()) {
            return Value.makeStringValue(this._value.substring(i, i + 1));
         }
      } finally {
         return Value.DEFAULT;
      }

      return Value.DEFAULT;
   }

   @Override
   public void enumerateAll(Vector v) {
      super.enumerateAll(v);
      v.addElement("length");
   }

   @Override
   public int notifyFieldDeleted(String name) {
      return name == "length" ? 1 : 2;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      return name != "length";
   }

   public String getValue() {
      return this._value;
   }
}
