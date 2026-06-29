package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.util.DataBuffer;
import net.rim.vm.Persistable;

final class PartInfo implements Persistable, BackupProvider, RestoreProvider {
   byte _state = 0;
   int _startBlockIndex = -1;
   int _chunkHint = -1;
   short _error = 0;
   String _domID;
   String _name;
   String _archiveIndicator;
   int _messageId;
   int _attachmentIndex = -1;
   int _partId = -1;
   long _accessTimestamp;
   long _pendingTimestamp;

   static final void resetPart(PartInfo part) {
      part._state = 0;
      part._startBlockIndex = -1;
      part._error = 0;
   }

   final void lowMemoryManagement(int messageId, int attachmentIndex, int partId, long timestamp) {
      this._messageId = messageId;
      this._attachmentIndex = attachmentIndex;
      this._partId = partId;
      this._accessTimestamp = timestamp;
   }

   @Override
   public final void serializeData(DataBuffer buffer, int param) {
      boolean writeReseted = param == 0;
      ConverterUtilities.writeInt(buffer, 6, writeReseted ? 0 : this._state);
      ConverterUtilities.writeInt(buffer, 7, writeReseted ? -1 : this._startBlockIndex);
      ConverterUtilities.writeInt(buffer, 8, this._chunkHint);
      ConverterUtilities.writeInt(buffer, 9, writeReseted ? 0 : this._error);
      if (this._domID != null) {
         ConverterUtilities.writeStringSmart(buffer, 10, this._domID);
      }

      if (this._name != null) {
         ConverterUtilities.writeStringSmart(buffer, 11, this._name);
      }

      ConverterUtilities.writeLong(buffer, 12, this._pendingTimestamp);
   }

   @Override
   public final void retrieveData(DataBuffer buffer, int versionID) {
      if (ConverterUtilities.getType(buffer) != 6) {
         throw new IllegalArgumentException();
      }

      this._state = (byte)ConverterUtilities.readInt(buffer);
      if (ConverterUtilities.getType(buffer) != 7) {
         throw new IllegalArgumentException();
      }

      this._startBlockIndex = ConverterUtilities.readInt(buffer);
      if (ConverterUtilities.getType(buffer) != 8) {
         throw new IllegalArgumentException();
      }

      this._chunkHint = ConverterUtilities.readInt(buffer);
      if (ConverterUtilities.getType(buffer) != 9) {
         throw new IllegalArgumentException();
      }

      this._error = (short)ConverterUtilities.readInt(buffer);
      if (ConverterUtilities.getType(buffer, true) == 10) {
         this._domID = ConverterUtilities.readString(buffer);
      }

      if (ConverterUtilities.getType(buffer, true) == 11) {
         this._name = ConverterUtilities.readString(buffer);
      }

      if (ConverterUtilities.getType(buffer) != 12) {
         throw new IllegalArgumentException();
      }

      this._pendingTimestamp = ConverterUtilities.readLong(buffer);
   }
}
