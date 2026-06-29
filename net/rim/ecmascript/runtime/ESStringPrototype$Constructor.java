package net.rim.ecmascript.runtime;

class ESStringPrototype$Constructor extends Constructor {
   ESStringPrototype$Constructor() {
      super("String", GlobalObject.getInstance().stringPrototype);
      this.addHostFunction(new ESStringPrototype$Constructor$1(this, "String", "fromCharCode", 1));
   }

   @Override
   public long run() {
      String str;
      if (this.getNumParms() == 0) {
         str = "";
      } else {
         str = Convert.toString(this.getParm(0));
      }

      return this.calledAsConstructor() ? Value.makeObjectValue(new ESString(str)) : Value.makeStringValue(str);
   }
}
