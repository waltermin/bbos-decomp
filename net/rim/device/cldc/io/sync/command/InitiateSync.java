package net.rim.device.cldc.io.sync.command;

import java.util.Vector;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.api.SyncAgentGroupOfRecords;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabase;
import net.rim.device.internal.synchronization.ota.util.LengthEncoding;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class InitiateSync extends SyncCommand {
   private Vector _hashes;
   private int _priority;
   private DataSourceDatabase _dataSourceDatabase;

   public InitiateSync() {
      this.setTag(11);
   }

   @Override
   public final boolean isValid() {
      return true;
   }

   public final void setPriority(int priority) {
      this._priority = priority;
   }

   public final void setDataSourceDatabase(DataSourceDatabase aDataSourceDatabase) {
      this._dataSourceDatabase = aDataSourceDatabase;
   }

   public final void addGroupHashValue(SyncAgentGroupOfRecords aSyncAgentGroupOfRecords) {
      if (this._hashes == null) {
         this._hashes = (Vector)(new Object(0));
      }

      this._hashes.addElement(aSyncAgentGroupOfRecords);
   }

   @Override
   public final void writeParametersTo(DataBuffer dout) {
      if (this._priority != 0) {
         TypeLengthEncoding.writeInt(dout, 37, this._priority);
      }

      if (this._dataSourceDatabase != null) {
         dout.write(38);
         dout.writeInt(0);
         int xStartPosition = dout.getPosition();
         this._dataSourceDatabase.writeTo(dout);
         int xEndPosition = dout.getPosition();
         int xLength = xEndPosition - xStartPosition;
         dout.setPosition(xStartPosition - 4);
         dout.writeInt(LengthEncoding.getFixedEncodingLengthFor(xLength));
         dout.setPosition(xEndPosition);
      }

      if (this._hashes != null) {
         int xIndex = this._hashes.size() - 1;
         if (xIndex > -1) {
            dout.writeByte(113);
            dout.writeInt(0);
            int xStartPosition = dout.getPosition();

            do {
               SyncAgentGroupOfRecords xSyncAgentGroupOfRecords = (SyncAgentGroupOfRecords)this._hashes.elementAt(xIndex);
               TypeLengthEncoding.writeInt(dout, xSyncAgentGroupOfRecords.getId(), xSyncAgentGroupOfRecords.hashCode());
            } while (--xIndex > -1);

            int xEndPosition = dout.getPosition();
            dout.setPosition(xStartPosition - 4);
            dout.writeInt(LengthEncoding.getFixedEncodingLengthFor(xEndPosition - xStartPosition));
            dout.setPosition(xEndPosition);
         }
      }
   }

   @Override
   public final void reset() {
      super.reset();
      this._priority = 0;
      if (this._hashes != null) {
         this._hashes.setSize(0);
      }

      if (this._dataSourceDatabase != null) {
         this._dataSourceDatabase = null;
      }
   }
}
