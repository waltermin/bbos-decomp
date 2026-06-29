package net.rim.device.apps.internal.browser.history;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.internal.browser.options.BrowserConfigRecord;
import net.rim.vm.Array;

public class LongTermHistory implements Persistable {
   private LongTermHistoryNode[] _nodes;
   private int _numberOfElements;
   private static final long LONG_TERM_HISTORY_KEY;
   private static final int MAX_SIZE;
   private static int GROW_ZISE = 10;
   private static PersistentObject _persist;

   public static LongTermHistory getInstance() {
      _persist = RIMPersistentStore.getPersistentObject(-528033205888586755L);
      synchronized (_persist) {
         if (_persist.getContents() == null) {
            _persist.setContents(new LongTermHistory(), 51);
            _persist.commit();
         }
      }

      return (LongTermHistory)_persist.getContents();
   }

   private LongTermHistory() {
      this._nodes = new LongTermHistoryNode[GROW_ZISE];
   }

   public synchronized int getNumberOfElements() {
      return this._numberOfElements;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public synchronized void addUrl(String url, String title, BrowserConfigRecord browserConfig) {
      if (url != null && url.length() != 0) {
         LongTermHistoryNode node = null;
         boolean var7 = false /* VF: Semaphore variable */;

         try {
            var7 = true;
            node = new LongTermHistoryNode(url, title, browserConfig);
            var7 = false;
         } finally {
            if (var7) {
               return;
            }
         }

         if (this._numberOfElements >= 500) {
            this.cleanup(100);
         }

         if (this._numberOfElements >= this._nodes.length) {
            Array.resize(this._nodes, this._nodes.length + GROW_ZISE);
         }

         this._nodes[this._numberOfElements] = node;
         this._numberOfElements++;
         _persist.commit();
      }
   }

   void cleanup(int min) {
      long cutoffDate = System.currentTimeMillis() - 1209600000;
      int index = binarySearch(this._nodes, cutoffDate, 0, this._numberOfElements);
      if (min > 0) {
         index = index < min ? min : index;
      }

      if (index >= 1) {
         int newLength = this._numberOfElements - index + 1;
         System.arraycopy(this._nodes, this._numberOfElements - newLength, this._nodes, 0, newLength);
         Array.resize(this._nodes, newLength);
         this._numberOfElements = newLength;
      }
   }

   public synchronized LongTermHistoryNode[] getMostRecentUrls(int number) {
      LongTermHistoryNode[] result = new LongTermHistoryNode[number];
      int numberAdded = 0;

      for (int inOffset = this._numberOfElements - 1; numberAdded < number && inOffset >= 0; inOffset--) {
         LongTermHistoryNode currentNode = this._nodes[inOffset];
         String currentUrl = currentNode.getUrl();
         boolean found = false;
         int i = 0;

         while (true) {
            if (i < numberAdded) {
               if (!result[i].getUrl().equals(currentUrl)) {
                  i++;
                  continue;
               }

               found = true;
            }

            if (!found) {
               result[numberAdded] = currentNode;
               numberAdded++;
            }
            break;
         }
      }

      if (result.length != numberAdded) {
         Array.resize(result, numberAdded);
      }

      return result;
   }

   public synchronized LongTermHistoryNode[] getUrlsInRange(long start, long end, boolean timeSort) {
      if (start < end
         && start >= 0
         && end >= 0
         && this._numberOfElements != 0
         && start <= this._nodes[this._numberOfElements - 1].getTimestamp()
         && end >= this._nodes[0].getTimestamp()) {
         int fromIndex = binarySearch(this._nodes, start, 0, this._numberOfElements);
         int toIndex = binarySearch(this._nodes, end, fromIndex, this._numberOfElements);
         int length = toIndex - fromIndex;
         LongTermHistoryNode[] result = new LongTermHistoryNode[toIndex - fromIndex];

         for (int i = length - 1; i > -1; i--) {
            result[i] = this._nodes[i + fromIndex];
         }

         Arrays.sort(result, new LTHUrlComparator());
         int index = 0;
         length = result.length;

         for (int i = 1; i < length; i++) {
            String url = result[index].getUrl();
            String nextUrl = result[i].getUrl();

            for (length = result.length; url.equals(nextUrl) && i < length; nextUrl = i < length ? result[i].getUrl() : null) {
               result[i] = null;
               i++;
            }

            if (nextUrl != null) {
               if (++index < i) {
                  result[index] = result[i];
               }
            }
         }

         if (index + 1 < length) {
            Array.resize(result, index + 1);
         }

         if (timeSort) {
            Arrays.sort(result, new LTHTimeComparator());
         } else {
            Arrays.sort(result, new LTHTitleComparator());
         }

         return result;
      } else {
         return null;
      }
   }

   public synchronized void clear() {
      if (this._numberOfElements != 0) {
         this._numberOfElements = 0;
         Array.resize(this._nodes, this._numberOfElements);
         _persist.commit();
      }
   }

   public synchronized void delete(String strToMatch, long startTime, long endTime, boolean matchUrl) {
      if (endTime >= startTime) {
         int fromIndex = 0;
         int toIndex = this._numberOfElements;
         if (startTime > 0) {
            fromIndex = binarySearch(this._nodes, startTime, 0, this._numberOfElements);
         }

         if (endTime < Long.MAX_VALUE) {
            toIndex = binarySearch(this._nodes, endTime, fromIndex, this._numberOfElements);
         }

         if (strToMatch == null) {
            int newLength = this._numberOfElements - toIndex + fromIndex;
            System.arraycopy(this._nodes, toIndex, this._nodes, fromIndex, this._numberOfElements - toIndex);
            Array.resize(this._nodes, newLength);
            this._numberOfElements = newLength;
            _persist.commit();
         } else {
            for (int i = toIndex - 1; i >= fromIndex; i--) {
               LongTermHistoryNode node = this._nodes[i];
               String currentStr = null;
               if (matchUrl) {
                  currentStr = node.getUrl();
               } else {
                  currentStr = node.getDomain();
               }

               if (strToMatch.equals(currentStr)) {
                  Arrays.removeAt(this._nodes, i);
                  this._numberOfElements--;
               }
            }

            _persist.commit();
         }
      }
   }

   private static synchronized int binarySearch(LongTermHistoryNode[] a, long key, int fromIndex, int toIndex) {
      int low = fromIndex;
      int high = toIndex - 1;

      while (low <= high) {
         int mid = low + high >> 1;
         if (key == a[mid].getTimestamp()) {
            high = mid;
            if (mid == low) {
               return low;
            }
         } else if (key < a[mid].getTimestamp()) {
            high = mid - 1;
         } else {
            low = mid + 1;
         }
      }

      return low;
   }
}
