package net.rim.device.internal.system;

import java.util.EmptyStackException;
import net.rim.vm.Array;

final class LEDEventProcessor$EventStack {
   private long[] _sourceIds = new long[10];
   private long[] _expirationDates = new long[10];
   private int[] _groupInfos = new int[10];
   private int _count;

   public final void pushEvent(long sourceIdLong, boolean holsteredBoolean, int groupInfo) {
      if (this._count == this._sourceIds.length) {
         int newLength = this._sourceIds.length + 10;
         Array.resize(this._sourceIds, newLength);
         Array.resize(this._expirationDates, newLength);
         Array.resize(this._groupInfos, newLength);
      }

      this._sourceIds[this._count] = sourceIdLong;
      this._expirationDates[this._count] = System.currentTimeMillis() + 900000;
      this._groupInfos[this._count] = groupInfo;
      this._count++;
   }

   public final void expireEvents(long sourceIdLong, int groupInfo) {
      if (groupInfo != -1) {
         for (int index = 0; index < this._count; index++) {
            if (this._groupInfos[index] == groupInfo) {
               this._expirationDates[index] = 0;
            }
         }
      } else {
         for (int index = 0; index < this._count; index++) {
            if (this._sourceIds[index] == sourceIdLong) {
               this._expirationDates[index] = 0;
            }
         }
      }
   }

   public final boolean contains(long sourceIdLong) {
      for (int index = 0; index < this._count; index++) {
         if (this._sourceIds[index] == sourceIdLong) {
            return true;
         }
      }

      return false;
   }

   public final void popEvent() {
      this._count--;
   }

   public final void peekEvent(LEDEventProcessor$EventHolder anEventHolder) {
      if (this._count == 0) {
         throw new EmptyStackException();
      }

      int index = this._count - 1;
      anEventHolder.fill(this._sourceIds[index], this._expirationDates[index]);
   }

   public final boolean isEmpty() {
      return this._count == 0;
   }
}
