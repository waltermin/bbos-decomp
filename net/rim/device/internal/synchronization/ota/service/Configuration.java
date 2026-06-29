package net.rim.device.internal.synchronization.ota.service;

import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.device.internal.synchronization.ota.util.TypeLengthEncoding;
import net.rim.vm.Persistence;

public final class Configuration implements Persistable {
   private boolean _default;
   private byte[] _encodingCapabilites;
   private int _serverCapabilities;
   private int _deviceCapabilities = 31;
   private Hashtable _dataSourceNameToDataSourceMap;
   private IntHashtable _dataSourceIdToDataSourceMap;
   private Vector _dataSourceNames;
   private long _sessionTimeout;
   private long _batchTimeout;
   private long _ignoredSessionTimeout;
   private int _numberOfChangesPerChangeList;
   private int _maxTotalWeigthPerInitilizationSession;
   private short _numberOfOperationRetries;
   private long _operationRetryTimeout;
   private long _operationRetrySuspensionLength;
   public short _numberOfOperationRetrySuspensions;
   private boolean _userEnabled;
   private boolean _userPreferenceToSync = true;
   private int _numberOfRetries;
   private long _sid;
   private DataSource _defaultSyncDataSource;
   private DataSource _defaultNonSyncDataSource;
   private static final int MINUTE;
   private static final long DEFAULT_SESSION_TIMEOUT;
   private static final long DEFAULT_BATCH_TIMEOUT;
   private static final long DEFAULT_IGNORED_SESSION_TIMEOUT;
   private static final int DEFAULT_NUMBER_OF_RETRIES;
   private static final int DEFAULT_OPERATION_RETRY_TIMEOUT;
   private static final int DEFAULT_NUMBER_OF_OPERATION_RETRIES;
   private static final long DEFAULT_OPERATION_RETRY_SUSPENSION_LENGTH;
   private static final int DEFAULT_NUMBER_OF_OPERATION_RETRY_SUSPENSIONS;
   private static final byte CAPABILITIES_HANDLING_DEVICE_DATABASE_SCHEMA;
   private static final byte CAPABILITIES_HANDLING_EXTENDED_HEADERS;
   private static final byte CAPABILITIES_DELETE_ON_SLOW_SYNC;
   private static final byte CAPABILITIES_MULTIPLE_DATASOURCE_IN_INIT_SESSION;
   private static final byte CAPABILITIES_HANDLING_NONEXOR_HASH;
   private static final byte DEFAULT;
   private static final byte ENCODING_CAPABILITES;
   private static final byte PROTOCOLVERSION;
   private static final byte DATASOURCE;
   private static final byte SESSIONTIMEOUT;
   private static final byte BATCHTIMEOUT;
   private static final byte USERENABLED;
   private static final byte RETRIES;
   private static final byte IGNOREDSESSIONTIMEOUT;
   private static final byte NUMBER_OF_CHANGES_PER_CHANGELIST;
   private static final byte SERVER_CAPABILITIES;

   public static final Configuration create(byte[] bytes, long sid) {
      DataBuffer xDataBuffer = (DataBuffer)(new Object(bytes, 0, bytes.length, true));
      return create(xDataBuffer, sid);
   }

   public static final Configuration create(DataBuffer din, long sid) {
      Configuration xConfiguration = new Configuration(sid);
      xConfiguration.parse(din);
      return xConfiguration;
   }

   public Configuration(long sid) {
      this._sessionTimeout = 1200000;
      this._batchTimeout = 600000;
      this._ignoredSessionTimeout = 1800000;
      this._numberOfRetries = 0;
      this._sid = sid;
      this._dataSourceNameToDataSourceMap = (Hashtable)(new Object(3));
      this._dataSourceIdToDataSourceMap = (IntHashtable)(new Object(3));
      this._dataSourceNames = (Vector)(new Object(2));
      this._userEnabled = true;
      this._numberOfOperationRetries = 3;
      this._operationRetryTimeout = 300000;
      this._operationRetrySuspensionLength = 1800000;
      this._numberOfOperationRetrySuspensions = 2;
      this._numberOfChangesPerChangeList = 100;
      this._maxTotalWeigthPerInitilizationSession = 500;
   }

