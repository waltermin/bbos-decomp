package net.rim.device.internal.synchronization.ota.service;

import java.util.Enumeration;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.device.internal.synchronization.ota.util.TLESerializableObject;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;

public final class DataSourceDatabase implements Persistable, TLESerializableObject {
   private int _version;
   private String _name;
   private int _id;
   private int _syncType;
   private int _syncMode;
   private byte _conflictResolution;
   private int _copyOfSyncType;
   private byte _copyOfConflictResolution;
   private byte _copyStatus;
   private int _syncFlags;
   private int _hashCode;
   private int _numberOfGroups;
   private int _recordTypeTag;
   private int _defaultTableId;
   private int _maxFieldTag;
   private IntHashtable _tables = (IntHashtable)(new Object(1));
   private int _flags;
   private static final byte DEVICE_ENABLED_FLAG;
   private static final byte SERVER_ENABLED_FLAG;
   private static final byte SEND_ON_INITIALIZATION_FLAG;
   private static final byte APPLY_DIFF_OPERATIONS_FLAG;
   private static final byte APPLY_MERGE_OPERATIONS_FLAG;
   private static final byte USE_EXPLICT_DATABASE_NAME_FLAG;
   private static final int INCLUDE_UNMAPPED_FIELDS_FLAG;
   private static final int APPLY_FIELD_LEVEL_CONFLCIT_FLAG;
   public static final String ANYDATABASE;
   public static final byte VERSION;
   public static final byte NAME;
   public static final byte ID;
   public static final byte SYNC_TYPE;
   public static final byte CONFLICT_RESOLUTION;
   public static final byte TABLE;
   public static final byte SYNC_FLAGS;
   public static final byte SYNC_MODE;
   public static final byte NUMBER_OF_GROUPS;
   public static final byte RECORD_TYPE;
   public static final byte DEVICE_ENABLED;
   public static final byte SERVER_ENABLED;
   public static final byte CONFLICT_RESOLUTION_SERVER_WINS;
   public static final byte CONFLICT_RESOLUTION_DEVICE_WINS;
   public static final int SYNC_FLAGS_ALLOW_OTA_SLOWSYNC;
   public static final int SYNC_FLAGS_DELETE_ON_FAILUR;
   public static final int SYNC_FLAGS_DELETE_ON_SLOWSYNC;
   public static final byte SYNC_TYPE_ONEWAY_TO_DEVICE;
   public static final byte SYNC_TYPE_ONEWAY_TO_SERVER;
   public static final byte SYNC_TYPE_TWOWAY;
   public static final byte SYNC_MODE_BATCHEDSYNC;
   public static final byte SYNC_MODE_PROGRESSIVESYNC;
   private static final byte DEFAULT_SYNC_MODE_VALUE;
   private static final byte DEFAULT_SYNC_TYPE_VALUE;
   private static final byte DEFAULT_SYNC_FLAGS_VALUE;
   private static final byte DEFAULT_CONFLICT_RESOLUTION_UNKNOWN;
   private static final short DEFAULT_SYNC_NUMBER_OF_GROUPS_VALUE;
   private static final byte COPY_SYNC_TYPE;
   private static final byte COPY_CONFLICT_RESOLUTION;

   public static final DataSourceDatabase create(byte[] bytes) {
      DataBuffer xDataBuffer = (DataBuffer)(new Object(bytes, 0, bytes.length, true));
      return create(xDataBuffer);
   }

   public static final DataSourceDatabase create(DataBuffer din) {
      return new DataSourceDatabase(din);
   }

   public DataSourceDatabase() {
      this._syncMode = 0;
      this._syncType = 3;
      this._syncFlags = 1;
      this._numberOfGroups = 253;
      this._conflictResolution = -1;
   }

   public DataSourceDatabase(DataBuffer din) {
      this();
      this.readFrom(din);
   }

   public DataSourceDatabase(DataSourceDatabase aDataSourceDatabase) {
      this.setVersion(aDataSourceDatabase.getVersion());
      this.setName(aDataSourceDatabase.getName());
      this.setId(aDataSourceDatabase.getId());
      this.setSyncType(aDataSourceDatabase.getSyncType());
      this.setSyncMode(aDataSourceDatabase.getSyncMode());
      this.setConflictResolution(aDataSourceDatabase.getConflictResolution());
      this.setSyncFlags(aDataSourceDatabase.getSyncFlags());
      this.setNumberOfGroups(aDataSourceDatabase.getNumberOfGroups());
      this.setRecordTypeTag(aDataSourceDatabase.getRecordTypeTag());
      this.setDefaultTableId(aDataSourceDatabase.getDefaultTableId());
      this.setMaxFieldTag(aDataSourceDatabase.getMaxFieldTag());
      this.setDeviceEnabled(aDataSourceDatabase.isDeviceEnabled());
      this.setServerEnabled(aDataSourceDatabase.isServerEnabled());
      this.sendWithInitialize(aDataSourceDatabase.sendWithInitialize());
   }

