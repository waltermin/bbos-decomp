package net.rim.device.api.collection;

import net.rim.device.api.util.Comparator;

public interface SortableCollection {
   Comparator getComparator();

   void setComparator(Comparator var1);
}
