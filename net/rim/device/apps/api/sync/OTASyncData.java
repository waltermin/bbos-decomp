package net.rim.device.apps.api.sync;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Persistable;

public final class OTASyncData implements Persistable {
   private int _hostSequence;
   private int _deviceSequence;
   byte[] _hashData;
   boolean _dirty;
   int _ownerId = -1;

   public OTASyncData(int hostSequence, int deviceSequence) {
      this._hostSequence = hostSequence;
      this._deviceSequence = deviceSequence;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof OTASyncData)) {
         return false;
      }

      OTASyncData otherSyncData = (OTASyncData)other;
      return this._hostSequence == otherSyncData._hostSequence && this._deviceSequence == otherSyncData._deviceSequence;
   }

   public final int getHostSequence() {
      return this._hostSequence;
   }

   public final int getDeviceSequence() {
      return this._deviceSequence;
   }

   public final synchronized void incrementDeviceSequence() {
      this._deviceSequence++;
   }

   public final synchronized void updateChecksum(byte[] hashData, boolean checkDirty) {
      if (checkDirty) {
         this._dirty = !Arrays.equals(this._hashData, hashData);
      } else {
         this._dirty = false;
      }

      this._hashData = hashData;
   }

   public final synchronized void removeChecksum() {
      this._hashData = null;
   }

   public final synchronized byte[] getChecksum() {
      return this._hashData;
   }

   public final boolean isDirty() {
      return this._dirty;
   }

   public final void markClean() {
      this._dirty = false;
   }

   public final int getOwnerId() {
      return this._ownerId;
   }

   public final void setOwnerId(int ownerId) {
      this._ownerId = ownerId;
   }

   public final void reset() {
      this._deviceSequence = 0;
      this._hostSequence = 0;
      this._dirty = false;
      this._ownerId = -1;
      this._hashData = null;
   }
}
