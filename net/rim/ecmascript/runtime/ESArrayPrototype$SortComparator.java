package net.rim.ecmascript.runtime;

import net.rim.device.api.util.IntComparator;

class ESArrayPrototype$SortComparator implements IntComparator {
   private GlobalObject _global;
   private ESFunction _compareFn;
   private long[] _values;
   private ThrownValue _thrownValue;
   private int[] _indexes;
   private final ESArrayPrototype this$0;

   int[] getIndexArray() {
      this._indexes = new int[this._values.length];
      int i = this._indexes.length - 1;

      while (i >= 0) {
         this._indexes[i] = i--;
      }

      return this._indexes;
   }

   long[] reorder() {
      long[] sorted = new long[this._values.length];

      for (int i = this._indexes.length - 1; i >= 0; i--) {
         sorted[i] = this._values[this._indexes[i]];
      }

      return sorted;
   }

   int compare(long x, long y) {
      if (x == Value.DEFAULT) {
         return y == Value.DEFAULT ? 0 : 1;
      }

      if (y == Value.DEFAULT) {
         return -1;
      }

      if (Value.getType(x) == 2) {
         return Value.getType(y) == 2 ? 0 : 1;
      }

      if (Value.getType(y) == 2) {
         return -1;
      }

      try {
         if (this._compareFn == null) {
            return Convert.toString(x).compareTo(Convert.toString(y));
         }

         this.this$0.compareParms[0] = x;
         this.this$0.compareParms[1] = y;
         return Convert.toSign(this._global.callFunction(this._compareFn, this.this$0.compareParms));
      } catch (ThrownValue th) {
         this._thrownValue = th;
         throw new ESArrayPrototype$SortException();
      }
   }

   ThrownValue getThrownValue() {
      return this._thrownValue;
   }

   @Override
   public int compare(int i, int j) {
      return this.compare(this._values[i], this._values[j]);
   }

   ESArrayPrototype$SortComparator(ESArrayPrototype _1, GlobalObject global, ESFunction compareFn, long[] values) {
      this.this$0 = _1;
      this._global = global;
      this._values = values;
      this._compareFn = compareFn;
   }
}
