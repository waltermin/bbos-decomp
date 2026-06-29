package net.rim.ecmascript.runtime;

class JavaOverloadedMethod extends JavaOverloadedCallable {
   JavaOverloadedMethod(JavaClass jc, String name) {
      super("JavaOverloadedMethod", jc, name, false);
   }

   @Override
   public long run() {
      JavaMethod m = (JavaMethod)this.findOverload();
      return GlobalObject.getInstance().callFunction(m, this.getThis(), super._parmArray, super._firstParm, super._numParms);
   }

   HostFunction unOverload() {
      switch (this.getNumOverloads()) {
         case -1:
            return this;
         case 0:
         default:
            return null;
         case 1:
            return this.getOverload(0);
      }
   }
}
