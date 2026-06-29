package net.rim.ecmascript.runtime;

import net.rim.ecmascript.util.Misc;

class ESArrayPrototype extends ESArray {
   private long[] compareParms = Misc.newMixedArray(2);

   ESArrayPrototype() {
      super(0);
      this.setGrowthIncrement(20);
      this.setPrototype(GlobalObject.getInstance().objectPrototype);
   }

   private static long bound(long start, long length) {
      if (start < 0) {
         return start + length < 0 ? 0 : start + length;
      } else {
         return start > length ? length : start;
      }
   }

   void populate() {
      this.addField("constructor", 2, Value.makeObjectValue(GlobalObject.getInstance().arrayConstructor));
      this.addHostFunction(new ESArrayPrototype$ToString("toString"));
      this.addHostFunction(new ESArrayPrototype$ToString("toLocaleString"));
      this.addHostFunction(new ESArrayPrototype$Join());
      this.addHostFunction(new ESArrayPrototype$1(this, "Array", "pop"));
      this.addHostFunction(new ESArrayPrototype$2(this, "Array", "push", 1));
      this.addHostFunction(new ESArrayPrototype$3(this, "Array", "concat", 1));
      this.addHostFunction(new ESArrayPrototype$4(this, "Array", "reverse"));
      this.addHostFunction(new ESArrayPrototype$5(this, "Array", "shift"));
      this.addHostFunction(new ESArrayPrototype$6(this, "Array", "sort", 1));
      this.addHostFunction(new ESArrayPrototype$7(this, "Array", "slice", 2));
      this.addHostFunction(new ESArrayPrototype$8(this, "Array", "splice", 2));
      this.addHostFunction(new ESArrayPrototype$9(this, "Array", "unshift"));
   }
}