   public final long getSid() {
      return this._sid;
   }

   public final void addDataSource(DataSource aDataSource) {
      String xDataSourceName = aDataSource.getName();
      if (!this._dataSourceNameToDataSourceMap.containsKey(xDataSourceName)) {
         this._dataSourceNames.addElement(xDataSourceName);
         this._dataSourceNameToDataSourceMap.put(xDataSourceName, aDataSource);
         this._dataSourceIdToDataSourceMap.put(aDataSource.getId(), aDataSource);
         if (!aDataSource.couldHandleNonSync()) {
            if (this._defaultSyncDataSource == null) {
               this._defaultSyncDataSource = aDataSource;
            } else if (aDataSource.isDefault()) {
               this._defaultSyncDataSource = aDataSource;
            }
         } else if (this._defaultNonSyncDataSource == null || aDataSource.isDefault()) {
            this._defaultNonSyncDataSource = aDataSource;
         }
      }

      Persistence.commit(this, true);
   }

   public final void setUserPreferenceToSync(boolean value) {
      this._userPreferenceToSync = value;
      Persistence.commit(this, true);
   }

   public final boolean isUserPreferenceToSyncSet() {
      return this._userPreferenceToSync;
   }

   public final void setBatchTimeout(long value) {
      if (value < 0) {
         value = 600000;
      }

      this._batchTimeout = value;
      Persistence.commit(this, true);
   }

   public final void setSessionTimeout(long value) {
      if (value <= 0) {
         value = 1200000;
      }

      this._sessionTimeout = value;
      Persistence.commit(this, true);
   }

   public final void setDataSources(Hashtable dataSources) {
      this._dataSourceNameToDataSourceMap = dataSources;
      Persistence.commit(this, true);
   }

   public final long getBatchTimeout() {
      return this._batchTimeout;
   }

   public final long getSessionTimeout() {
      return this._sessionTimeout;
   }

   public final void setDeviceCapabilities(int deviceCapabilities) {
      this._deviceCapabilities = deviceCapabilities;
   }

   public final int getDeviceCapabilities() {
      return this._deviceCapabilities;
   }

   public final void setServerCapabilities(int serverCapabilities) {
      this._serverCapabilities = serverCapabilities;
   }

   public final int getServerCapabilities() {
      return this._serverCapabilities;
   }

   public final boolean shouldSendDatabaseSchema() {
      return Helper.getFlagValue(this._serverCapabilities, 1) && Helper.getFlagValue(this._deviceCapabilities, 1);
   }

   public final boolean extendedHeadersEnabled() {
      return Helper.getFlagValue(this._serverCapabilities, 2) && Helper.getFlagValue(this._deviceCapabilities, 2);
   }

   public final boolean isDeleteOnSlowSyncEnabled() {
      return Helper.getFlagValue(this._serverCapabilities, 4) && Helper.getFlagValue(this._deviceCapabilities, 4);
   }

   public final boolean isMultipleDatasourceInInitSessionEnabled() {
      return Helper.getFlagValue(this._serverCapabilities, 8) && Helper.getFlagValue(this._deviceCapabilities, 8);
   }

   public final boolean noneXorHashEnabled() {
      return Helper.getFlagValue(this._serverCapabilities, 16) && Helper.getFlagValue(this._deviceCapabilities, 16);
   }

   public final void setIgnoredSessionTimeout(long ignoredSessionTimeout) {
      this._ignoredSessionTimeout = ignoredSessionTimeout;
   }

   public final void setNumberOfChangesPerChangeList(int numberOfChangesPerChangeList) {
      this._numberOfChangesPerChangeList = numberOfChangesPerChangeList;
   }

