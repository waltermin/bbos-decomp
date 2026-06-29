package net.rim.device.api.collection;

public interface ReadableLongMap extends Collection {
   int size();

   Object get(long var1);

   long getKey(Object var1);

   boolean contains(long var1);
}
