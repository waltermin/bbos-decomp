package net.rim.ecmascript.runtime;

import net.rim.vm.Reflect;

class JavaConstructor extends JavaCallable {
   private int _constructor;
   private int _class;

   JavaConstructor(JavaClass jc, String name, int clazz, int constructor, int[] parms) {
      super("JavaConstructor", jc, name, clazz, parms, true);
      this._constructor = constructor;
      this._class = clazz;
   }

   @Override
   public long run() {
      return Value.makeObjectValue(JavaObject.createInstance(Reflect.newInstance(this._class, this._constructor, this.convertArgs())));
   }

   int getConstructor() {
      return this._constructor;
   }
}