   public final boolean definesRecordTypeTag() {
      return this._recordTypeTag != 0;
   }

   public final DataSourceDatabase makeSpecificTo(String databaseName, int version, SyncCollectionSchema aSyncCollectionSchema) {
      DataSourceDatabase xDataSourceDatabase = new DataSourceDatabase(this);
      xDataSourceDatabase.setVersion(version);
      xDataSourceDatabase.setName(databaseName);
      if (aSyncCollectionSchema != null) {
         xDataSourceDatabase.setRecordTypeTag(aSyncCollectionSchema.getRecordTypeTag());
         int xDefaultTableId = aSyncCollectionSchema.getDefaultRecordType();
         xDataSourceDatabase.setDefaultTableId(xDefaultTableId);
         int[] xTables = aSyncCollectionSchema.getRecordTypes();

         for (int xTableIndex = 0; xTableIndex < xTables.length; xTableIndex++) {
            int xTableId = xTables[xTableIndex];
            int[] xKeysList = aSyncCollectionSchema.getKeyFieldIds(xTableId);
            if (xKeysList != null) {
               DataSourceDatabaseTable xDataSourceDatabaseTable = new DataSourceDatabaseTable();
               DataSourceDatabaseFields xDataSourceDatabaseFields = new DataSourceDatabaseFields();
               xDataSourceDatabaseTable.setId(xTableId);
               xDataSourceDatabaseTable.setDefault(xTableId == xDefaultTableId);

               for (int xIndex = 0; xIndex < xKeysList.length; xIndex++) {
                  xDataSourceDatabaseFields.addField(xKeysList[xIndex], true, 0, xKeysList[xIndex], null);
               }

               xDataSourceDatabaseFields.allFieldsMapped(true);
               xDataSourceDatabaseTable.setSchema(xDataSourceDatabaseFields);
               xDataSourceDatabase.addTable(xDataSourceDatabaseTable);
            }
         }
      }

      return xDataSourceDatabase;
   }

   public final boolean applyMergeOperations() {
      return Helper.getFlagValue(this._flags, 16);
   }

