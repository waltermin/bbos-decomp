package net.rim.ecmascript.runtime;

class ESNumberPrototype extends ESNumber {
   ESNumberPrototype() {
      super((double)0L, true);
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().numberConstructor));
      this.addHostFunction(new ESNumberPrototype$1(this, "Number", "toString"));
      this.addHostFunction(new ESNumberPrototype$2(this, "Number", "valueOf"));
      this.addHostFunction(new ESNumberPrototype$3(this, "Number", "toFixed", 1));
      this.addHostFunction(new ESNumberPrototype$4(this, "Number", "toPrecision", 1));
      this.addHostFunction(new ESNumberPrototype$5(this, "Number", "toExponential", 1));
   }
}
