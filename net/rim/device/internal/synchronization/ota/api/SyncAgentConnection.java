package net.rim.device.internal.synchronization.ota.api;

import java.io.IOException;
import java.util.Vector;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.DataSource;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabase;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.session.SessionManager;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;
import net.rim.device.internal.synchronization.ota.util.TLEFieldsList;
import net.rim.vm.WeakReference;

public final class SyncAgentConnection implements OTASyncPriorityProvider {
   private WeakReference _wRef_SyncAgentConnectionListener;
   private SyncAgentConnectionState _syncAgentConnectionState;
   private SyncAgentUrl _syncAgentUrl;
   private SessionManager _sessionManager;
   private Configuration _configuration;
   private SyncAgentGroupOfRecords[] _groups;
   private SyncAgentStatistics _syncAgentStatistics;
   private byte _priority;
   private byte _dependencyLevel;
   private boolean _closed;
   private DataSourceDatabase _dataSourceDatabase;
   private int _retryCount = 0;
   private static final IOException ClosedConnectionIOException = new IOException("Closed");
   private static final int MAX_RETRY = 10;

   public final Configuration getConfiguration() {
      return this._configuration;
   }

   public final DataSourceDatabase getDataSourceDatabase() {
      return this._dataSourceDatabase;
   }

   public final int getDatabaseVersion() {
      return this._syncAgentUrl.getVersion();
   }

   public final int getNumberOfRecords() {
      return this.notifyListener(63, null);
   }

   public final SyncAgentStatistics getSyncAgentStatistics() {
      return this._syncAgentStatistics;
   }

   public final short getContextualRetryCount() {
      return this._syncAgentConnectionState.getContextualRetryCount();
   }

   public final int getSyncDependencyLevel() {
      return this._dependencyLevel;
   }

   public final int getPacketLevelPriority() {
      return this._priority * (this._dependencyLevel + 128);
   }

   public final boolean isInitialized() {
      return this._syncAgentConnectionState.isInitialized();
   }

