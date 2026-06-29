package net.rim.ecmascript.runtime;

import net.rim.vm.Reflect;

public class JavaObject extends RedirectedObject {
   protected Object _obj;

   protected JavaObject(Object obj) {
      super("JavaObject", GlobalObject.getInstance().objectPrototype);
      this._obj = obj;
   }

   public static JavaObject createInstance(Object o) {
      if (o instanceof boolean[]) {
         return new JavaBooleanArray((boolean[])o);
      } else if (o instanceof byte[]) {
         return new JavaByteArray((byte[])o);
      } else if (o instanceof char[]) {
         return new JavaCharArray((char[])o);
      } else if (o instanceof short[]) {
         return new JavaShortArray((short[])o);
      } else if (o instanceof int[]) {
         return new JavaIntArray((int[])o);
      } else if (o instanceof float[]) {
         return new JavaFloatArray((float[])o);
      } else if (o instanceof long[]) {
         return new JavaLongArray((long[])o);
      } else if (o instanceof double[]) {
         return new JavaDoubleArray((double[])o);
      } else if (o instanceof Object[]) {
         int objClass = Reflect.getComponentClass(o);
         String name = Reflect.getClassName(objClass);
         JavaClass jc = new JavaClass(name, name, objClass);
         return new JavaObjectArray(jc, (Object[])o);
      } else {
         return new JavaClassInstance(o);
      }
   }

   Object getJavaObject() {
      return this._obj;
   }

   @Override
   public long defaultValue() {
      return this.defaultStringValue();
   }

   @Override
   public long defaultNumberValue() {
      return Value.makeObjectValue(this);
   }

   @Override
   public long defaultStringValue() {
      return Value.makeStringValue(this._obj.toString());
   }
}
