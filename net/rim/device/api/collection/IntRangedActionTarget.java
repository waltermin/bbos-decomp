package net.rim.device.api.collection;

public interface IntRangedActionTarget {
   int FROM_THE_START = Integer.MIN_VALUE;
   int TO_THE_END = Integer.MAX_VALUE;

   void apply(int var1, int var2, long var3, Object var5);
}
