package net.rim.device.apps.internal.calendar.eventdb;

import java.util.TimeZone;
import java.util.Vector;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.vm.Array;

final class CalDBImpl$SetOfEvents {
   private long _time;
   private int _maxBefore;
   private int _maxOnOrAfter;
   private int _OnOrAfterExpands;
   private int _beforeExpands;
   private Object[] _onOrAfter = new Object[0];
   private long[] _onOrAfterTime = new long[0];
   private long[] _onOrAfterLUID = new long[0];
   int _numOnOrAfter;
   private Object[] _before = new Object[0];
   private long[] _beforeTime = new long[0];
   private long[] _beforeLUID = new long[0];
   int _numBefore;
   private int _status = 0;

   public final void clear() {
      Array.resize(this._onOrAfter, 0);
      Array.resize(this._onOrAfterTime, 0);
      Array.resize(this._onOrAfterLUID, 0);
      this._numOnOrAfter = 0;
      Array.resize(this._before, 0);
      Array.resize(this._beforeTime, 0);
      Array.resize(this._beforeLUID, 0);
      this._numBefore = 0;
      this._status = 0;
   }

   public final void setParameters(long time, int maxBefore, int maxOnOrAfter) {
      this._time = time;
      this._maxBefore = maxBefore;
      this._maxOnOrAfter = maxOnOrAfter;
      this.ResizeAfterLists(maxOnOrAfter);
      this.ResizeBeforeLists(maxBefore);
      this._numOnOrAfter = 0;
      this._numBefore = 0;
      this._OnOrAfterExpands = 0;
      this._beforeExpands = 0;
   }

   public final void ResizeAfterLists(int maxOnOrAfter) {
      this._maxOnOrAfter = maxOnOrAfter;
      Array.resize(this._onOrAfter, maxOnOrAfter);
      Array.resize(this._onOrAfterTime, maxOnOrAfter);
      Array.resize(this._onOrAfterLUID, maxOnOrAfter);
      if (this._numOnOrAfter > this._maxOnOrAfter) {
         this._numOnOrAfter = this._maxOnOrAfter;
      }
   }

   public final void ResizeBeforeLists(int maxBefore) {
      this._maxBefore = maxBefore;
      Array.resize(this._before, maxBefore);
      Array.resize(this._beforeTime, maxBefore);
      Array.resize(this._beforeLUID, maxBefore);
      if (this._numBefore > this._maxBefore) {
         this._numBefore = this._maxBefore;
      }
   }

   public final int getEvents(Vector eventVector) {
      Object[] objects = this._before;

      for (int i = this._numBefore - 1; i >= 0; i--) {
         eventVector.addElement(objects[i]);
      }

      objects = this._onOrAfter;
      int max = this._numOnOrAfter;

      for (int i = 0; i < max; i++) {
         eventVector.addElement(objects[i]);
      }

      return this._status;
   }

   public final boolean insert(Object o, long start, long luid) {
      long time = this._time;
      if (start >= time) {
         if (this._numOnOrAfter < this._maxOnOrAfter) {
            this.insertOnOrAfter(o, start, luid);
            return true;
         }

         if (this._numOnOrAfter > 0) {
            if (start < this._onOrAfterTime[this._numOnOrAfter - 1]) {
               this.insertOnOrAfter(o, start, luid);
               return true;
            }

            if (start == this._onOrAfterTime[this._numOnOrAfter - 1] && luid < this._onOrAfterLUID[this._numOnOrAfter - 1]) {
               this.insertOnOrAfter(o, start, luid);
               return true;
            }
         }

         this._status |= 1;
         return false;
      } else {
         if (this._numBefore < this._maxBefore) {
            this.insertBefore(o, start, luid);
            return true;
         }

         if (this._numBefore > 0) {
            if (start > this._beforeTime[this._numBefore - 1]) {
               this.insertBefore(o, start, luid);
               return true;
            }

            if (start == this._beforeTime[this._numBefore - 1] && luid > this._beforeLUID[this._numBefore - 1]) {
               this.insertBefore(o, start, luid);
               return true;
            }
         }

         this._status |= 2;
         return false;
      }
   }

   public final boolean containsLUID(long luid) {
      for (int i = 0; i < this._onOrAfterLUID.length; i++) {
         if (this._onOrAfterLUID[i] == luid) {
            return true;
         }
      }

      for (int i = 0; i < this._beforeLUID.length; i++) {
         if (this._beforeLUID[i] == luid) {
            return true;
         }
      }

      return false;
   }

