package net.rim.ecmascript.runtime;

class ESArrayPrototype$1 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$1(ESArrayPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      long length = Convert.toUint32(thiz.getField("length"));
      if (length == 0) {
         thiz.putField("length", Value.ZERO);
         return Value.UNDEFINED;
      } else {
         length -= 1;
         length = Value.makeLongValue(length);
         long value = thiz.getElement(length);
         thiz.deleteElement(length);
         thiz.putField("length", length);
         return value;
      }
   }
}
