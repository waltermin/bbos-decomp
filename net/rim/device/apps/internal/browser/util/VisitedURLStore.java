package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.apps.internal.browser.core.BrowserDaemonRegistry;

public final class VisitedURLStore implements Persistable {
   private Object[] _sortedURLsEncodings = new Object[100];
   private Object[] _recentURLsEncodings = new Object[100];
   private int _numberOfElements;
   private static final long URL_STORE_KEY;
   public static final int MAX_SIZE;
   private static PersistentObject _persist;

   public static final VisitedURLStore getInstance() {
      _persist = RIMPersistentStore.getPersistentObject(6613947982019865069L);
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _persist.setContents(new VisitedURLStore(), 51);
            _persist.commit();
         }
      }

      return (VisitedURLStore)_persist.getContents();
   }

   private static final Object encode(String string) {
      return PersistentContent.encode(string);
   }

   private static final String decode(Object encoding) {
      return PersistentContent.decodeString(encoding);
   }

   private static final int linearFind(Object[] array, int numberOfElements, String item, int notFound) {
      int length = numberOfElements;

      for (int i = 0; i < length; i++) {
         if (item.equals(decode(array[i]))) {
            return i;
         }
      }

      return notFound;
   }

   private static final boolean delete(Object[] array, int numberOfElements, int index) {
      if (index < 0) {
         return false;
      }

      System.arraycopy(array, index + 1, array, index, numberOfElements - index - 1);
      array[numberOfElements - 1] = null;
      return true;
   }

   private static final boolean linearDelete(Object[] array, int numberOfElements, String item) {
      return delete(array, numberOfElements, linearFind(array, numberOfElements, item, -1));
   }

   private static final void addToFront(Object[] array, int numberOfElements, String item) {
      System.arraycopy(array, 0, array, 1, numberOfElements);
      array[0] = encode(item);
   }

   private static final void moveToFront(Object[] array, int numberOfElements, String item) {
      int index = linearFind(array, numberOfElements, item, numberOfElements);
      if (index > 0) {
         System.arraycopy(array, 0, array, 1, index);
      }

      array[0] = encode(item);
   }

   private static final int sortedFind(Object[] array, int numberOfElements, String item) {
      int low = 0;
      int high = numberOfElements - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         String key = decode(array[mid]);
         int result = StringUtilities.compareToIgnoreCase(item, key, 1701707776);
         if (result < 0) {
            high = mid - 1;
         } else {
            if (result <= 0) {
               return mid;
            }

            low = mid + 1;
         }
      }

      return -(low + 1);
   }

   private static final boolean sortedInsert(Object[] array, int numberOfElements, String item) {
      int index = sortedFind(array, numberOfElements, item);
      if (index >= 0) {
         return false;
      }

      index = -index - 1;
      System.arraycopy(array, index, array, index + 1, numberOfElements - index);
      array[index] = encode(item);
      return true;
   }

   private static final boolean sortedDelete(Object[] array, int numberOfElements, String item) {
      return delete(array, numberOfElements, sortedFind(array, numberOfElements, item));
   }

   public final int size() {
      return this._numberOfElements;
   }

   public final synchronized String getElementAt(int index) {
      return index >= 0 && index < this._numberOfElements ? decode(this._sortedURLsEncodings[index]) : null;
   }

   public final synchronized String getRecentElementAt(int index) {
      return index >= 0 && index < this._numberOfElements ? decode(this._recentURLsEncodings[index]) : null;
   }

   public final synchronized void clear() {
      this._numberOfElements = 0;

      for (int i = 0; i < 100; i++) {
         String item = decode(this._recentURLsEncodings[i]);
         if (item != null) {
            BrowserDaemonRegistry.notifyUrlCollectionListeners(item, 1);
         }

         this._sortedURLsEncodings[i] = null;
         this._recentURLsEncodings[i] = null;
      }

      _persist.commit();
   }

   public final synchronized void cleanup() {
      while (this._numberOfElements < 50) {
         this.deleteOldestElement();
      }

      _persist.commit();
   }

   public final synchronized boolean delete(String item) {
      if (!sortedDelete(this._sortedURLsEncodings, this._numberOfElements, item)) {
         return false;
      }

      linearDelete(this._recentURLsEncodings, this._numberOfElements, item);
      this._numberOfElements--;
      _persist.commit();
      BrowserDaemonRegistry.notifyUrlCollectionListeners(item, 1);
      return true;
   }

   private final void deleteOldestElement() {
      if (this._numberOfElements > 0) {
         int elementToDelete = this._numberOfElements - 1;
         String item = decode(this._recentURLsEncodings[elementToDelete]);
         sortedDelete(this._sortedURLsEncodings, this._numberOfElements, item);
         delete(this._recentURLsEncodings, this._numberOfElements, elementToDelete);
         this._numberOfElements--;
         BrowserDaemonRegistry.notifyUrlCollectionListeners(item, 1);
      }
   }

   public final synchronized void add(String item) {
      if (this._numberOfElements >= 100) {
         this.deleteOldestElement();
      }

      if (!sortedInsert(this._sortedURLsEncodings, this._numberOfElements, item)) {
         moveToFront(this._recentURLsEncodings, this._numberOfElements, item);
      } else {
         addToFront(this._recentURLsEncodings, this._numberOfElements, item);
         this._numberOfElements++;
         BrowserDaemonRegistry.notifyUrlCollectionListeners(item, 0);
      }

      _persist.commit();
   }

   public final synchronized int[] match(String prefix) {
      int[] result = new int[]{-1, -1};
      int low = sortedFind(this._sortedURLsEncodings, this._numberOfElements, prefix);
      if (low < 0) {
         low = -low - 1;
      }

      int high = sortedFindEnd(this._sortedURLsEncodings, this._numberOfElements, prefix, low);
      if (high >= low) {
         result[0] = low;
         result[1] = high;
      }

      return result;
   }

   private static final int sortedFindEnd(Object[] array, int numberOfElements, String prefix, int low) {
      int match = -1;
      int high = numberOfElements - 1;

      while (low >= 0 && low <= high) {
         int mid = low + high >> 1;
         if (StringUtilities.startsWithIgnoreCaseAndAccents(decode(array[mid]), prefix)) {
            low = mid + 1;
            match = mid;
         } else {
            high = mid - 1;
         }
      }

      return match;
   }
}
