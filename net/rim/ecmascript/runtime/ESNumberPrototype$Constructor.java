package net.rim.ecmascript.runtime;

class ESNumberPrototype$Constructor extends Constructor {
   ESNumberPrototype$Constructor() {
      super("Number", GlobalObject.getInstance().numberPrototype);
      this.addField("MAX_VALUE", 7, Value.makeDoubleValue((double)9218868437227405311L));
      this.addField("MIN_VALUE", 7, Value.makeDoubleValue(Double.MIN_VALUE));
      this.addField("NaN", 7, Value.NaN);
      this.addField("NEGATIVE_INFINITY", 7, Value.NEGATIVE_INFINITY);
      this.addField("POSITIVE_INFINITY", 7, Value.POSITIVE_INFINITY);
   }

   @Override
   public long run() {
      long value = this.getParm(0, Value.ZERO);
      return this.calledAsConstructor() ? Value.makeObjectValue(new ESNumber(Convert.toDouble(value))) : Convert.toNumber(value);
   }
}
