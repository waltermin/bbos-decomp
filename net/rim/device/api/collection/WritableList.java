package net.rim.device.api.collection;

public interface WritableList extends Collection {
   void add(Object var1);

   void insertAt(int var1, Object var2);

   void removeAt(int var1);

   void remove(Object var1);

   void removeAll();
}
