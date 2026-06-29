package net.rim.ecmascript.runtime;

import java.util.Enumeration;
import java.util.Vector;

class JavaClassInstance extends JavaObject implements ESThunkInterface {
   private JavaClass _javaClass;
   private boolean _haveFields;
   private JavaThunkHandler _thunk;

   JavaClassInstance(Object obj) {
      super(obj);
      this._javaClass = JavaClass.findClass(obj);
   }

   static native void setForeign(Object var0, Object var1);

   static native void setOverridden(Object var0, String var1);

   void setOverrides(ESObject overrides) {
      this._thunk = new JavaThunkHandler(overrides, this._javaClass.getName());
      setForeign(super._obj, this);
      Vector v = (Vector)(new Object());
      overrides.enumerate(v, false);

      for (int i = v.size() - 1; i >= 0; i--) {
         String name = (String)v.elementAt(i);
         this.putField(name, overrides.getField(name));
         setOverridden(super._obj, name);
      }
   }

   @Override
   public long runDWord(String name, int[] parmTypes, int[] parms, int returnType) {
      return this._thunk.runDWord(this, name, parmTypes, parms, returnType);
   }

   @Override
   public int runWord(String name, int[] parmTypes, int[] parms, int returnType) {
      return (int)this._thunk.runDWord(this, name, parmTypes, parms, returnType);
   }

   @Override
   public void runVoid(String name, int[] parmTypes, int[] parms, int returnType) {
      this._thunk.runDWord(this, name, parmTypes, parms, returnType);
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      if (!this._haveFields) {
         this._javaClass.defineFields(this);
         this._haveFields = true;
      }

      long fieldValue = this.noRedirectGetField(name);
      JavaField f = JavaClass.getJavaField(fieldValue, name);
      return f != null ? f.put(super._obj, value) : this._javaClass.notifyFieldChanged(name, value);
   }

   @Override
   public long requestFieldValue(String name) {
      long value = this.getOwnField(name);
      if (Value.getType(value) == 2) {
         if (!this._haveFields) {
            this._javaClass.defineFields(this);
            this._haveFields = true;
            value = this.getOwnField(name);
         }

         if (Value.getType(value) == 2) {
            value = this._javaClass.getInstanceMethod(name);
         }
      }

      JavaField f = JavaClass.getJavaField(value, name);
      if (f != null) {
         return f.get(super._obj);
      }

      if (Value.getType(value) == 2) {
         value = this._javaClass.getField(name);
      }

      return value;
   }

   @Override
   void enumerate(Vector v) {
      if (!(super._obj instanceof Object)) {
         super.enumerate(v);
      } else {
         Enumeration e = (Enumeration)super._obj;

         while (e.hasMoreElements()) {
            v.addElement(e.nextElement());
         }
      }
   }
}