   public final boolean isTherePendingOperationFor(int operation, int uid, Vector changesList) {
      synchronized (this._syncAgentConnectionState) {
         if (!this.isClosed() && this._syncAgentConnectionState.isInitialized() && !this._syncAgentConnectionState.isSuspended()) {
            for (int xIndex = changesList.size() - 1; xIndex >= 0; xIndex--) {
               SyncApplicationChangeList xSyncApplicationChangeList = (SyncApplicationChangeList)changesList.elementAt(xIndex);
               if (xSyncApplicationChangeList.containsOperation(uid, operation)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public final boolean isTherePendingChanges() {
      synchronized (this._syncAgentConnectionState) {
         if (!this.isClosed() && this._syncAgentConnectionState.isInitialized() && !this._syncAgentConnectionState.isSuspended()) {
            Vector xChangesList = this._syncAgentConnectionState.getChangesList();

            while (!xChangesList.isEmpty()) {
               SyncApplicationChangeList xSyncApplicationChangeList = (SyncApplicationChangeList)xChangesList.firstElement();
               if (!xSyncApplicationChangeList.isEmpty()) {
                  return true;
               }

               xChangesList.removeElementAt(0);
            }
         }

         return false;
      }
   }

   public final boolean isDeleteOnSlowSyncEnabled() {
      if (!this._configuration.isDeleteOnSlowSyncEnabled()) {
         return false;
      }

      String defaultNonSyncDataSourceName = this._configuration.getDefaultNonSyncDataSource().getName();
      String thisDataSourceName = this._syncAgentUrl.getDataSourceName();
      return thisDataSourceName.equals(defaultNonSyncDataSourceName) ? true : this._dataSourceDatabase.getDeleteOnSlowSync();
   }

   public final void setSyncAgentConnectionListener(SyncAgentConnectionListener aSyncAgentConnectionListener) {
      synchronized (SyncAgentConnections.getLock()) {
         synchronized (this._syncAgentConnectionState) {
            if (aSyncAgentConnectionListener != null) {
               this._wRef_SyncAgentConnectionListener = new WeakReference(aSyncAgentConnectionListener);
               if (!SyncAgentConnections.isConnectionRegistered(this._syncAgentUrl)) {
                  SyncAgentConnections.registerConnection(this);
               }
            } else {
               this._wRef_SyncAgentConnectionListener = null;
            }
         }
      }
   }

   public final SyncAgentConnectionListener getSyncAgentConnectionListener() {
      return (SyncAgentConnectionListener)(this._wRef_SyncAgentConnectionListener != null ? this._wRef_SyncAgentConnectionListener.get() : null);
   }

   public final SyncAgentUrl getUrl() {
      return this._syncAgentUrl;
   }

   public final int getMaxNumberOfChangesPerChangeList() {
      return this._configuration.getNumberOfChangesPerChangeList();
   }

   public final void submit(SyncApplicationChangeList aSyncApplicationChangeList, boolean forceSync) {
      synchronized (this._syncAgentConnectionState) {
         Vector xChangesList = this._syncAgentConnectionState.getChangesList();
         xChangesList.addElement(aSyncApplicationChangeList);
         this._sessionManager.triggerSync(forceSync || this._dataSourceDatabase.getSyncMode() == 1);
      }
   }

   public final void submit(SyncApplicationChange aSyncApplicationChange, boolean forceSync, boolean optimizeChanges) {
      boolean xReturnStatus = false;
      int xStatus = 0;
      synchronized (this._syncAgentConnectionState) {
         if (this.isClosed()) {
            throw ClosedConnectionIOException;
         }

         if (!this._dataSourceDatabase.isOneWaySyncToServerAllowed()) {
            xStatus = 200;
            xReturnStatus = true;
         } else {
            SyncApplicationChangeList xChangeList = null;
            Vector xChangesList = this._syncAgentConnectionState.getChangesList();
            if (!xChangesList.isEmpty()) {
               xChangeList = (SyncApplicationChangeList)xChangesList.lastElement();
               if (xChangeList.isLocked() || !optimizeChanges) {
                  xChangeList = null;
               }
            }

            if (xChangeList == null || xChangeList.size() == this._configuration.getNumberOfChangesPerChangeList()) {
               xChangeList = this.createChangeList();
               xChangesList.addElement(xChangeList);
            }

            xChangeList.add(aSyncApplicationChange);
            this._sessionManager.triggerSync(forceSync || this._dataSourceDatabase.getSyncMode() == 1);
         }
      }

      if (xReturnStatus) {
         SyncApplicationChangeStatus xSyncApplicationChangeStatus = new SyncApplicationChangeStatus();
         xSyncApplicationChangeStatus.setRefId(aSyncApplicationChange.getRefId());
         xSyncApplicationChangeStatus.setStatus(xStatus);
         this.onSessionEvent(8, xSyncApplicationChangeStatus);
      }
   }

   public final void flush() {
      this._sessionManager.triggerSync(true);
   }

   public final void reset() {
      this.reset(true);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final int onSessionEvent(int eventId, Object anObject) {
      try {
         switch (eventId) {
            case 3:
            case 7:
               this._syncAgentConnectionState.resetChangeLists();
               break;
            case 49:
            case 51:
               if (this.isClosed()) {
                  String errorMessage = "Start transaction failed: Database connection is closed.";
                  Logger.logOperationError(eventId, 0, errorMessage);
                  return 411;
               }
               break;
            case 50:
            case 52:
               if (this.isClosed()) {
                  return 200;
               }
            case 53:
            case 54:
               break;
            case 55:
               synchronized (this._syncAgentConnectionState) {
                  this._syncAgentConnectionState.setInitialized(true);
                  this._syncAgentConnectionState.setSuspended(false);
                  this._dataSourceDatabase.setDeviceEnabled(true);
               }

               return this.notifyListener(eventId, null);
            case 56:
               this.reset(true);
               return 200;
            case 57:
               this.reset(false);
               return 200;
            case 59:
               this._syncAgentConnectionState.setSuspended(false);
               Logger.logSuspendResumeConnection(false, this._syncAgentUrl.getDatabaseName(), false);
               return 200;
            case 64:
               Object xTicket = PersistentContent.getTicket();
               if (xTicket != null) {
                  this._syncAgentConnectionState.encryptContent(true);
               }

               return 200;
            case 65:
               if (anObject instanceof SyncApplicationChangeStatus) {
                  SyncApplicationChangeStatus xChangeStatus = (SyncApplicationChangeStatus)anObject;
                  SyncApplicationChange xChange = this._syncAgentConnectionState.getChangeBy(xChangeStatus.getRefId());
                  if (xChange == null) {
                     String errorMessage = "Unexpected error: SyncAgentConnectionState.getChangesBy() returned a NULL SyncApplicationChange.";
                     Logger.logOperationError(65, 0, errorMessage);
                     return 411;
                  }

                  if (!xChangeStatus.isContextual()) {
                     xChange.resetRetryCount((byte)0);
                     xChange.incrementRetryCount((byte)1);
                     if (this._syncAgentConnectionState.isSuspended()) {
                        return 200;
                     }
                  } else {
                     this._syncAgentConnectionState.resetContextualRetryCount();
                  }

                  this._syncAgentStatistics.incrementNumberOfFailedOperations();
                  this._syncAgentStatistics.setRemovedDueToFailure(true);
                  String xMsg = this._syncAgentUrl.getDatabaseName() + ',' + "RTR" + ',' + "SUSP" + ',';
                  if (!(xChange instanceof SyncApplicationRecordChange)) {
                     if (xChangeStatus.isContextual()) {
                        Logger.logOperationError(0, 0, xMsg + "CNTX");
                     }
                  } else {
                     SyncApplicationRecordChange xRecordChange = (SyncApplicationRecordChange)xChange;
                     Logger.logOperationError(xChange.getOperation(), xRecordChange.getRecordUid(), xMsg + StringUtilities.removeChars("OP=", "="));
                  }
               }

               SyncAgentTransactionManager.getInstance()
                  .excecuteSyncAgentTransaction(
                     new SyncAgentTransaction(66, this._syncAgentUrl, this._configuration.getOperationRetrySuspensionLengthInMinutes())
                  );
            case 58:
               this._syncAgentConnectionState.setSuspended(true);
               Logger.logSuspendResumeConnection(true, this._syncAgentUrl.getDatabaseName(), false);
               return 200;
            case 66:
               this._syncAgentConnectionState.setSuspended(false);
               this._sessionManager.triggerSync(true);
               Logger.logSuspendResumeConnection(false, this._syncAgentUrl.getDatabaseName(), true);
               return 200;
            case 67:
               if (!this.isDisabled() && anObject instanceof SyncApplicationChangeStatus) {
                  this._syncAgentConnectionState.setDisabled(true);
                  Logger.logAddingRemovingConnection(false, 0, 0, this._syncAgentUrl.getDatabaseName() + ',' + "RTR", true);
                  this.close(false);
               }

               return 200;
            default:
               if (this.isClosed() || this.isDisabled()) {
                  String errorMessage = "SyncAgentConnection.onSessionEvent(): The connection is not ready for sync. isClosed()="
                     + this.isClosed()
                     + " isDisabled()="
                     + this.isDisabled();
                  Logger.logErrorMessage(errorMessage);
                  return 412;
               }

               if (this._retryCount >= 10) {
                  this._retryCount = 0;
                  String errorMessage = "SyncAgentConnection.onSessionEvent(): Max retry attempts exceeded";
                  Logger.logErrorMessage(errorMessage);
                  return 412;
               }

               if (!this.isInitialized()) {
                  this._retryCount++;
                  String errorMessage = "SyncAgentConnection.onSessionEvent(): Connection is not initialized, ask server to retry.";
                  Logger.logErrorMessage(errorMessage);
                  return 420;
               }

               this._retryCount = 0;
               if (!(anObject instanceof SyncAgentRecord)) {
                  if (anObject instanceof SyncApplicationChangeStatus) {
                     synchronized (this._syncAgentConnectionState) {
                        SyncApplicationChangeStatus xSyncApplicationChangeStatus = (SyncApplicationChangeStatus)anObject;
                        if (xSyncApplicationChangeStatus.isContextual() && xSyncApplicationChangeStatus.getStatus() == 420) {
                           this._syncAgentConnectionState.incrementContextualRetryCount();
                           return 200;
                        }

                        Vector xChangesList = this._syncAgentConnectionState.getChangesList();
                        if (!xChangesList.isEmpty()) {
                           SyncApplicationChangeList xSyncApplicationChangeList = (SyncApplicationChangeList)xChangesList.firstElement();
                           if (xSyncApplicationChangeList.isLocked()) {
                              int xStatus = xSyncApplicationChangeStatus.getStatus();
                              if (xStatus == 420) {
                                 SyncApplicationChange xAppChange = this._syncAgentConnectionState.getChangeBy(xSyncApplicationChangeStatus.getRefId());
                                 if (xAppChange != null) {
                                    xAppChange.incrementRetryCount((byte)0);
                                 }
                              } else if (xStatus != 414) {
                                 this._syncAgentConnectionState.resetContextualRetryCount();
                                 xSyncApplicationChangeList.removeByRefId(xSyncApplicationChangeStatus.getRefId());
                              }
                           }
                        }
                     }
                  }
               } else {
                  SyncAgentRecord xSyncAgentRecord = (SyncAgentRecord)anObject;

                  try {
                     SyncAgentConnections.checkForLowMemory();
                     if (SyncAgentConnections.isMemoryLow()) {
                        return 413;
                     }

                     if (!this.handleConflicts(eventId, xSyncAgentRecord)) {
                        return 200;
                     }
                  } catch (Throwable var20) {
                     String errorMessage = "SyncAgentConnection.handleConflicts() threw an IOException: " + e.toString();
                     Logger.logOperationError(eventId, 0, errorMessage);
                     return 411;
                  }
               }
         }
      } catch (Throwable var21) {
         String errorMessage = "SyncAgentConnection.onSessionEvent() threw an exception: " + t.toString();
         Logger.logOperationError(eventId, 0, errorMessage);
         return 411;
      }

      return this.notifyListener(eventId, anObject);
   }

   public final void close(boolean byServer) {
      this.close(byServer, false);
   }

   public final void close(boolean byServer, boolean deregisteringAll) {
      synchronized (SyncAgentConnections.getLock()) {
         label61: {
            synchronized (this._syncAgentConnectionState) {
               if (!this.isClosed()) {
                  this._closed = true;
                  if (!deregisteringAll) {
                     SyncAgentConnections.deregisterConnection(this);
                  } else {
                     SyncAgentConnections.deregisterAllConnections();
                  }

                  if (!this.isDisabled()) {
                     SyncAgentConnectionState.purge(this._syncAgentUrl);
                  }

                  this.destroyGroups();
                  SyncAgentStatisticsCollector.purgeSyncAgentStatisticsFor(this._syncAgentUrl);
                  if (!byServer) {
                     this._dataSourceDatabase.setDeviceEnabled(false);
                     this._sessionManager.disableSyncDatabase(this._syncAgentUrl);
                     this._sessionManager.triggerSync(true);
                  }

                  this.notifyListener(9, null);
                  this.setSyncAgentConnectionListener(null);
                  break label61;
               }

               if (this.isDisabled() && deregisteringAll) {
                  SyncAgentConnectionState.purge(this._syncAgentUrl);
               }
            }

            return;
         }
      }

      if (byServer && this._configuration.isUserEnabled() && this._configuration.isUserPreferenceToSyncSet()) {
         SyncAgent xSyncAgent = SyncAgent.getSingletonInstance();
         xSyncAgent.notifyListenersWith(5, this._syncAgentUrl);
      }
   }

   public final SyncAgentGroupOfRecords[] getGroups() {
      synchronized (this._syncAgentConnectionState) {
         if (this.isClosed()) {
            throw ClosedConnectionIOException;
         } else {
            return this._groups;
         }
      }
   }

   public final SyncAgentGroupOfRecords getGroup(int aGroupId) {
      synchronized (this._syncAgentConnectionState) {
         if (this.isClosed()) {
            throw ClosedConnectionIOException;
         } else {
            return this._groups != null ? this._groups[aGroupId] : null;
         }
      }
   }

   public final boolean isDisabled() {
      return this._syncAgentConnectionState.isDisabled();
   }

   public final void buildGroups() {
      ConverterUtilities.indicatePossibleSerializationEncodings(new byte[]{0}, this._configuration.getEncodingCapabilities());
      long xStartTime = System.currentTimeMillis();
      SyncAgentRecordHashesList xListOfRecordHashes = new SyncAgentRecordHashesList();
      this.notifyListener(60, xListOfRecordHashes);
      synchronized (this._syncAgentConnectionState) {
         if (this.isClosed()) {
            throw ClosedConnectionIOException;
         }

         this._groups = SyncAgentGroupOfRecords.createGroups(xListOfRecordHashes, this._dataSourceDatabase, this._configuration.noneXorHashEnabled());
      }

      long xEndTime = System.currentTimeMillis();
      ConverterUtilities.indicatePossibleSerializationEncodings(this._configuration.getEncodingCapabilities(), this._configuration.getEncodingCapabilities());
      Logger.logBuildingGroupHashesTime(this._syncAgentUrl.getDatabaseName(), xEndTime - xStartTime);
   }

   public final boolean isClosed() {
      synchronized (this._syncAgentConnectionState) {
         return this._closed;
      }
   }

   public final boolean isSyncOperationsComplete() {
      synchronized (this._syncAgentConnectionState) {
         return this._syncAgentStatistics.getPercentage() == 100;
      }
   }

   public final boolean useOrderedHash() {
      return this._configuration.noneXorHashEnabled();
   }

   public final void destroyGroups() {
      synchronized (this._syncAgentConnectionState) {
         if (this._groups != null) {
            ReusableObjectPool xReusableObjectPool = ReusableObjectPool.getSingletonInstance(-7570004851727517767L);

            for (int xGroupId = 0; xGroupId < this._groups.length; xGroupId++) {
               SyncAgentGroupOfRecords xSyncAgentGroupOfRecords = this._groups[xGroupId];
               if (xSyncAgentGroupOfRecords != null) {
                  xReusableObjectPool.checkIn(xSyncAgentGroupOfRecords);
               }
            }

            this._groups = null;
         }
      }
   }

   public final IntHashtable getPendingChanges() {
      if (this._syncAgentConnectionState.isSuspended()) {
         return null;
      }

      SyncApplicationChangeList xChangeList = null;
      Vector xChangesList = this._syncAgentConnectionState.getChangesList();

      while (!this.isClosed()) {
         synchronized (this._syncAgentConnectionState) {
            if (xChangesList.isEmpty()) {
               return null;
            }

            xChangeList = (SyncApplicationChangeList)xChangesList.firstElement();
            xChangeList.lock();
         }

         if (xChangeList.shouldBeFilled()) {
            this.notifyListener(62, xChangeList);
            xChangeList.shouldBeFilled(false);
         }

         synchronized (this._syncAgentConnectionState) {
            if (xChangeList.isEmpty()) {
               xChangesList.removeElementAt(0);
               xChangeList = null;
               continue;
            }
         }

         IntHashtable xTableIdToChangesMap = xChangeList.getChanges(this);
         if (!xTableIdToChangesMap.isEmpty()) {
            return xTableIdToChangesMap;
         }

         if (xChangeList.isEmpty()) {
            synchronized (this._syncAgentConnectionState) {
               xChangesList.removeElementAt(0);
               xChangeList = null;
            }
         }
      }

      return null;
   }

   public final short getRetryCountFor(int aRefId) {
      SyncApplicationChange aAppChange = this._syncAgentConnectionState.getChangeBy(aRefId);
      return aAppChange == null ? 0 : aAppChange.getRetryCount((byte)0);
   }

   public final short getRetrySuspensionCount(int aRefId) {
      SyncApplicationChange aAppChange = this._syncAgentConnectionState.getChangeBy(aRefId);
      return aAppChange == null ? 0 : aAppChange.getRetryCount((byte)1);
   }

   @Override
   public final int getSyncPriority() {
      return this._priority;
   }

   private final int notifyListener(int operation, Object anObject) {
      SyncAgentConnectionListener xSyncAgentConnectionListener = this.getSyncAgentConnectionListener();
      int xReturnValue = 411;
      if (xSyncAgentConnectionListener != null) {
         try {
            xReturnValue = xSyncAgentConnectionListener.onSyncAgentConnectionEvent(operation, anObject);
         } finally {
            return xReturnValue;
         }
      }

      return xReturnValue;
   }

   private final void removeAllChangesFor(int aUid, Vector xChangesList, boolean includingDeleteOperationChanges) {
      for (int xIndex = xChangesList.size() - 1; xIndex > -1; xIndex--) {
         SyncApplicationChangeList xSyncApplicationChangeList = (SyncApplicationChangeList)xChangesList.elementAt(xIndex);
         if (xSyncApplicationChangeList != null) {
            if (includingDeleteOperationChanges) {
               xSyncApplicationChangeList.remove(aUid);
            } else {
               SyncApplicationChange xSyncApplicationChange = (SyncApplicationChange)xSyncApplicationChangeList.get(aUid);
               if (xSyncApplicationChange != null && xSyncApplicationChange.getOperation() != 2) {
                  xSyncApplicationChangeList.remove(aUid);
               }
            }
         }
      }
   }

   private final boolean isThereAnyRelatedChangesFor(int aUid, Vector xChangesList) {
      for (int xIndex = xChangesList.size() - 1; xIndex > -1; xIndex--) {
         SyncApplicationChangeList xSyncApplicationChangeList = (SyncApplicationChangeList)xChangesList.elementAt(xIndex);
         if (xSyncApplicationChangeList != null && xSyncApplicationChangeList.get(aUid) != null) {
            return true;
         }
      }

      return false;
   }

   private final void handleRecordLevelConflicts(int operation, SyncAgentRecord aSyncAgentRecord, Vector aChangesList) {
      if (this._dataSourceDatabase.getConflictResolution() == 1) {
         if ((operation == 4 || operation == 5) && this.isThereAnyRelatedChangesFor(aSyncAgentRecord.getUid(), aChangesList)) {
            aSyncAgentRecord.setFields(null);
            return;
         }
      } else {
         this.removeAllChangesFor(aSyncAgentRecord.getUid(), aChangesList, false);
      }
   }

   private final boolean handleConflicts(int operation, SyncAgentRecord aSyncAgentRecord) {
      synchronized (this._syncAgentConnectionState) {
         Vector xChangesList = this._syncAgentConnectionState.getChangesList();
         if (xChangesList.isEmpty()) {
            return true;
         }

         if (operation == 2) {
            this.removeAllChangesFor(aSyncAgentRecord.getUid(), xChangesList, true);
            return true;
         }

         if (this.isTherePendingOperationFor(2, aSyncAgentRecord.getUid(), xChangesList)) {
            return false;
         }

         if (this._dataSourceDatabase.containsTables() && this._dataSourceDatabase.applyFieldLevelConflicts()) {
            SyncApplicationChangeList xSyncApplicationChangeList = (SyncApplicationChangeList)xChangesList.firstElement();
            if (xSyncApplicationChangeList.isLocked()) {
               this.handleFieldLevelConflictsForLockedChangeList(xChangesList, xSyncApplicationChangeList, aSyncAgentRecord);
            } else {
               this.handleFieldLevelConflictsForUnLockedChangeLists(xChangesList, xSyncApplicationChangeList, aSyncAgentRecord);
            }

            return true;
         } else {
            this.handleRecordLevelConflicts(operation, aSyncAgentRecord, xChangesList);
            return true;
         }
      }
   }

   private final void handleFieldLevelConflictsForLockedChangeList(
      Vector aChangesList, SyncApplicationChangeList aSyncApplicationChangeList, SyncAgentRecord aSyncAgentRecord
   ) {
      SyncApplicationChange xSyncApplicationChange = (SyncApplicationChange)aSyncApplicationChangeList.get(aSyncAgentRecord.getUid());
      if (xSyncApplicationChange != null) {
         if (this._dataSourceDatabase.getConflictResolution() != 1) {
            byte[] xParameters = xSyncApplicationChange.getParameters(false);
            if (xParameters != null) {
               TLEFieldsList xA = new TLEFieldsList(xParameters);
               aSyncAgentRecord.rewind();
               TLEFieldsList xC = new TLEFieldsList(aSyncAgentRecord);
               TLEFieldsList xX = xC.intersectWith(xA, false);
               aSyncAgentRecord.setFields(xC.toByteArray());
               xA = xX.subtractFrom(xA, true);
               xParameters = xA.toByteArray();
               xSyncApplicationChange.setParameters(xParameters);
               int xRecordUId = aSyncAgentRecord.getUid();

               for (int xIndex = 1; xIndex < aChangesList.size(); xIndex++) {
                  aSyncApplicationChangeList = (SyncApplicationChangeList)aChangesList.elementAt(xIndex);
                  xSyncApplicationChange = (SyncApplicationChange)aSyncApplicationChangeList.get(xRecordUId);
                  if (xSyncApplicationChange != null) {
                     TLEFieldsList xT = new TLEFieldsList(xParameters);
                     TLEFieldsList xY = xC.intersectWith(xT, false);
                     xT = xX.subtractFrom(xT, true);
                     xT = xY.subtractFrom(xT, true);
                     xSyncApplicationChange.setParameters(xT.toByteArray());
                  }
               }
            }
         } else {
            aSyncAgentRecord.rewind();
            TLEFieldsList xC = new TLEFieldsList(aSyncAgentRecord);
            byte[] xParameters = xSyncApplicationChange.getParameters(false);
            if (xParameters != null) {
               TLEFieldsList xA = new TLEFieldsList(xParameters);
               TLEFieldsList xX = xA.intersectWith(xC, false);
               xX.subtractFrom(xC, true);
               aSyncAgentRecord.setFields(xC.toByteArray());
            }

            int xRecordUid = aSyncAgentRecord.getUid();

            for (int xIndex = 1; xIndex < aChangesList.size(); xIndex++) {
               aSyncApplicationChangeList = (SyncApplicationChangeList)aChangesList.elementAt(xIndex);
               if (aSyncApplicationChangeList != null) {
                  xSyncApplicationChange = (SyncApplicationChange)aSyncApplicationChangeList.get(xRecordUid);
                  if (xSyncApplicationChange != null) {
                     xParameters = xSyncApplicationChange.getParameters(false);
                     if (xParameters != null) {
                        TLEFieldsList xT = new TLEFieldsList(xParameters);
                        xC.subtractFrom(xT, true);
                        xSyncApplicationChange.setParameters(xT.toByteArray());
                     }
                  }
               }
            }
         }
      }
   }

   private final void handleFieldLevelConflictsForUnLockedChangeLists(
      Vector aChangesList, SyncApplicationChangeList aSyncApplicationChangeList, SyncAgentRecord aSyncAgentRecord
   ) {
      SyncApplicationChange xSyncApplicationChange = (SyncApplicationChange)aSyncApplicationChangeList.get(aSyncAgentRecord.getUid());
      if (xSyncApplicationChange != null) {
         if (xSyncApplicationChange.getOperation() != 2) {
            byte[] xParameters = xSyncApplicationChange.getParameters(false);
            if (xParameters != null) {
               aSyncAgentRecord.rewind();
               TLEFieldsList xC = new TLEFieldsList(aSyncAgentRecord);
               TLEFieldsList xT = new TLEFieldsList(xParameters);
               TLEFieldsList xX = xC.intersectWith(xT, false);
               xX.subtractFrom(xT, true);
               if (xX.size() != 0) {
                  if (xT.size() != 0) {
                     xSyncApplicationChange.setParameters(xT.toByteArray());
                     return;
                  }

                  aSyncApplicationChangeList.remove(aSyncAgentRecord.getUid());
               }
            }
         }
      }
   }

   private final void reset(boolean triggerSync) {
      synchronized (this._syncAgentConnectionState) {
         this._syncAgentConnectionState.resetAll();
         this._syncAgentStatistics.reset();
         if (triggerSync) {
            this._sessionManager.triggerSync(true);
         }
      }
   }

   private final SyncApplicationChangeList createChangeList() {
      return this._dataSourceDatabase.containsTables() ? new SyncApplicationOptimizedChangeList() : new SyncApplicationUnOptimizedChangeList();
   }

   @Override
   public final int hashCode() {
      return this._syncAgentUrl.hashCode();
   }

   @Override
   public final boolean equals(Object anObject) {
      if (this == anObject) {
         return true;
      } else {
         return anObject instanceof SyncAgentConnection ? this.hashCode() == anObject.hashCode() : false;
      }
   }

   public SyncAgentConnection(SyncAgentUrl aSyncAgentUrl, int aPriority, SyncCollectionSchema aSyncCollectionSchema, int appModualHandle, int aDependencyLevel) throws IOException {
      if (aPriority > 10 || aPriority < 1) {
         throw new IllegalArgumentException();
      }

      if (aDependencyLevel <= 255 && aDependencyLevel >= 1) {
         this._syncAgentUrl = aSyncAgentUrl;
         long sid = this._syncAgentUrl.getSid();
         this._sessionManager = SessionManager.getSessionManagerFor(sid);
         if (this._sessionManager == null) {
            throw new IOException();
         }

         this._priority = (byte)aPriority;
         this._dependencyLevel = (byte)(aDependencyLevel + -128);
         this._configuration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
         if (!this._configuration.isUserPreferenceToSyncSet()) {
            throw new IOException();
         }

         String xDataSourceName = this._syncAgentUrl.getDataSourceName();
         String xDatabaseName = this._syncAgentUrl.getDatabaseName();
         int xDatabaseVersion = this._syncAgentUrl.getVersion();
         DataSource xDataSource = this._configuration.getDataSourceBy(xDataSourceName);
         boolean xSchemaIsProvided = aSyncCollectionSchema != null && this._configuration.shouldSendDatabaseSchema();
         this._dataSourceDatabase = this._configuration
            .getClosestDataSourceDatabase(xDataSourceName, xSchemaIsProvided ? "*" : xDatabaseName, xDatabaseVersion);
         if (this._dataSourceDatabase != null && this._dataSourceDatabase.isServerEnabled()) {
            if (this._dataSourceDatabase.isGeneric()) {
               this._dataSourceDatabase = this._dataSourceDatabase.makeSpecificTo(xDatabaseName, xDatabaseVersion, aSyncCollectionSchema);
               this._dataSourceDatabase.useExplicitDatabaseName(true);
               xDataSource.addDataSourceDatabase(this._dataSourceDatabase);
            }

            if (xSchemaIsProvided) {
               this._dataSourceDatabase.sendWithInitialize(true);
               this._dataSourceDatabase.applyDiffOperations(false);
               this._dataSourceDatabase.applyMergeOperations(false);
               this._dataSourceDatabase.useExplicitDatabaseName(true);
               this._dataSourceDatabase.includeUnmappedFields(true);
               this._dataSourceDatabase.applyFieldLevelConflicts(false);
            }

            this._syncAgentConnectionState = SyncAgentConnectionState.create(this._syncAgentUrl);
            this._syncAgentConnectionState.setAppModualHandle(appModualHandle);
            this._syncAgentStatistics = SyncAgentStatisticsCollector.getSyncAgentStatisticsFor(aSyncAgentUrl);
            if (this._syncAgentConnectionState.isDisabled()) {
               this._closed = true;
            }
         } else {
            SyncAgentStatisticsCollector.purgeSyncAgentStatisticsFor(aSyncAgentUrl);
            throw new IOException();
         }
      } else {
         throw new IllegalArgumentException();
      }
   }
}
