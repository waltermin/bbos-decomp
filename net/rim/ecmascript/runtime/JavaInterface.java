package net.rim.ecmascript.runtime;

import java.util.Vector;
import net.rim.vm.Reflect;

class JavaInterface extends HostFunction implements ESThunkInterface {
   int _class;
   ESObject _overrides;
   JavaThunkHandler _thunk;

   JavaInterface(int clazz) {
      super("JavaInterface", Reflect.getClassName(clazz), 0, true);
      this._class = clazz;
   }

   @Override
   public long run() {
      JavaObject obj = JavaObject.createInstance(this);
      Vector v = (Vector)(new Object());
      this._overrides.enumerate(v);

      for (int i = v.size() - 1; i >= 0; i--) {
         String name = (String)v.elementAt(i);
         obj.putField(name, this._overrides.getField(name));
      }

      return Value.makeObjectValue(obj);
   }

   public void setOverrides(ESObject overrides) {
      this._overrides = overrides;
      this._thunk = new JavaThunkHandler(overrides, this.getName());
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
}
