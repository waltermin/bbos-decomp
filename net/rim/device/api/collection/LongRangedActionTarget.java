package net.rim.device.api.collection;

public interface LongRangedActionTarget {
   long FROM_THE_START = Long.MIN_VALUE;
   long TO_THE_END = Long.MAX_VALUE;

   void apply(long var1, long var3, long var5, Object var7);
}
