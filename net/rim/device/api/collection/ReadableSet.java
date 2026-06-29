package net.rim.device.api.collection;

import java.util.Enumeration;

public interface ReadableSet extends Collection {
   int size();

   boolean contains(Object var1);

   Enumeration getElements();

   int getElements(Object[] var1);
}