   public final int getNumberOfChangesPerChangeList() {
      return this._numberOfChangesPerChangeList;
   }

   public final int getMaxTotalWeightPerInitializationSession() {
      return this._maxTotalWeigthPerInitilizationSession;
   }

   public final long getIgnoredSessionTimeout() {
      return this._ignoredSessionTimeout;
   }

   public final DataSource getDefaultSyncDataSource() {
      return this._defaultSyncDataSource;
   }

   public final DataSource getDefaultNonSyncDataSource() {
      return this._defaultNonSyncDataSource;
   }

   public final DataSource getDataSourceBy(int aDataSourceId) {
      return (DataSource)this._dataSourceIdToDataSourceMap.get(aDataSourceId);
   }

   public final DataSource getDataSourceBy(String aDataSourceName) {
      return (DataSource)this._dataSourceNameToDataSourceMap.get(aDataSourceName);
   }

   public final Hashtable getDataSources() {
      return this._dataSourceNameToDataSourceMap;
   }

   public final void setUserEnabled(boolean value) {
      this._userEnabled = value;
      Persistence.commit(this, true);
   }

   public final boolean isUserEnabled() {
      return this._userEnabled && !ITPolicy.getBoolean(33, 1, false);
   }

   public final void setNumberOfRetires(int aValue) {
      this._numberOfRetries = aValue;
      Persistence.commit(this, true);
   }

   public final int getNumberOfRetries() {
      return this._numberOfRetries;
   }

   public final int getNumberOfOperationRetries() {
      return this._numberOfOperationRetries;
   }

   public final long getOperationRetryTimeOut() {
      return this._operationRetryTimeout;
   }

   public final long getOperationRetrySuspensionLength() {
      return this._operationRetrySuspensionLength;
   }

   public final int getOperationRetrySuspensionLengthInMinutes() {
      return (int)this._operationRetrySuspensionLength / 60000;
   }

   public final short getNumberOfOperationRetrySuspensions() {
      return this._numberOfOperationRetrySuspensions;
   }

   public final Vector getDataSourceNames() {
      return this._dataSourceNames;
   }

   public final boolean dataSourceDatabaseContainsTables(String aDataSourceName, String aDatabaseName, int version) {
      DataSourceDatabase xDataSourceDatabase = this.getDataSourceDatabase(aDataSourceName, aDatabaseName, version);
      return xDataSourceDatabase != null ? xDataSourceDatabase.containsTables() : false;
   }

   public final boolean isDataSourceDatabaseTableDefined(String aDataSourceName, String aDatabaseName, int dbVersion, int aTableId) {
      DataSourceDatabase xDataSourceDatabase = this.getDataSourceDatabase(aDataSourceName, aDatabaseName, dbVersion);
      return xDataSourceDatabase != null ? xDataSourceDatabase.containsTable(aTableId) : false;
   }

   public final boolean isDataSourceDatabaseDefined(String aDataSourceName, String aDatabaseName) {
      DataSource xDataSource = this.getDataSourceBy(aDataSourceName);
      return xDataSource != null ? xDataSource.contains(aDatabaseName) : false;
   }

   public final boolean isDataSourceDatabaseDefined(String aDataSourceName, String aDatabaseName, int aDatabaseVersion) {
      return this.getDataSourceDatabase(aDataSourceName, aDatabaseName, aDatabaseVersion) != null;
   }

   public final void copyInto(Configuration aConfiguration) {
      aConfiguration._sessionTimeout = this._sessionTimeout;
      aConfiguration._batchTimeout = this._batchTimeout;
      aConfiguration._ignoredSessionTimeout = this._ignoredSessionTimeout;
      aConfiguration._numberOfChangesPerChangeList = this._numberOfChangesPerChangeList;
      aConfiguration._defaultSyncDataSource = this._defaultSyncDataSource;
      aConfiguration._defaultNonSyncDataSource = this._defaultNonSyncDataSource;
      aConfiguration._dataSourceNames = this._dataSourceNames;
      aConfiguration._userEnabled = this._userEnabled;
      aConfiguration._numberOfRetries = this._numberOfRetries;
      aConfiguration._dataSourceNameToDataSourceMap = this._dataSourceNameToDataSourceMap;
      aConfiguration._dataSourceIdToDataSourceMap = this._dataSourceIdToDataSourceMap;
      aConfiguration._serverCapabilities = this._serverCapabilities;
      aConfiguration._deviceCapabilities = this._deviceCapabilities;
      Persistence.commit(aConfiguration, true);
   }

