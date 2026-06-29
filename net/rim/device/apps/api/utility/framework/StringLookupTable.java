package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.collection.util.BigIntVector;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.vm.Array;

public class StringLookupTable implements Persistable {
   private StringLookupTable$StringBundle[] _bundles = new StringLookupTable$StringBundle[1];
   private BigIntVector _sortedIndex;
   private static final long STRING_LOOKUP_TABLE_GUID;
   private static StringLookupTable _lookupTable;

   public StringLookupTable() {
      this._bundles[0] = new StringLookupTable$StringBundle();
      this._sortedIndex = (BigIntVector)(new Object());
   }

   public static StringLookupTable getInstance() {
      if (_lookupTable == null) {
         PersistentObject store = RIMPersistentStore.getPersistentObject(-6406650129714817592L);
         _lookupTable = (StringLookupTable)store.getContents();
         if (_lookupTable == null) {
            synchronized (store) {
               _lookupTable = (StringLookupTable)store.getContents();
               if (_lookupTable == null) {
                  _lookupTable = new StringLookupTable();
                  store.setContents(_lookupTable, 51);
                  store.commit();
               }
            }
         }
      }

      return _lookupTable;
   }

   private int locateIndex(String string) {
      int low = 0;
      int high = this._sortedIndex.size();

      while (low != high) {
         int mid = low + high >> 1;
         int midVal = this._sortedIndex.elementAt(mid);
         String value = this.getString(midVal);
         int result = value.compareTo(string);
         if (result < 0) {
            low = mid + 1;
         } else {
            if (result <= 0) {
               return mid;
            }

            high = mid;
         }
      }

      return -(low + 1);
   }

   public synchronized int register(String string) {
      int index = this.locateIndex(string);
      if (index < 0) {
         int bundleCount = this._bundles.length;
         StringLookupTable$StringBundle bundle = this._bundles[bundleCount - 1];
         int handle = bundle.addString(string);
         if (bundle.isFull()) {
            ObjectGroup.createGroupIgnoreTooBig(bundle);
            Array.resize(this._bundles, bundleCount + 1);
            this._bundles[bundleCount] = new StringLookupTable$StringBundle();
         }

         index = -index - 1;
         handle += (bundleCount - 1) * 64;
         this._sortedIndex.insertElementAt(handle, index);
         PersistentObject.commit(this);
         return handle;
      } else {
         return this._sortedIndex.elementAt(index);
      }
   }

   public synchronized String getString(int handle) {
      int bundleIndex = handle / 64;
      int offset = handle % 64;
      return this._bundles[bundleIndex].getString(offset);
   }

   public synchronized boolean contains(String string) {
      return this.locateIndex(string) >= 0;
   }
}