   public final void applyMergeOperations(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 16);
   }

   public final boolean applyDiffOperations() {
      return Helper.getFlagValue(this._flags, 8);
   }

   public final void applyDiffOperations(boolean apply) {
      this._flags = Helper.setFlagValue(this._flags, apply, 8);
   }

   public final void sendWithInitialize(boolean sendWithInitialize) {
      this._flags = Helper.setFlagValue(this._flags, sendWithInitialize, 4);
   }

   public final boolean sendWithInitialize() {
      return Helper.getFlagValue(this._flags, 4);
   }

   public final boolean isServerEnabled() {
      return Helper.getFlagValue(this._flags, 2);
   }

   public final boolean isDeviceEnabled() {
      return Helper.getFlagValue(this._flags, 1);
   }

   public final void setDeviceEnabled(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 1);
   }

   public final void setServerEnabled(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 2);
   }

   public final void useExplicitDatabaseName(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 32);
   }

   public final boolean useExplicitDatabaseName() {
      return Helper.getFlagValue(this._flags, 32);
   }

   public final void includeUnmappedFields(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 64);
   }

   public final boolean includeUnmappedFields() {
      return Helper.getFlagValue(this._flags, 64);
   }

   public final boolean applyFieldLevelConflicts() {
      return Helper.getFlagValue(this._flags, 128);
   }

   public final void applyFieldLevelConflicts(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 128);
   }

   public final boolean isGeneric() {
      return "*".equals(this._name);
   }

   public final int getDefaultTableId() {
      return this._defaultTableId;
   }

   public final void setDefaultTableId(int defaultTableId) {
      this._defaultTableId = defaultTableId;
   }

   public final void setVersion(int version) {
      this._version = version;
   }

   public final int getVersion() {
      return this._version;
   }

   public final void setName(String name) {
      if (name != null && name.length() != 0) {
         this._name = name;
         this._hashCode = CRC32.update(-1, name.getBytes());
      }
   }

   public final String getName() {
      return this._name;
   }

   public final void setSyncFlags(int syncFlags) {
      this._syncFlags = syncFlags;
   }

   public final boolean getDeleteOnSlowSync() {
      return Helper.getFlagValue(this._syncFlags, 128);
   }

   public final void setDeleteOnSlowSync(boolean value) {
      this._syncFlags = Helper.setFlagValue(this._syncFlags, value, 128);
   }

   public final int getSyncFlags() {
      return this._syncFlags;
   }

   public final void setSyncType(int syncType) {
      if (syncType < 0 && syncType > 3) {
         this._syncType = 3;
      } else {
         this._syncType = syncType;
      }
   }

   public final int getSyncType() {
      return this._syncType;
   }

   public final void setId(int id) {
      this._id = (short)id;
   }

   public final int getId() {
      return this._id;
   }

   public final void setSyncMode(int syncMode) {
      this._syncMode = syncMode;
   }

   public final int getSyncMode() {
      return this._syncMode;
   }

   public final void setMaxFieldTag(int maxFieldTag) {
      this._maxFieldTag = maxFieldTag;
   }

   public final int getMaxFieldTag() {
      return this._maxFieldTag;
   }

   public final void setNumberOfGroups(int numberOfGroups) {
      this._numberOfGroups = numberOfGroups;
   }

   public final int getNumberOfGroups() {
      return this._numberOfGroups;
   }

   public final void setConflictResolution(int conflictResolution) {
      this._conflictResolution = (byte)conflictResolution;
   }

   public final byte getConflictResolution() {
      return this._conflictResolution == -1 ? 0 : this._conflictResolution;
   }

   public final boolean isOneWaySyncToDeviceAllowed() {
      return (this._syncType & 1) == 1;
   }

   public final boolean isOneWaySyncToServerAllowed() {
      return (this._syncType & 2) == 2;
   }

   public final int getNumberOfTables() {
      return this._tables.size();
   }

   public final boolean containsTables() {
      return this._tables != null && !this._tables.isEmpty();
   }

   public final boolean containsTable(int aTableId) {
      return this._tables != null ? (DataSourceDatabaseTable)this._tables.get(aTableId) != null : false;
   }

   public final IntHashtable getTables() {
      return this._tables;
   }

   public final void addTable(DataSourceDatabaseTable aTable) {
      int xTableId = aTable.getId();
      this.setMaxFieldTag(Math.max(this._maxFieldTag, aTable.getSchema().getMaxFieldTag()));
      if (this._defaultTableId == 0 && aTable.isDefault()) {
         this._defaultTableId = xTableId;
      }

      this.applyDiffOperations(true);
      this.applyMergeOperations(true);
      this.applyFieldLevelConflicts(true);
      this._tables.put(xTableId, aTable);
   }

   public final DataSourceDatabaseTable getTable(int aTableId) {
      return this._tables != null ? (DataSourceDatabaseTable)this._tables.get(aTableId) : null;
   }

   public final boolean isRecordTypeTag(int aTag) {
      return aTag == this._recordTypeTag;
   }

   public final int getRecordTypeTag() {
      return this._recordTypeTag;
   }

   public final void setRecordTypeTag(int recordTypeTag) {
      this._recordTypeTag = recordTypeTag;
   }

   public final boolean isDeleteOnFailuerOn() {
      return (this._syncFlags & 64) == 64;
   }

   public final boolean isOTASlowSyncAllowed() {
      return (this._syncFlags & 1) == 1;
   }

   public final void restoreConflictSettings() {
      if ((this._copyStatus & 2) > 0) {
         this._conflictResolution = this._copyOfConflictResolution;
         this._copyStatus = (byte)(this._copyStatus & -3);
      }
   }

   public final void restoreSyncTypeSettings() {
      if ((this._copyStatus & 1) > 0) {
         this._syncType = this._copyOfSyncType;
         this._copyStatus = (byte)(this._copyStatus & -2);
      }
   }

   public final void makeCopyOfConflictSettings() {
      if ((this._copyStatus & 2) == 0) {
         this._copyOfConflictResolution = this._conflictResolution;
         this._copyStatus = (byte)(this._copyStatus | 2);
      }
   }

   public final void makeCopyOfSyncTypeSettings() {
      if ((this._copyStatus & 1) == 0) {
         this._copyOfSyncType = this._syncType;
         this._copyStatus = (byte)(this._copyStatus | 1);
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      } else {
         return !(anObject instanceof DataSourceDatabase) ? false : this.hashCode() == anObject.hashCode();
      }
   }

   @Override
   public final void readFrom(DataBuffer din) {
      try {
         while (din.available() != 0) {
            int xTag = din.readUnsignedByte();
            switch (xTag) {
               case 0:
                  TypeLengthEncoding.skipValue(din);
                  break;
               case 1:
               default:
                  this.setVersion(TypeLengthEncoding.readInt(din));
                  break;
               case 2:
                  this.setName(TypeLengthEncoding.readString(din));
                  break;
               case 3:
                  this.setId(this._id = TypeLengthEncoding.readUnsignedShort(din));
                  break;
               case 4:
                  this.setSyncType(TypeLengthEncoding.readInt(din));
                  break;
               case 5:
                  this.setConflictResolution(TypeLengthEncoding.readInt(din));
                  break;
               case 6:
                  byte[] xTableBytes = TypeLengthEncoding.readBytes(din);
                  DataBuffer xDataBuffer = (DataBuffer)(new Object(xTableBytes, 0, xTableBytes.length, true));
                  DataSourceDatabaseTable xNewTable = DataSourceDatabaseTable.create(xDataBuffer);
                  int xTableId = xNewTable.getId();
                  DataSourceDatabaseTable xTable = (DataSourceDatabaseTable)this._tables.get(xTableId);
                  if (xTable != null) {
                     xDataBuffer.rewind();
                     xTable.readFrom(xDataBuffer);
                  } else {
                     xTable = xNewTable;
                  }

                  this.addTable(xTable);
                  break;
               case 7:
                  this.setSyncFlags(TypeLengthEncoding.readInt(din));
                  break;
               case 8:
                  this.setSyncMode(TypeLengthEncoding.readInt(din));
                  break;
               case 9:
                  this.setNumberOfGroups(TypeLengthEncoding.readInt(din));
                  break;
               case 10:
                  this.setRecordTypeTag(TypeLengthEncoding.readInt(din));
                  break;
               case 11:
                  this.setDeviceEnabled(TypeLengthEncoding.readBoolean(din));
                  break;
               case 12:
                  this.setServerEnabled(TypeLengthEncoding.readBoolean(din));
            }
         }
      } finally {
         return;
      }
   }

   @Override
   public final void writeTo(DataBuffer dout) {
      TypeLengthEncoding.writeInt(dout, 10, this._recordTypeTag);
      if (this._conflictResolution != -1) {
         TypeLengthEncoding.writeInt(dout, 5, this._conflictResolution);
      }

      if (this._syncType != 3) {
         TypeLengthEncoding.writeInt(dout, 4, this._syncType);
      }

      if (this._syncFlags != 0) {
         TypeLengthEncoding.writeInt(dout, 7, this._syncFlags);
      }

      if (this._tables != null) {
         Enumeration xTablesList = this._tables.elements();

         while (xTablesList.hasMoreElements()) {
            DataSourceDatabaseTable xTable = (DataSourceDatabaseTable)xTablesList.nextElement();
            if (xTable != null) {
               TypeLengthEncoding.writeTLESerializableObject(dout, 6, xTable);
            }
         }
      }
   }

   @Override
   public final String toString() {
      StringBuffer xSb = (StringBuffer)(new Object());
      xSb.append('[').append(this._name).append(']').append('\n');
      xSb.append("Ver= ").append(this._version).append('\n');
      xSb.append("ID= ").append(this._id).append('\n');
      xSb.append("SE= ").append(this.isServerEnabled()).append('\n');
      xSb.append("DE= ").append(this.isDeviceEnabled()).append('\n');
      xSb.append("SyncType= ").append(this._syncType).append('\n');
      xSb.append("SyncMode= ").append(this._syncMode).append('\n');
      xSb.append("Conflict= ").append(this._conflictResolution).append('\n');
      xSb.append("SyncFlags= ").append(this._syncFlags).append('\n');
      xSb.append("NumGroups= ").append(this._numberOfGroups).append('\n');
      xSb.append("RTT= ").append(this._recordTypeTag).append('\n');
      if (this._tables != null) {
         Enumeration xTablesList = this._tables.elements();

         while (xTablesList.hasMoreElements()) {
            xSb.append(xTablesList.nextElement());
         }
      }

      return xSb.toString();
   }
}
