package net.rim.ecmascript.runtime;

class ESBooleanPrototype extends ESBoolean {
   ESBooleanPrototype() {
      super(false, true);
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().booleanConstructor));
      this.addHostFunction(new ESBooleanPrototype$1(this, "String", "toString", 0));
      this.addHostFunction(new ESBooleanPrototype$2(this, "String", "valueOf", 0));
   }
}
