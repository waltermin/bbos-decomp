package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

class JavaPackage extends RedirectedObject {
   private String _name;

   JavaPackage(String name) {
      super("JavaPackage", GlobalObject.getInstance().objectPrototype);
      this._name = name;
      if (name == null) {
         GlobalObject.getInstance().rootJavaPackage = this;
      }
   }

   static JavaClass createClass(String name) {
      JavaPackage curr = GlobalObject.getInstance().rootJavaPackage;
      int lastIndex = 0;
      int index = 0;

      while (true) {
         index = name.indexOf(46, lastIndex);
         if (index == -1) {
            return (JavaClass)curr.getPackage(Misc.stringIntern(name.substring(lastIndex)), true);
         }

         curr = (JavaPackage)curr.getPackage(Misc.stringIntern(name.substring(lastIndex, index)), false);
         lastIndex = index + 1;
      }
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      return true;
   }

   static native boolean hasOpenAccess(Class var0);

   ESObject getPackage(String name, boolean tryClass) {
      long value = this.noRedirectGetField(name);
      if (Value.getType(value) != 2) {
         return Value.checkIfObjectValue(value);
      }

      String fullName;
      if (this._name == null) {
         fullName = name;
      } else {
         fullName = this._name + "." + name;
      }

      try {
         if (tryClass) {
            Class cls = Class.forName(fullName);
            GlobalObject.getInstance();
            if (!GlobalObject.noLiveConnect && hasOpenAccess(cls)) {
               JavaClass jc = new JavaClass(fullName, name, cls);
               GlobalObject.getInstance().javaClassLookupTable.put(fullName, jc);
               return jc;
            }

            throw ThrownValue.referenceError(Resources.getString(58), fullName);
         }
      } finally {
         return new JavaPackage(fullName);
      }

      return new JavaPackage(fullName);
   }

   long getJavaClass(String name) {
      long value = this.requestFieldValue(name);
      ESObject obj = Value.checkIfObjectValue(value);
      if (obj == null) {
         return Value.UNDEFINED;
      } else {
         return !(obj instanceof JavaClass) ? Value.UNDEFINED : value;
      }
   }

   @Override
   public long requestFieldValue(String name) {
      long value = this.noRedirectGetField(name);
      if (Value.getType(value) != 2) {
         return value;
      }

      ESObject obj = this.getPackage(name, true);
      return obj == null ? Value.UNDEFINED : Value.makeObjectValue(obj);
   }

   @Override
   public long defaultStringValue() {
      return Value.makeStringValue("[JavaPackage " + (this._name == null ? "" : this._name) + "]");
   }
}
