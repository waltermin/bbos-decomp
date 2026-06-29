package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class Status extends SyncCommand {
   private int _sessionErrorCode = 200;
   private IntIntHashtable _cmdIdToErrorCodeMap;
   private IntIntHashtable _datagramSequenceToErrorCodeMap;
   private IntIntHashtable _pendingOperationsMap;
   private boolean _invalidDatagramFlag;

   public Status() {
      this.setTag(8);
   }

   @Override
   public final boolean isValid() {
      return true;
   }

   public final void setSessionErrorCode(int anErrorCode) {
      this._sessionErrorCode = anErrorCode;
   }

   public final int getSessionErrorCode() {
      return this._sessionErrorCode;
   }

   public final void addCmdErrorCode(int cmdId, int anErrorCode) {
      if (this._cmdIdToErrorCodeMap == null) {
         this._cmdIdToErrorCodeMap = new IntIntHashtable();
      }

      this._cmdIdToErrorCodeMap.put(cmdId, anErrorCode);
   }

   public final void addDatagramErrorCode(int aDatagramSequence, int anErrorCode) {
      if (this._datagramSequenceToErrorCodeMap == null) {
         this._datagramSequenceToErrorCodeMap = new IntIntHashtable();
      }

      if (anErrorCode == 402 && !this._invalidDatagramFlag) {
         this._invalidDatagramFlag = true;
      }

      this._datagramSequenceToErrorCodeMap.put(aDatagramSequence, anErrorCode);
   }

   public final void addPendingOperations(int aCmdId, int ops) {
      if (this._pendingOperationsMap == null) {
         this._pendingOperationsMap = new IntIntHashtable();
      }

      this._pendingOperationsMap.put(aCmdId, ops);
   }

   public final IntIntHashtable getCommandsErrorCodes() {
      return this._cmdIdToErrorCodeMap;
   }

   public final boolean containsInvalidDagaramError() {
      return this._invalidDatagramFlag;
   }

   public final IntIntHashtable getDagaramsErrorCodes() {
      return this._datagramSequenceToErrorCodeMap;
   }

   public final IntIntHashtable getPendingNumberOfOperationsMap() {
      return this._pendingOperationsMap;
   }

   @Override
   public final void readParametersFrom(DataBuffer din) {
      try {
         while (din.available() > 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            switch (xTag) {
               case 48:
                  this.handleDatagramSequence(din, TypeLengthEncoding.readInt(din));
                  break;
               case 80:
                  this.handleCmdId(din, TypeLengthEncoding.readInt(din));
                  break;
               case 99:
                  this.setSessionErrorCode(TypeLengthEncoding.readInt(din));
                  break;
               default:
                  TypeLengthEncoding.skipValue(din);
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final void writeParametersTo(DataBuffer dout) {
      if (this._sessionErrorCode != 200) {
         TypeLengthEncoding.writeInt(dout, 99, this._sessionErrorCode);
      }

      if (this._cmdIdToErrorCodeMap != null && !this._cmdIdToErrorCodeMap.isEmpty()) {
         this.writeMappedError(dout, this._cmdIdToErrorCodeMap, 80);
      }

      if (this._datagramSequenceToErrorCodeMap != null && !this._datagramSequenceToErrorCodeMap.isEmpty()) {
         this.writeMappedError(dout, this._datagramSequenceToErrorCodeMap, 48);
      }
   }

   private final void handleDatagramSequence(DataBuffer din, int aKeyTag) {
      int xTag = TypeLengthEncoding.readTag(din);
      switch (xTag) {
         case 99:
            this.addDatagramErrorCode(aKeyTag, TypeLengthEncoding.readInt(din));
            return;
         default:
            TypeLengthEncoding.skipValue(din);
      }
   }

   private final void handleCmdId(DataBuffer din, int aKeyTag) {
      int xTag = TypeLengthEncoding.readTag(din);
      switch (xTag) {
         case 35:
            this.addPendingOperations(aKeyTag, TypeLengthEncoding.readInt(din));
            return;
         case 99:
            this.addCmdErrorCode(aKeyTag, TypeLengthEncoding.readInt(din));
            return;
         default:
            TypeLengthEncoding.skipValue(din);
      }
   }

   private final void writeMappedError(DataBuffer dout, IntIntHashtable aMapOfErrors, int aKeyTag) {
      IntEnumeration e = aMapOfErrors.keys();

      while (e.hasMoreElements()) {
         int xMapKey = e.nextElement();
         int xErrorValue = aMapOfErrors.get(xMapKey);
         TypeLengthEncoding.writeInt(dout, aKeyTag, xMapKey);
         TypeLengthEncoding.writeInt(dout, 99, xErrorValue);
      }
   }

   @Override
   public final void reset() {
      super.reset();
      this._invalidDatagramFlag = false;
      if (this._cmdIdToErrorCodeMap != null) {
         this._cmdIdToErrorCodeMap.clear();
      }

      if (this._datagramSequenceToErrorCodeMap != null) {
         this._datagramSequenceToErrorCodeMap.clear();
      }

      if (this._pendingOperationsMap != null) {
         this._pendingOperationsMap.clear();
      }

      this._sessionErrorCode = 200;
   }
}