   public final void parse(DataBuffer din) {
      try {
         while (din.available() != 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            switch (xTag) {
               case 1:
                  TypeLengthEncoding.skipValue(din);
                  break;
               case 2:
                  byte[] xValue = TypeLengthEncoding.readBytes(din);
                  if (xValue.length != 0) {
                     this.addDataSource(DataSource.create(xValue));
                  }
                  break;
               case 3:
               default:
                  this.setSessionTimeout(TypeLengthEncoding.readInt(din) * 60000);
                  break;
               case 4:
                  this.setBatchTimeout(TypeLengthEncoding.readInt(din) * 60000);
                  break;
               case 5:
                  this.setUserEnabled(TypeLengthEncoding.readBoolean(din));
                  break;
               case 6:
                  this.setNumberOfRetires(TypeLengthEncoding.readInt(din));
                  break;
               case 7:
                  this.setIgnoredSessionTimeout(TypeLengthEncoding.readInt(din) * 60000);
                  break;
               case 8:
                  this.setNumberOfChangesPerChangeList(TypeLengthEncoding.readInt(din));
                  break;
               case 9:
                  this.setServerCapabilities(TypeLengthEncoding.readInt(din));
            }
         }
      } finally {
         return;
      }
   }

   public final void parseServiceRecordInfo(DataBuffer din) {
      try {
         while (din.available() != 0) {
            int xTag = TypeLengthEncoding.readTag(din);
            switch (xTag) {
               case 1:
                  this.markAsDefault(TypeLengthEncoding.readBoolean(din));
                  break;
               case 96:
                  this._encodingCapabilites = TypeLengthEncoding.readBytes(din);
            }
         }
      } finally {
         return;
      }
   }

   public final void markAsDefault(boolean value) {
      this._default = value;
      Persistence.commit(this, true);
   }

   public final boolean isDefault() {
      return this._default;
   }

   private final void validate(String dataSourceName, String databaseName) {
      if (dataSourceName == null || databaseName == null) {
         throw new Object();
      }
   }

   public final DataSourceDatabase getDataSourceDatabase(String dataSourceName, String databaseName, int version) {
      this.validate(dataSourceName, databaseName);
      DataSource xDataSource = (DataSource)this.getDataSources().get(dataSourceName);
      return xDataSource != null ? xDataSource.getDataSourceDatabaseBy(databaseName, version) : null;
   }

   public final DataSourceDatabase getClosestDataSourceDatabase(String dataSourceName, String databaseName, int version) {
      this.validate(dataSourceName, databaseName);
      DataSourceDatabase xDataSourceDatabase = null;
      DataSource xDataSource = (DataSource)this.getDataSources().get(dataSourceName);
      if (xDataSource != null) {
         xDataSourceDatabase = xDataSource.getDataSourceDatabaseBy(databaseName, version);
         if (xDataSourceDatabase == null && xDataSource.couldHandleNonSync()) {
            xDataSourceDatabase = xDataSource.getDataSourceDatabaseBy("*", 0);
         }
      }

      return xDataSourceDatabase;
   }

   public final boolean isDataSourceDatabaseEnabled(String dataSourceName, String databaseName, int version) {
      this.validate(dataSourceName, databaseName);
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? xDataSourceDatabase.isServerEnabled() : false;
   }

