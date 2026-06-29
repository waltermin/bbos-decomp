package net.rim.device.apps.internal.calendar.eventdb;

import net.rim.device.api.collection.LoadableCollection;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.ReadableLongList;

class LongThresholdList implements ReadableList, LoadableCollection {
   private ReadableList _data;
   private ReadableLongList _keys;
   private long _upper;
   private boolean _includeUpper;
   private long _lower;
   private boolean _includeLower;
   private boolean _calculationCurrent;
   private int _c_offsetToFirst;
   private int _c_numItems;

   @Override
   public void loadFrom(Object collection) {
      this._calculationCurrent = false;
      this._keys = (ReadableLongList)collection;
      this._data = (ReadableList)collection;
   }

   public void setThresholds(long lowerThresh, boolean includeLower, long upperThresh, boolean includeUpper) {
      this._calculationCurrent = false;
      this._upper = upperThresh;
      this._includeUpper = includeUpper;
      this._lower = lowerThresh;
      this._includeLower = includeLower;
   }

   @Override
   public int size() {
      this.doCalc();
      return this._c_numItems;
   }

   @Override
   public int getAt(int offset, int length, Object[] values, int destIndex) {
      this.doCalc();
      if (length + offset > this._c_numItems) {
         length = this._c_numItems - offset;
      }

      return length <= 0 ? 0 : this._data.getAt(offset + this._c_offsetToFirst, length, values, destIndex);
   }

   @Override
   public Object getAt(int offset) {
      this.doCalc();
      return offset >= this._c_numItems ? null : this._data.getAt(offset + this._c_offsetToFirst);
   }

   @Override
   public int getIndex(Object element) {
      this.doCalc();
      int index = this._data.getIndex(element) - this._c_offsetToFirst;
      return index >= 0 && index < this._c_numItems ? index : -1;
   }

   private void doCalc() {
      if (!this._calculationCurrent) {
         int low = 0;
         int high = this._keys.size() - 1;

         while (low <= high) {
            int mid = (low + high) / 2;
            long keyInCollection = this._keys.getLongAt(mid);
            if (keyInCollection < this._lower) {
               low = mid + 1;
            } else if (keyInCollection > this._lower) {
               high = mid - 1;
            } else if (this._includeLower) {
               high = mid - 1;
            } else {
               low = mid + 1;
            }
         }

         this._c_offsetToFirst = low;
         low = 0;
         high = this._keys.size() - 1;

         while (low <= high) {
            int mid = (low + high) / 2;
            long keyInCollection = this._keys.getLongAt(mid);
            if (keyInCollection < this._upper) {
               low = mid + 1;
            } else if (keyInCollection > this._upper) {
               high = mid - 1;
            } else if (this._includeUpper) {
               low = mid + 1;
            } else {
               high = mid - 1;
            }
         }

         this._c_numItems = low - this._c_offsetToFirst;
         if (this._c_numItems < 0) {
            this._c_numItems = 0;
         }

         this._calculationCurrent = true;
      }
   }
}
