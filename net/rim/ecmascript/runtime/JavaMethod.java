package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;
import net.rim.vm.Reflect;

class JavaMethod extends JavaCallable {
   private int _class;
   private int _method;
   private int _returnType;
   private String _shortName;

   JavaMethod(JavaClass jc, String shortName, String name, int clazz, int method, int returnType, int[] parms) {
      super("JavaMethod", jc, name, returnType, parms, false);
      this._method = method;
      this._class = clazz;
      this._returnType = returnType;
      this._shortName = shortName;
   }

   public String getShortName() {
      return this._shortName;
   }

   @Override
   public long run() {
      Object thiz = null;
      if (this.getThis() instanceof JavaObject) {
         thiz = ((JavaObject)this.getThis()).getJavaObject();
      }

      int[] args = this.convertArgs();
      switch (Reflect.classify(this._returnType)) {
         case 0:
         case 9:
         case 10:
         case 13:
            Reflect.invokeWord(this._class, this._method, thiz, this.convertArgs());
            return Value.UNDEFINED;
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         default:
            return Value.makeIntegerValue(Reflect.invokeWord(this._class, this._method, thiz, args));
         case 6:
            return Value.makeLongValue(Reflect.invokeDWord(this._class, this._method, thiz, args));
         case 7:
         case 8:
            int var4 = Reflect.invokeWord(this._class, this._method, thiz, args);
            if (var4 == 0) {
               return Value.NULL;
            }

            return Value.makeObjectValue(JavaObject.createInstance(Misc.toObject(var4)));
         case 11:
            return Value.makeDoubleValue(Misc.toFloat(Reflect.invokeWord(this._class, this._method, thiz, args)));
         case 12:
            return Value.makeDoubleValue(Misc.toDouble(Reflect.invokeDWord(this._class, this._method, thiz, args)));
         case 14:
            int rc = Reflect.invokeWord(this._class, this._method, thiz, args);
            return rc == 0 ? Value.NULL : Value.makeStringValue((String)Misc.toObject(rc));
      }
   }
}