   public final boolean isOTASlowSyncAllowedFor(String dataSourceName, String databaseName, int version) {
      this.validate(dataSourceName, databaseName);
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? xDataSourceDatabase.isOTASlowSyncAllowed() : false;
   }

   public final boolean isOneWaySyncToDeviceAllowed(String dataSourceName, String databaseName, int version) {
      this.validate(dataSourceName, databaseName);
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? (xDataSourceDatabase.getSyncType() & 1) == 1 : false;
   }

   public final boolean isOneWaySyncToServerAllowed(String dataSourceName, String databaseName, int version) {
      this.validate(dataSourceName, databaseName);
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? (xDataSourceDatabase.getSyncType() & 2) == 2 : false;
   }

   public final boolean isTwoWaySyncAllowed(String dataSourceName, String databaseName, int version) {
      this.validate(dataSourceName, databaseName);
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? (xDataSourceDatabase.getSyncType() & 2) == 2 && (xDataSourceDatabase.getSyncType() & 1) == 1 : false;
   }

   public final DataSourceDatabaseTable getDataSourceDatabaseTableFor(String dataSourceName, String databaseName, int version, int aTableId) {
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? xDataSourceDatabase.getTable(aTableId) : null;
   }

   public final DataSourceDatabaseFields getDataSourceDatabaseFieldsFor(String dataSourceName, String databaseName, int dbVersion, int aTableId) {
      DataSourceDatabaseTable xDataSourceDatabaseTable = this.getDataSourceDatabaseTableFor(dataSourceName, databaseName, dbVersion, aTableId);
      return xDataSourceDatabaseTable != null ? xDataSourceDatabaseTable.getSchema() : null;
   }

   public final int getSyncModeFor(String dataSourceName, String databaseName, int version) {
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? xDataSourceDatabase.getSyncMode() : 1;
   }

   public final int getNumberOfGroupsFor(String dataSourceName, String databaseName, int version) {
      DataSourceDatabase xDataSourceDatabase = this.getClosestDataSourceDatabase(dataSourceName, databaseName, version);
      return xDataSourceDatabase != null ? xDataSourceDatabase.getNumberOfGroups() : -1;
   }

   public final byte[] getEncodingCapabilities() {
      return this._encodingCapabilites;
   }

   @Override
   public final int hashCode() {
      return CRC32.update(-1, String.valueOf(this._sid).getBytes());
   }

   @Override
   public final String toString() {
      StringBuffer xSb = (StringBuffer)(new Object());
      xSb.append("[Configuration]").append('\n');
      xSb.append("SID= ").append(this._sid).append('\n');
      xSb.append("Default Service= ").append(this._default).append('\n');
      xSb.append("SessionTimeout= ").append(this._sessionTimeout).append('\n');
      xSb.append("BatchTimeout= ").append(this._batchTimeout).append('\n');
      xSb.append("IgnoredTimeout=").append(this._ignoredSessionTimeout).append('\n');
      xSb.append("UserEnabled= ").append(this._userEnabled).append('\n');
      xSb.append("NumberOfRetries= ").append(this._numberOfRetries).append('\n');
      xSb.append("Max Changes/List= ").append(this._numberOfChangesPerChangeList).append('\n');
      xSb.append("D/S CAP= ").append(this._deviceCapabilities).append('/').append(this._serverCapabilities).append('\n');
      if (this._defaultSyncDataSource != null) {
         xSb.append("Default Sync  DS=").append(this._defaultSyncDataSource.getName()).append('\n');
      }

      if (this._defaultNonSyncDataSource != null) {
         xSb.append("Default NSync DS=").append(this._defaultNonSyncDataSource.getName()).append('\n');
      }

      xSb.append("[DataSources List]").append('\n');
      int xDataNamesSize = this._dataSourceNames.size();

      for (int xIndex = 0; xIndex < xDataNamesSize; xIndex++) {
         xSb.append(this._dataSourceNameToDataSourceMap.get(this._dataSourceNames.elementAt(xIndex)));
      }

      return xSb.toString();
   }
}