   private final void insertOnOrAfter(Object o, long time, long luid) {
      Object[] objects = this._onOrAfter;
      long[] times = this._onOrAfterTime;
      long[] luids = this._onOrAfterLUID;
      int num = this._numOnOrAfter;
      int max = this._maxOnOrAfter;
      int i = 0;

      while (i < num && time >= times[i] && (time != times[i] || luid >= luids[i])) {
         i++;
      }

      if (i >= max) {
         this._status |= 1;
      } else {
         if (++num > max) {
            num = max;
            this._OnOrAfterExpands++;
            boolean lastAreSameDate = true;
            long lastTime = -1;
            int numberOnSameDate = 0;

            for (int j = times.length - 1; j >= 0 && lastAreSameDate; j--) {
               if (lastTime == -1) {
                  lastTime = times[j];
                  numberOnSameDate++;
               } else {
                  lastAreSameDate &= DateTimeUtilities.isSameDate(lastTime, times[j]);
                  if (lastAreSameDate) {
                     numberOnSameDate++;
                  }
               }
            }

            if (numberOnSameDate <= this._OnOrAfterExpands) {
               this.ResizeAfterLists(this._maxOnOrAfter - numberOnSameDate + 1);
               this._OnOrAfterExpands = 0;
               num = this._maxOnOrAfter;
               this._status |= 1;
            } else {
               this.ResizeAfterLists(this._maxOnOrAfter + 1);
               num++;
            }
         }

         if (i >= num) {
            i = num - 1;
         }

         System.arraycopy(objects, i, objects, i + 1, num - i - 1);
         System.arraycopy(times, i, times, i + 1, num - i - 1);
         System.arraycopy(luids, i, luids, i + 1, num - i - 1);
         objects[i] = o;
         times[i] = time;
         luids[i] = luid;
         this._numOnOrAfter = num;
      }
   }

   private final void insertBefore(Object o, long time, long luid) {
      Object[] objects = this._before;
      long[] times = this._beforeTime;
      long[] luids = this._beforeLUID;
      int num = this._numBefore;
      int max = this._maxBefore;
      int i = 0;

      while (i < num && time <= times[i] && (time != times[i] || luid <= luids[i])) {
         i++;
      }

      if (i >= max) {
         this._status |= 2;
      } else {
         if (++num > max) {
            num = max;
            this._beforeExpands++;
            boolean lastAreSameDate = true;
            long lastTime = -1;
            int numberOnSameDate = 0;

            for (int j = times.length - 1; j >= 0 && lastAreSameDate; j--) {
               if (lastTime == -1) {
                  lastTime = times[j];
                  numberOnSameDate++;
               } else {
                  lastAreSameDate &= DateTimeUtilities.isSameDate(lastTime, times[j]);
                  if (lastAreSameDate) {
                     numberOnSameDate++;
                  }
               }
            }

            if (numberOnSameDate <= this._beforeExpands) {
               this.ResizeBeforeLists(this._maxBefore - numberOnSameDate + 1);
               this._beforeExpands = 0;
               this._status |= 2;
               num = this._maxBefore;
            } else {
               this.ResizeBeforeLists(this._maxBefore + 1);
               num++;
            }
         }

         if (i >= num) {
            i = num - 1;
         }

         System.arraycopy(objects, i, objects, i + 1, num - i - 1);
         System.arraycopy(times, i, times, i + 1, num - i - 1);
         System.arraycopy(luids, i, luids, i + 1, num - i - 1);
         objects[i] = o;
         times[i] = time;
         luids[i] = luid;
         this._numBefore = num;
      }
   }

   public final boolean isFilled() {
      return this._numBefore == this._maxBefore && this._numOnOrAfter == this._maxOnOrAfter;
   }

   public final long getEarliestTime(TimeZone tz) {
      if (this._numBefore > 0) {
         return this._beforeTime[this._numBefore - 1];
      } else {
         return this._numOnOrAfter > 0 ? this._onOrAfterTime[0] : -1;
      }
   }

   public final long getLatestTime(TimeZone tz) {
      if (this._numOnOrAfter > 0) {
         return this._onOrAfterTime[this._numOnOrAfter - 1];
      } else {
         return this._numBefore > 0 ? this._beforeTime[0] : -1;
      }
   }
}
