package net.rim.device.api.collection;

public interface ReadableIntMap extends Collection {
   int size();

   Object get(int var1);

   int getKey(Object var1);

   boolean contains(int var1);
}
