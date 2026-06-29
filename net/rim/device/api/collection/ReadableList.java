package net.rim.device.api.collection;

public interface ReadableList extends Collection {
   int size();

   Object getAt(int var1);

   int getAt(int var1, int var2, Object[] var3, int var4);

   int getIndex(Object var1);
}
