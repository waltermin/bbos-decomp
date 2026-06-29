package net.rim.ecmascript.runtime;

class JavaOverloadedConstructor extends JavaOverloadedCallable {
   JavaOverloadedConstructor(JavaClass jc, String name) {
      super("JavaOverloadedConstructor", jc, name, true);
   }

   @Override
   public long run() {
      JavaConstructor c = (JavaConstructor)this.findOverload();
      return GlobalObject.getInstance().callConstructor(c, null, super._parmArray, super._firstParm, super._numParms);
   }
}
