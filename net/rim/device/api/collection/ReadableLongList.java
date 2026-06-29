package net.rim.device.api.collection;

public interface ReadableLongList extends Collection {
   int size();

   long getLongAt(int var1);

   int getAt(int var1, int var2, long[] var3, int var4);

   int getIndex(long var1);
}
