package net.rim.ecmascript.runtime;

class ESBooleanPrototype$Constructor extends Constructor {
   ESBooleanPrototype$Constructor() {
      super("Boolean", GlobalObject.getInstance().booleanPrototype);
   }

   @Override
   public long run() {
      boolean value = Convert.toBoolean(this.getParm(0));
      return this.calledAsConstructor() ? Value.makeObjectValue(new ESBoolean(value)) : Value.makeBooleanValue(value);
   }
}
