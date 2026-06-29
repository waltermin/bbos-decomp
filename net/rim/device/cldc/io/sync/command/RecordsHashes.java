package net.rim.device.cldc.io.sync.command;

import java.util.Vector;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.api.SyncAgentRecordHashes;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class RecordsHashes extends SyncCommand {
   private Vector _records;
   private int _sizeInBytes;

   public RecordsHashes() {
      this.setTag(17);
   }

   public final void addRecordHashes(SyncAgentRecordHashes aSyncAgentRecordHashes) {
      if (this._records == null) {
         this._records = (Vector)(new Object());
      }

      this._records.addElement(aSyncAgentRecordHashes);
      this._sizeInBytes += 12;
      int xKeyFieldsHash = aSyncAgentRecordHashes.getKeyFieldsHash();
      int xAllFieldsHash = aSyncAgentRecordHashes.getAllFieldsHash();
      if (xKeyFieldsHash != 0 && xKeyFieldsHash != xAllFieldsHash) {
         this._sizeInBytes += 6;
      }
   }

   @Override
   public final boolean isValid() {
      return true;
   }

   @Override
   public final void writeParametersTo(DataBuffer outs) {
      if (this._records != null && !this._records.isEmpty()) {
         for (int xIndex = this._records.size() - 1; xIndex > -1; xIndex--) {
            SyncAgentRecordHashes xSyncAgentRecordHashes = (SyncAgentRecordHashes)this._records.elementAt(xIndex);
            int xUid = xSyncAgentRecordHashes.getUid();
            int xKeysHash = xSyncAgentRecordHashes.getKeyFieldsHash();
            int xAllFieldsHash = xSyncAgentRecordHashes.getAllFieldsHash();
            TypeLengthEncoding.writeInt(outs, 1, xUid);
            if (xKeysHash != 0 && xKeysHash != xAllFieldsHash) {
               TypeLengthEncoding.writeInt(outs, 3, xKeysHash);
            }

            TypeLengthEncoding.writeInt(outs, 4, xAllFieldsHash);
         }
      }
   }

   @Override
   public final void reset() {
      super.reset();
      this._sizeInBytes = 0;
      if (this._records != null) {
         this._records.setSize(0);
         this._records.trimToSize();
      }
   }

   public final void removeLastRecordHashes() {
      if (this._records != null && !this._records.isEmpty()) {
         int xIndex = this._records.size() - 1;
         SyncAgentRecordHashes xSyncAgentRecordHashes = (SyncAgentRecordHashes)this._records.elementAt(xIndex);
         this._sizeInBytes -= 12;
         if (xSyncAgentRecordHashes.getAllFieldsHash() != xSyncAgentRecordHashes.getKeyFieldsHash()) {
            this._sizeInBytes -= 6;
         }

         this._records.removeElementAt(xIndex);
      }
   }

   @Override
   public final int size() {
      return super.size() + this._sizeInBytes;
   }
}
