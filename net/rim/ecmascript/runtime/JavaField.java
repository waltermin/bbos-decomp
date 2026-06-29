package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Resources;
import net.rim.vm.Reflect;

class JavaField extends ESObject {
   protected int _field;
   protected int _class;

   static JavaField makeJavaField(int c, int f) {
      switch (Reflect.classify(Reflect.getFieldType(c, f))) {
         case 0:
         case 7:
         case 8:
         case 9:
         case 10:
            return new JavaObjectField(c, f);
         case 1:
            return new JavaBooleanField(c, f);
         case 2:
         default:
            return new JavaByteField(c, f);
         case 3:
            return new JavaCharField(c, f);
         case 4:
            return new JavaShortField(c, f);
         case 5:
            return new JavaIntField(c, f);
         case 6:
            return new JavaLongField(c, f);
         case 11:
            return new JavaFloatField(c, f);
         case 12:
            return new JavaDoubleField(c, f);
      }
   }

   private ThrownValue noAccess() {
      return ThrownValue.referenceError(Resources.getString(60), Reflect.getClassName(this._class), Reflect.getFieldName(this._class, this._field));
   }

   long get(Object instance) {
      try {
         return this.getValue(instance);
      } finally {
         throw this.noAccess();
      }
   }

   boolean put(Object instance, long value) {
      try {
         this.putValue(instance, value);
         return true;
      } finally {
         throw this.noAccess();
      }
   }

   protected long getValue(Object _1) {
      throw null;
   }

   protected void putValue(Object _1, long _2) {
      throw null;
   }
}
