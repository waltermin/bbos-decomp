package net.rim.device.cldc.io.sync.command;

import net.rim.device.api.util.DataBuffer;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;
import net.rim.vm.Array;

public final class Use extends SyncCommand {
   private int _dataSourceId;
   private int _databaseId;
   private byte[] _databaseName;
   private int _databaseVersion;
   private int _tableId;
   private int _totalNumberOfOperations;
   private int _sizeInBytes;

   public Use() {
      this.setTag(7);
      this._databaseName = new byte[0];
   }

   @Override
   public final boolean isValid() {
      return this._databaseName.length != 0 ? true : this._databaseId != 0 || this._dataSourceId != 0;
   }

   public final void setDatabaseVersion(int value) {
      this._databaseVersion = value;
   }

   public final int getDatabaseVersion() {
      return this._databaseVersion;
   }

   public final boolean hasExplictDatabaseName() {
      return this._databaseId == 0 && this._databaseName.length != 0;
   }

   public final boolean isForDatabase() {
      return this._databaseId != 0 || this._databaseName.length != 0;
   }

   public final boolean isForDataSource() {
      return this._dataSourceId != 0;
   }

   public final void setDataSourceId(int aDataSourceId) {
      this._dataSourceId = aDataSourceId;
   }

   public final int getDataSourceId() {
      return this._dataSourceId;
   }

   public final void setDatabaseId(int aDatabaseId) {
      this._databaseId = aDatabaseId;
   }

   public final int getDatabaseId() {
      return this._databaseId;
   }

   public final void setDatabaseName(String name) {
      if (name != null) {
         this._databaseName = name.trim().getBytes();
      }
   }

   public final String getDatabaseName() {
      return (String)(new Object(this._databaseName));
   }

   public final int getTotalNumberOfOperations() {
      return this._totalNumberOfOperations;
   }

   private final void setTotalNumberOfOperations(int value) {
      this._totalNumberOfOperations = value;
   }

   public final void setTableId(int aTableId) {
      this._tableId = aTableId;
   }

   public final int getTableId() {
      return this._tableId;
   }

   @Override
   public final void readParametersFrom(DataBuffer din) {
      try {
         while (din.available() > 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            switch (xTag) {
               case 16:
                  this.setDataSourceId(TypeLengthEncoding.readInt(din));
                  break;
               case 32:
                  TypeLengthEncoding.readBytes(din, this._databaseName);
                  break;
               case 33:
                  this.setDatabaseId(TypeLengthEncoding.readInt(din));
                  break;
               case 34:
                  this.setTableId(TypeLengthEncoding.readInt(din));
                  break;
               case 35:
                  this.setTotalNumberOfOperations(TypeLengthEncoding.readInt(din));
                  break;
               case 36:
                  this.setDatabaseVersion(TypeLengthEncoding.readInt(din));
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
      if (this._dataSourceId != 0) {
         TypeLengthEncoding.writeInt(dout, 16, this._dataSourceId);
      }

      if (this._databaseId != 0) {
         TypeLengthEncoding.writeInt(dout, 33, this._databaseId);
      }

      if (this._databaseName.length != 0) {
         TypeLengthEncoding.writeBytes(dout, 32, this._databaseName);
      }

      if (this._tableId != 0) {
         TypeLengthEncoding.writeInt(dout, 34, this._tableId);
      }

      if (this._databaseVersion != 0) {
         TypeLengthEncoding.writeInt(dout, 36, this._databaseVersion);
      }
   }

   @Override
   public final int size() {
      if (this._dataSourceId != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._dataSourceId);
      }

      if (this._databaseId != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._databaseId);
      }

      if (this._databaseName.length != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + this._databaseName.length;
      }

      if (this._tableId != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._tableId);
      }

      if (this._databaseVersion != 0) {
         this._sizeInBytes = this._sizeInBytes + 2 + TypeLengthEncoding.getNumberOfBytesRequiredFor(this._databaseVersion);
      }

      return super.size() + this._sizeInBytes;
   }

   @Override
   public final void reset() {
      super.reset();
      this._dataSourceId = 0;
      this._databaseId = 0;
      this._tableId = 0;
      this._totalNumberOfOperations = 0;
      this._databaseVersion = 0;
      this._sizeInBytes = 0;
      Array.resize(this._databaseName, 0);
   }
}
