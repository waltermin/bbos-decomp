package net.rim.device.apps.internal.browser.sbloader;

import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.api.util.Comparator;

class SortedList extends SortedReadableList {
   SortedList(Comparator comparator, SbInfo[] list) {
      super(comparator);
      int len = list.length;

      for (int i = 0; i < len; i++) {
         this.doAdd(list[i]);
      }
   }
}
