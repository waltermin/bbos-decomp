package net.rim.device.api.collection;

public interface LongRangedActionTarget {
   long FROM_THE_START;
   long TO_THE_END;

   void apply(long var1, long var3, long var5, Object var7);
}
