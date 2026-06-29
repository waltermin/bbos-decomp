package net.rim.device.cldc.io.sync;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.device.internal.synchronization.ota.util.LengthEncoding;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;
import net.rim.vm.Array;

public class RecordBaseSyncCommand extends SyncCommand {
   private int _recordUID;
   private int _index;
   private int _lastIndex;
   private byte[] _recordFields = new byte[0];
   private int[] _recordFieldsAttributes = new int[0];
   private int _bitflags;
   private int _sizeInBytes;
   private static final byte FORSLOWSYNC = 1;

   public RecordBaseSyncCommand() {
   }

   public RecordBaseSyncCommand(int aCommandId, int aCommand) {
      super(aCommandId, aCommand);
   }

   @Override
   public int size() {
      if (this._recordUID != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._recordUID);
      }

      if (this._index != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._index);
      }

      if (this._lastIndex != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._lastIndex);
      }

      if (this._recordFields.length != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + this._recordFields.length;
      }

      if (this._bitflags != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._bitflags);
      }

      return super.size() + this._sizeInBytes;
   }

   public boolean isForSlowSync() {
      return Helper.getFlagValue(this._bitflags, 1);
   }

   public void setForSlowSync(boolean value) {
      this._bitflags = (byte)Helper.setFlagValue(this._bitflags, value, 1);
   }

   @Override
   public boolean isValid() {
      return this._recordUID != 0 && this._index > -1 && this._index <= this._lastIndex;
   }

   public void setRecordUID(int recordUID) {
      this._recordUID = recordUID;
   }

   public int getRecordUID() {
      return this._recordUID;
   }

   public void setIndex(int index) {
      this._index = index;
   }

   public int getIndex() {
      return this._index;
   }

   public void setLastIndex(int lastIndex) {
      this._lastIndex = lastIndex;
   }

   public int getLastIndex() {
      return this._lastIndex;
   }

   public void setRecordFields(byte[] recordFields) {
      if (recordFields == null) {
         Array.resize(this._recordFields, 0);
      } else {
         this._recordFields = recordFields;
      }
   }

   public byte[] getRecordFields() {
      return this._recordFields;
   }

   public void addFieldAttribute(int xTag, int attributes) {
      if (this._recordFieldsAttributes.length <= xTag) {
         Array.resize(this._recordFieldsAttributes, xTag + 1);
      }

      this._recordFieldsAttributes[xTag] = attributes;
   }

   public int[] getFieldsAttributes() {
      return this._recordFieldsAttributes;
   }

   private void parseFieldsAttrbutes(DataBuffer dins) {
      try {
         byte[] xValue = TypeLengthEncoding.readBytes(dins);
         DataBuffer xFieldAttributes = new DataBuffer(xValue, 0, xValue.length, true);

         while (xFieldAttributes.available() > 0) {
            int xFieldTag = xFieldAttributes.readUnsignedByte();
            int xFieldAtrributes = TypeLengthEncoding.readInt(xFieldAttributes);
            this.addFieldAttribute(xFieldTag, xFieldAtrributes);
         }
      } finally {
         return;
      }
   }

   private void writeFieldsAttributes(DataBuffer dout) {
      dout.write(6);
      dout.writeInt(0);
      int xStartPosition = dout.getPosition();

      for (int xIndex = 0; xIndex < this._recordFieldsAttributes.length; xIndex++) {
         int xAttributes = this._recordFieldsAttributes[xIndex];
         if (xAttributes != 0) {
            TypeLengthEncoding.writeInt(dout, xIndex, xAttributes);
         }
      }

      int xEndPosition = dout.getPosition();
      int xLength = xEndPosition - xStartPosition;
      dout.setPosition(xStartPosition - 4);
      dout.writeInt(LengthEncoding.getFixedEncodingLengthFor(xLength));
      dout.setPosition(xEndPosition);
   }

   protected boolean readParameterValueFrom(int aTag, DataBuffer din) {
      switch (aTag) {
         case 1:
            this._recordUID = TypeLengthEncoding.readInt(din);
            return true;
         case 2:
            TypeLengthEncoding.readBytes(din, this._recordFields);
            return true;
         case 6:
            this.parseFieldsAttrbutes(din);
            return true;
         case 97:
            this._index = TypeLengthEncoding.readInt(din);
            return true;
         case 98:
            this._lastIndex = TypeLengthEncoding.readInt(din);
            return true;
         case 101:
            this._bitflags = TypeLengthEncoding.readInt(din);
            return true;
         default:
            return false;
      }
   }

   @Override
   public void writeParametersTo(DataBuffer dout) {
      if (this._recordUID != 0) {
         TypeLengthEncoding.writeInt(dout, 1, this._recordUID);
      }

      if (this._index != 0) {
         TypeLengthEncoding.writeInt(dout, 97, this._index);
      }

      if (this._lastIndex != 0) {
         TypeLengthEncoding.writeInt(dout, 98, this._lastIndex);
      }

      if (this._recordFields.length != 0) {
         TypeLengthEncoding.writeBytes(dout, 2, this._recordFields);
      }

      if (this._recordFieldsAttributes.length != 0) {
         this.writeFieldsAttributes(dout);
      }

      if (this._bitflags != 0) {
         TypeLengthEncoding.writeInt(dout, 101, this._bitflags);
      }
   }

   @Override
   public SyncCommand[] fragment(SyncCommandsPool aSyncCommandsPool, int fragmentSize) {
      SyncCommand[] xSyncCommands = null;
      int xFieldsSize = this._recordFields != null ? this._recordFields.length : 0;
      if (xFieldsSize == 0) {
         return new SyncCommand[]{this};
      }

      int xNumberOfFragments = xFieldsSize / fragmentSize;
      xNumberOfFragments += xFieldsSize % fragmentSize == 0 ? 0 : 1;
      xSyncCommands = new SyncCommand[xNumberOfFragments];

      for (int xIndex = 0; xIndex < xNumberOfFragments; xIndex++) {
         int xStartIndex = xIndex * fragmentSize;
         int xBufferSize = Math.min(fragmentSize, xFieldsSize - xStartIndex);
         RecordBaseSyncCommand xRecordBaseSyncCommand = (RecordBaseSyncCommand)aSyncCommandsPool.checkOut(this.getTag());
         xRecordBaseSyncCommand.setId(this.getId());
         xRecordBaseSyncCommand.setRecordUID(this.getRecordUID());
         xRecordBaseSyncCommand.setIndex(xIndex);
         xRecordBaseSyncCommand.setLastIndex(xNumberOfFragments - 1);
         byte[] xBuffer = new byte[xBufferSize];
         System.arraycopy(this._recordFields, xStartIndex, xBuffer, 0, xBufferSize);
         xRecordBaseSyncCommand.setRecordFields(xBuffer);
         xSyncCommands[xIndex] = xRecordBaseSyncCommand;
      }

      return xSyncCommands;
   }

   @Override
   public void reset() {
      super.reset();
      this._recordUID = 0;
      this._index = 0;
      this._lastIndex = 0;
      this._bitflags = 0;
      this._sizeInBytes = 0;
      Array.resize(this._recordFields, 0);
      Array.resize(this._recordFieldsAttributes, 0);
   }
}
