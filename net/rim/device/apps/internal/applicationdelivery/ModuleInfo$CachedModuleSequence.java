package net.rim.device.apps.internal.applicationdelivery;

import net.rim.device.api.util.Persistable;

class ModuleInfo$CachedModuleSequence implements Persistable {
   private int _moduleOffset;
   private byte[] _sequenceData;
   private int _sequenceNumber;

   ModuleInfo$CachedModuleSequence(int sequenceNumber, int moduleOffset, byte[] sequenceData, int sequenceOffset, int sequenceLength) {
      this._moduleOffset = moduleOffset;
      this._sequenceData = new byte[sequenceLength];
      System.arraycopy(sequenceData, sequenceOffset, this._sequenceData, 0, sequenceLength);
      this._sequenceNumber = sequenceNumber;
   }

   public int getModuleOffset() {
      return this._moduleOffset;
   }

   public byte[] getSequenceData() {
      return this._sequenceData;
   }

   public int getSequenceNumber() {
      return this._sequenceNumber;
   }
}
