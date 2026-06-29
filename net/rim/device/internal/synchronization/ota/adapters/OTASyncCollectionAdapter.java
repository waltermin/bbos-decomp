package net.rim.device.internal.synchronization.ota.adapters;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.synchronization.OTASyncEventOptimizationProvider;
import net.rim.device.api.synchronization.OTASyncListener;
import net.rim.device.api.synchronization.OTASyncPriorityAndDependencyProvider;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncCollectionSchemaProvider;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntVector;
import net.rim.device.internal.synchronization.ota.api.Logger;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnection;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnectionListener;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnections;
import net.rim.device.internal.synchronization.ota.api.SyncAgentRecord;
import net.rim.device.internal.synchronization.ota.api.SyncAgentRecordHashes;
import net.rim.device.internal.synchronization.ota.api.SyncAgentRecordHashesList;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationChangeList;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationGroupChangeList;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationOptimizedGroupChangeList;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationOptimizedRecordChange;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationRecordChange;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationUnOptimizedGroupChangeList;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabase;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabaseFields;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabaseTable;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;
import net.rim.device.internal.synchronization.ota.util.TLEFieldsList;
import net.rim.vm.Array;
import net.rim.vm.DirtyBits;

public final class OTASyncCollectionAdapter implements CollectionListener, SyncAgentConnectionListener {
   private SyncAgentConnection _syncAgentConnection;
   private SyncAgentUrl _syncAgentUrl;
   private SyncCollection _syncCollection;
   private SyncConverter _syncConverter;
   private IntVector _listOfIgnoredUidToOperation;
   private DataSourceDatabase _dataSourceDatabase;
   private SyncObjectWrapper _inBoundSyncObjectWrapper;
   private SyncObjectWrapper _outBoundSyncObjectWrapper;
   private TLEFieldsList _inBoundNewTLEFields;
   private TLEFieldsList _inBoundOldTLEFields;
   private long _transactionTime;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void bind(SyncAgentUrl aSyncAgentUrl) {
      try {
         if (this.isBound()) {
            throw new Object();
         }

         this._listOfIgnoredUidToOperation = (IntVector)(new Object());
         this._syncAgentUrl = aSyncAgentUrl;
         this._syncAgentUrl.setVersion(this._syncCollection.getSyncVersion());
         int xSyncCollectionPriority = !(this._syncCollection instanceof Object) ? 5 : ((OTASyncPriorityProvider)this._syncCollection).getSyncPriority();
         int xSyncCollectionDependencyLevel = !(this._syncCollection instanceof Object)
            ? 255
            : ((OTASyncPriorityAndDependencyProvider)this._syncCollection).getDependencyLevel();
         boolean xResetTheConnection = false;
         if (!SyncAgentConnections.isConnectionRegistered(aSyncAgentUrl)) {
            SyncCollectionSchema xSyncCollectionSchema = null;
            if (this._syncCollection instanceof Object) {
               xSyncCollectionSchema = ((SyncCollectionSchemaProvider)this._syncCollection).getSchema();
            }

            this._syncAgentConnection = new SyncAgentConnection(
               this._syncAgentUrl,
               xSyncCollectionPriority,
               xSyncCollectionSchema,
               CodeModuleManager.getModuleHandleForObject(this._syncCollection),
               xSyncCollectionDependencyLevel
            );
         } else {
            this._syncAgentConnection = SyncAgentConnections.getConnectionBy(this._syncAgentUrl);
            OTASyncCollectionAdapter xOTASyncCollectionAdapter = (OTASyncCollectionAdapter)this._syncAgentConnection.getSyncAgentConnectionListener();
            if (xOTASyncCollectionAdapter != null) {
               xOTASyncCollectionAdapter.degisterCollectionEventListerner();
            }

            xResetTheConnection = true;
         }

         if (!this._syncAgentConnection.isDisabled()) {
            this._dataSourceDatabase = this._syncAgentConnection.getDataSourceDatabase();
            if (this._dataSourceDatabase.isOneWaySyncToServerAllowed()) {
               this.registerCollectionEventListener();
            }

            if (SyncAgent.getSingletonInstance().isUsedByOtherSyncSources(this._syncAgentUrl.getSid(), this._syncAgentUrl.getDatabaseName())) {
               this._dataSourceDatabase.makeCopyOfConflictSettings();
               this._dataSourceDatabase.makeCopyOfSyncTypeSettings();
               this._dataSourceDatabase.setConflictResolution(1);
               xResetTheConnection = true;
            }

            if (xResetTheConnection) {
               this._syncAgentConnection.reset();
            } else {
               this._syncAgentConnection.flush();
            }

            this._syncAgentConnection.setSyncAgentConnectionListener(this);
         }
      } catch (Throwable var7) {
         this.degisterCollectionEventListerner();
         throw new Object(t.getMessage());
      }
   }

   public final void unbind() {
      this.unbind(false);
   }

   public final void unbind(boolean systemOTASyncDisabled) {
      this.degisterCollectionEventListerner();
      if (this._syncAgentConnection != null) {
         this._syncAgentConnection.close(false, systemOTASyncDisabled);
      }
   }

   public final boolean isBound() {
      return this._syncAgentConnection != null && !this._syncAgentConnection.isClosed() && !this._syncAgentConnection.isDisabled();
   }

   public final SyncCollection getSyncCollection() {
      return this._syncCollection;
   }

   public final boolean isInitialized() {
      return this._syncAgentConnection != null && this._syncAgentConnection.isInitialized();
   }

   public final void reInitialize() {
      if (this._syncAgentConnection != null) {
         this._syncAgentConnection.reset();
      }
   }

   public final boolean isOTASyncCompleted() {
      return this.isInitialized() ? this._syncAgentConnection.isSyncOperationsComplete() : false;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void handleElementOperations(
      int anOperation, Collection collection, Object oldElement, Object newElement, boolean fillChangeLater, boolean forceSync
   ) {
      if (this.verifyUidMatch(anOperation, (SyncObject)oldElement, (SyncObject)newElement)) {
         Object xCpTicket = PersistentContent.getTicket();
         boolean xLookForTableIdOnDelete = true;
         if (PersistentContent.isContentProtectionSupported() && xCpTicket == null) {
            fillChangeLater = true;
            xLookForTableIdOnDelete = false;
         }

         int xUid = ((SyncObject)newElement).getUID();
         if (!this.isValidUid(xUid)) {
            Logger.logInvalidUid(anOperation, this._dataSourceDatabase.getName(), xUid);
         } else {
            synchronized (this._listOfIgnoredUidToOperation) {
               int xIndexOfTheUid = this._listOfIgnoredUidToOperation.indexOf(xUid);
               if (xIndexOfTheUid != -1) {
                  this._listOfIgnoredUidToOperation.removeElementAt(xIndexOfTheUid);
                  return;
               }
            }

            SyncObjectWrapper xOldSyncObjectWrapper = null;
            SyncObjectWrapper xNewSyncObjectWrapper = null;
            boolean xDatabaseHasTables = this._dataSourceDatabase.containsTables();
            int xDatabaseVersion = this._syncCollection.getSyncVersion();

            try {
               if (!fillChangeLater) {
                  if (xDatabaseHasTables && anOperation == 4 && this._dataSourceDatabase.applyDiffOperations() && oldElement == newElement) {
                     return;
                  }

                  xNewSyncObjectWrapper = new SyncObjectWrapper((SyncObject)newElement);
                  xNewSyncObjectWrapper.setDataSourceDatabase(this._dataSourceDatabase, xDatabaseVersion);
                  xNewSyncObjectWrapper.setSyncConverter(this._syncConverter);
                  if (!xNewSyncObjectWrapper.convertToSerialForm(xDatabaseHasTables)) {
                     Logger.logOperationError(anOperation, xUid, "");
                     return;
                  }

                  int xTableId = xNewSyncObjectWrapper.getTableId();
                  if (xDatabaseHasTables) {
                     if (!this._dataSourceDatabase.containsTable(xTableId)) {
                        Logger.logNotDefinedTableId(anOperation, xTableId);
                        return;
                     }

                     if (!xNewSyncObjectWrapper.encode(9)) {
                        Logger.logOperationError(anOperation, xUid, "");
                     }

                     if (anOperation == 4 && oldElement != null && this._dataSourceDatabase.applyDiffOperations()) {
                        xOldSyncObjectWrapper = new SyncObjectWrapper((SyncObject)oldElement);
                        xOldSyncObjectWrapper.setDataSourceDatabase(this._dataSourceDatabase, xDatabaseVersion);
                        xOldSyncObjectWrapper.setSyncConverter(this._syncConverter);
                        xOldSyncObjectWrapper.setTableId(xTableId);
                        if (!xOldSyncObjectWrapper.convertToSerialForm(false) || !xOldSyncObjectWrapper.encode(9)) {
                           Logger.logOperationError(anOperation, xUid, "");
                           return;
                        }

                        xOldSyncObjectWrapper.rewindOtaFormOfSyncObject();
                        xNewSyncObjectWrapper.rewindOtaFormOfSyncObject();
                        DataBuffer xDiffBuffer = this.diff(xOldSyncObjectWrapper.getOtaFormOfSyncObject(), xNewSyncObjectWrapper.getOtaFormOfSyncObject());
                        if (xDiffBuffer.available() == 0) {
                           return;
                        }

                        if (xDatabaseHasTables) {
                           xNewSyncObjectWrapper.resetOtaFormOfSyncObject();
                           xNewSyncObjectWrapper.getOtaFormOfSyncObject().write(xDiffBuffer.getArray());
                        }
                     }
                  }

                  byte[] xRecordFields = null;
                  if (anOperation != 2) {
                     xRecordFields = xDatabaseHasTables ? xNewSyncObjectWrapper.getOtaBuffer() : xNewSyncObjectWrapper.getSerialBuffer();
                  }

                  this.submitRecordChange(anOperation, xUid, xTableId, xRecordFields, fillChangeLater, forceSync);
                  return;
               }

               int xTableId = 0;
               if (anOperation == 2) {
                  if (xLookForTableIdOnDelete) {
                     if (xDatabaseHasTables) {
                        xNewSyncObjectWrapper = new SyncObjectWrapper((SyncObject)newElement);
                        xNewSyncObjectWrapper.setDataSourceDatabase(this._dataSourceDatabase, xDatabaseVersion);
                        xNewSyncObjectWrapper.setSyncConverter(this._syncConverter);
                        if (!xNewSyncObjectWrapper.convertToSerialForm(true)) {
                           Logger.logOperationError(anOperation, xUid, "");
                           return;
                        }

                        xTableId = xNewSyncObjectWrapper.getTableId();
                     } else {
                        xTableId = this._dataSourceDatabase.getDefaultTableId();
                     }
                  }

                  fillChangeLater = false;
               }

               this.submitRecordChange(anOperation, xUid, xTableId, null, fillChangeLater, forceSync);
            } catch (Throwable var18) {
               Logger.logOperationError(anOperation, xUid, t);
               return;
            }
         }
      }
   }

   public final void flush() {
      try {
         if (this._syncAgentConnection != null) {
            this._syncAgentConnection.flush();
            return;
         }
      } finally {
         return;
      }
   }

   @Override
   public final void reset(Collection collection) {
      if (this._syncAgentConnection.isDeleteOnSlowSyncEnabled()) {
         this._dataSourceDatabase.setDeleteOnSlowSync(true);
         this._dataSourceDatabase.makeCopyOfConflictSettings();
         this._dataSourceDatabase.makeCopyOfSyncTypeSettings();
         this._dataSourceDatabase.setConflictResolution(1);
         this._dataSourceDatabase.setSyncType(2);
         this._dataSourceDatabase.sendWithInitialize(true);
         this.reInitialize();
      }

      synchronized (this._listOfIgnoredUidToOperation) {
         this._listOfIgnoredUidToOperation.removeAllElements();
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      this.handleElementOperations(1, collection, null, element, true, false);
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      this.handleElementOperations(2, collection, null, element, true, true);
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      boolean xNeedToFillInChangeLater = (!this._dataSourceDatabase.containsTables() || !this._dataSourceDatabase.applyMergeOperations())
         && this.IsOptimizeChanges(collection);
      this.handleElementOperations(4, collection, oldElement, newElement, xNeedToFillInChangeLater, false);
   }

   @Override
   public final int onSyncAgentConnectionEvent(int eventId, Object anObject) {
      int xReturnCode = 407;
      switch (eventId) {
         case 1:
            if (anObject instanceof SyncAgentRecord) {
               SyncAgentRecord xSyncAgentRecordx = (SyncAgentRecord)anObject;
               xReturnCode = this.onAddRecordFromServer(xSyncAgentRecordx);
               if (xReturnCode != 200 && this._dataSourceDatabase.isDeleteOnFailuerOn()) {
                  this.submitRecordChange(2, xSyncAgentRecordx.getUid(), xSyncAgentRecordx.getTableId(), null, false, true);
                  return xReturnCode;
               }
            }
            break;
         case 2:
            return this.onDeleteRecordFromServer((SyncAgentRecord)anObject);
         case 3:
            return this.onDeleteAllFromServer();
         case 4:
            if (anObject instanceof SyncAgentRecord) {
               SyncAgentRecord xSyncAgentRecordx = (SyncAgentRecord)anObject;
               xReturnCode = this.onUpdateRecordFromServer(xSyncAgentRecordx);
               if (xReturnCode != 200 && this._dataSourceDatabase.isDeleteOnFailuerOn()) {
                  this.submitRecordChange(2, xSyncAgentRecordx.getUid(), xSyncAgentRecordx.getTableId(), null, false, true);
                  return xReturnCode;
               }
            }
            break;
         case 5:
            if (anObject instanceof SyncAgentRecord) {
               SyncAgentRecord xSyncAgentRecordx = (SyncAgentRecord)anObject;
               xReturnCode = this.onReplaceRecordFromServer(xSyncAgentRecordx);
               if (xReturnCode != 200 && this._dataSourceDatabase.isDeleteOnFailuerOn()) {
                  this.submitRecordChange(2, xSyncAgentRecordx.getUid(), xSyncAgentRecordx.getTableId(), null, false, true);
                  return xReturnCode;
               }
            }
            break;
         case 6: {
            SyncAgentRecord xSyncAgentRecord = (SyncAgentRecord)anObject;
            return this.onGetRecordFromServer(xSyncAgentRecord);
         }
         case 7: {
            SyncAgentRecord xSyncAgentRecord = (SyncAgentRecord)anObject;
            return this.onGetAllRecordsFromServer(xSyncAgentRecord);
         }
         case 8:
            return 200;
         case 9:
            this.degisterCollectionEventListerner();
            return 200;
         case 49:
            return this.onStartSyncTransaction(true);
         case 50:
            return this.onEndSyncTransaction(true);
         case 51:
            return this.onStartSyncTransaction(false);
         case 52:
            return this.onEndSyncTransaction(false);
         case 53:
            return this.onStartSlowSyncTransaction();
         case 54:
            return this.onEndSlowSyncTransaction();
         case 55:
            SyncAgent.getSingletonInstance().unMarkDatabaseAsUsedByOtherSyncSources(this._syncAgentUrl.getSid(), this._syncAgentUrl.getDatabaseName());
            if (this._syncAgentUrl.getSid() != -1) {
               SyncAgent.getSingletonInstance().unMarkDatabaseAsUsedByOtherSyncSources(-1, this._syncAgentUrl.getDatabaseName());
            }

            this._dataSourceDatabase.setDeleteOnSlowSync(false);
            this._dataSourceDatabase.restoreConflictSettings();
            this._dataSourceDatabase.restoreSyncTypeSettings();
            return 200;
         case 60:
            return this.onGetAllRecordsHashes((SyncAgentRecordHashesList)anObject);
         case 61:
            return this.onFillInRecordChange((SyncApplicationRecordChange)anObject);
         case 62:
            return this.onFillInChangeList((SyncApplicationChangeList)anObject);
         case 63:
            return this._syncCollection.getSyncObjectCount();
         default:
            xReturnCode = 407;
      }

      return xReturnCode;
   }

   private final void degisterCollectionEventListerner() {
      if (this._syncCollection instanceof Object) {
         ((CollectionEventSource)this._syncCollection).removeCollectionListener(this);
      }
   }

   private final boolean verifyUidMatch(int operation, SyncObject oldSyncObject, SyncObject newSyncObject) {
      if (oldSyncObject != null && newSyncObject != null && oldSyncObject.getUID() != newSyncObject.getUID()) {
         Logger.logUidsNotMatching(operation, oldSyncObject.getUID(), newSyncObject.getUID(), this._syncAgentUrl.getDatabaseName());
         return false;
      } else {
         return true;
      }
   }

   private final void registerCollectionEventListener() {
      if (this._syncCollection instanceof Object) {
         ((CollectionEventSource)this._syncCollection).addCollectionListener(this);
      }
   }

   private final void filterUnMappedFields(TLEFieldsList aTLEFieldsList, int aTableId) throws OTASyncCollectionAdapter$InvalidTableIdException {
      DataSourceDatabaseTable xDataSourceDatabaseTable = this._dataSourceDatabase.getTable(aTableId);
      if (xDataSourceDatabaseTable == null) {
         throw new OTASyncCollectionAdapter$InvalidTableIdException(null);
      }

      DataSourceDatabaseFields xDataSourceDatabaseFields = xDataSourceDatabaseTable.getSchema();
      int[] xTags = aTLEFieldsList.getTags(new int[aTLEFieldsList.size()]);

      for (int xIndex = xTags.length - 1; xIndex > -1; xIndex--) {
         int xTag = xTags[xIndex];
         if (!xDataSourceDatabaseFields.contains(xTag)) {
            aTLEFieldsList.remove(xTag);
         }
      }
   }

   private final DataBuffer diff(DataBuffer aTleFormOfOldElement, DataBuffer aTleFormOfNewElement) {
      TLEFieldsList xOldTLEFields = new TLEFieldsList(aTleFormOfOldElement);
      TLEFieldsList xNewTLEFields = new TLEFieldsList(aTleFormOfNewElement);
      TLEFieldsList xDiffTLEFields = xOldTLEFields.diffWith(xNewTLEFields, true);
      return xDiffTLEFields.toDataBuffer();
   }

   private final boolean isValidUid(int uid) {
      return uid != 0;
   }

   private final void submitRecordChange(int operation, int uid, int tableId, byte[] recordFields, boolean fillInLater, boolean forceSync) {
      try {
         SyncApplicationRecordChange xSyncApplicationRecordChange = this.createSyncApplicationRecordChange();
         xSyncApplicationRecordChange.setOperation(operation);
         xSyncApplicationRecordChange.setRecordUid(uid);
         xSyncApplicationRecordChange.setTableId((byte)tableId);
         xSyncApplicationRecordChange.setRecordFields(recordFields);
         xSyncApplicationRecordChange.shouldBeFilled(fillInLater);
         this._syncAgentConnection.submit(xSyncApplicationRecordChange, forceSync, this.IsOptimizeChanges());
      } finally {
         return;
      }
   }

   public OTASyncCollectionAdapter(SyncCollection aSyncCollection) {
      this._syncCollection = aSyncCollection;
      this._syncConverter = this._syncCollection.getSyncConverter();
   }

   private final int onStartSyncTransaction(boolean byServerSession) {
      if (byServerSession) {
         this._transactionTime = System.currentTimeMillis();
         if (this._syncCollection instanceof Object) {
            ((OTASyncListener)this._syncCollection).otaSyncOperationStarted(this._syncCollection, 1);
         }

         SyncAgent.getSingletonInstance().notifyListenersWith(21, this._syncAgentUrl);
         Logger.logTransaction(true, 0, this._syncAgentUrl.getDatabaseName());
      }

      return 200;
   }

   private final int onEndSyncTransaction(boolean byServerSession) {
      if (byServerSession) {
         if (this._syncCollection instanceof Object) {
            ((OTASyncListener)this._syncCollection).otaSyncOperationStopped(this._syncCollection, 1);
         }

         DirtyBits.commit();
         SyncAgent.getSingletonInstance().notifyListenersWith(22, this._syncAgentUrl);
         this._inBoundNewTLEFields = null;
         this._inBoundOldTLEFields = null;
         this._inBoundSyncObjectWrapper = null;
         Logger.logTransaction(false, System.currentTimeMillis() - this._transactionTime, this._syncAgentUrl.getDatabaseName());
      }

      return 200;
   }

   private final int onStartSlowSyncTransaction() {
      if (this._syncCollection instanceof Object) {
         ((OTASyncListener)this._syncCollection).otaSyncOperationStarted(this._syncCollection, 0);
      }

      return 200;
   }

   private final int onEndSlowSyncTransaction() {
      if (this._syncCollection instanceof Object) {
         ((OTASyncListener)this._syncCollection).otaSyncOperationStopped(this._syncCollection, 0);
      }

      return 200;
   }

   private final void makeInBoundTLEFieldsReady() {
      if (this._inBoundNewTLEFields == null) {
         this._inBoundNewTLEFields = new TLEFieldsList();
      } else {
         this._inBoundNewTLEFields.reset();
      }

      if (this._inBoundOldTLEFields == null) {
         this._inBoundOldTLEFields = new TLEFieldsList();
      } else {
         this._inBoundOldTLEFields.reset();
      }
   }

   private final void makeSyncObjectWrapperReady(boolean inBound, int aRecordUid, int aTableId) {
      SyncObjectWrapper xSyncObjectWrapper = inBound ? this._inBoundSyncObjectWrapper : this._outBoundSyncObjectWrapper;
      if (xSyncObjectWrapper == null) {
         xSyncObjectWrapper = new SyncObjectWrapper();
         xSyncObjectWrapper.setDataSourceDatabase(this._dataSourceDatabase, this._syncCollection.getSyncVersion());
         xSyncObjectWrapper.setSyncConverter(this._syncConverter);
      }

      xSyncObjectWrapper.setUid(aRecordUid);
      xSyncObjectWrapper.setTableId(aTableId);
      if (inBound) {
         this._inBoundSyncObjectWrapper = xSyncObjectWrapper;
      } else {
         this._outBoundSyncObjectWrapper = xSyncObjectWrapper;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int onAddRecordFromServer(SyncAgentRecord aSyncAgentRecord) {
      int returnCode = 200;
      if (aSyncAgentRecord.getFields() != null && aSyncAgentRecord.getFields().length != 0) {
         int xRecordUid = aSyncAgentRecord.getUid();
         boolean xDatabaseHasTables = this._dataSourceDatabase.containsTables();
         boolean var13 = false /* VF: Semaphore variable */;

         int var23;
         label182: {
            label183: {
               try {
                  try {
                     var13 = true;
                     if (this._syncCollection.getSyncObject(xRecordUid) != null) {
                        var23 = returnCode;
                        var13 = false;
                        break label182;
                     }

                     this.makeSyncObjectWrapperReady(true, xRecordUid, aSyncAgentRecord.getTableId());
                     if (xDatabaseHasTables) {
                        this._inBoundSyncObjectWrapper.setOtaFormOfSyncObject(aSyncAgentRecord);
                     } else {
                        this._inBoundSyncObjectWrapper.setSerialFormOfSyncObject(aSyncAgentRecord);
                     }

                     synchronized (this._listOfIgnoredUidToOperation) {
                        this._listOfIgnoredUidToOperation.addElement(xRecordUid);
                     }

                     if (this._inBoundSyncObjectWrapper.decode() && this._syncCollection.addSyncObject(this._inBoundSyncObjectWrapper.getSyncObject())) {
                        DirtyBits.setClean(this._inBoundSyncObjectWrapper.getSyncObject());
                        var13 = false;
                     } else {
                        synchronized (this._listOfIgnoredUidToOperation) {
                           this._listOfIgnoredUidToOperation.removeElement(xRecordUid);
                           String errorMessage = "Wrapper decode or SyncCollection.addSyncObject() failed.";
                           Logger.logOperationError(1, xRecordUid, errorMessage);
                           returnCode = 411;
                           var13 = false;
                        }
                     }
                  } catch (Throwable var20) {
                     Logger.logOperationError(1, xRecordUid, t);
                     returnCode = 411;
                     var13 = false;
                     break label183;
                  }
               } finally {
                  if (var13) {
                     if (this._inBoundSyncObjectWrapper != null) {
                        this._inBoundSyncObjectWrapper.reset(true);
                     }
                  }
               }

               if (this._inBoundSyncObjectWrapper != null) {
                  this._inBoundSyncObjectWrapper.reset(true);
                  return returnCode;
               }

               return returnCode;
            }

            if (this._inBoundSyncObjectWrapper != null) {
               this._inBoundSyncObjectWrapper.reset(true);
               return returnCode;
            }

            return returnCode;
         }

         if (this._inBoundSyncObjectWrapper != null) {
            this._inBoundSyncObjectWrapper.reset(true);
         }

         return var23;
      } else {
         return returnCode;
      }
   }

   private final int onDeleteRecordFromServer(SyncAgentRecord aSyncAgentRecord) {
      int returnCode = 200;
      int xRecordUid = aSyncAgentRecord.getUid();
      SyncObject xOldElement = this._syncCollection.getSyncObject(xRecordUid);
      if (xOldElement != null) {
         synchronized (this._listOfIgnoredUidToOperation) {
            this._listOfIgnoredUidToOperation.addElement(xRecordUid);
         }

         if (!this._syncCollection.removeSyncObject(xOldElement)) {
            synchronized (this._listOfIgnoredUidToOperation) {
               this._listOfIgnoredUidToOperation.removeElement(xRecordUid);
               String errorMessage = "SyncCollection.removeSyncObject() failed.";
               Logger.logOperationError(2, xRecordUid, errorMessage);
               return 411;
            }
         }
      }

      return returnCode;
   }

   private final int onDeleteAllFromServer() {
      int returnCode = 200;
      if (!this._syncCollection.removeAllSyncObjects()) {
         String errorMessage = "SyncCollection.removeAllSyncObjects() failed.";
         Logger.logOperationError(3, 0, errorMessage);
         returnCode = 411;
      }

      return returnCode;
   }

   private final int onReplaceRecordFromServer(SyncAgentRecord aSyncAgentRecord) {
      return this.onUpdateRecordFromServerCore(aSyncAgentRecord, true);
   }

   private final int onUpdateRecordFromServer(SyncAgentRecord aSyncAgentRecord) {
      return this.onUpdateRecordFromServerCore(aSyncAgentRecord, false);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int onUpdateRecordFromServerCore(SyncAgentRecord aSyncAgentRecord, boolean handleAsReplace) {
      int operation = handleAsReplace ? 5 : 4;
      int returnCode = 200;
      if (aSyncAgentRecord.getFields() != null && aSyncAgentRecord.getFields().length != 0) {
         int xTableId = aSyncAgentRecord.getTableId();
         int xRecordUid = aSyncAgentRecord.getUid();
         boolean xDatabaseHasTables = this._dataSourceDatabase.containsTables();
         boolean xApplyMergeOperation = this._dataSourceDatabase.applyMergeOperations();
         boolean var23 = false /* VF: Semaphore variable */;

         short var41;
         label406: {
            label407: {
               label408: {
                  try {
                     try {
                        try {
                           var23 = true;
                           SyncObject t = this._syncCollection.getSyncObject(xRecordUid);
                           if (t != null) {
                              this.makeSyncObjectWrapperReady(true, xRecordUid, xTableId);
                              this._inBoundSyncObjectWrapper.setSyncObject(t);
                              if (!xDatabaseHasTables) {
                                 this._inBoundSyncObjectWrapper.setSerialFormOfSyncObject(aSyncAgentRecord);
                              } else {
                                 if (xApplyMergeOperation
                                    && (
                                       !this._inBoundSyncObjectWrapper.convertToSerialForm(false)
                                          || !this._inBoundSyncObjectWrapper.encode(handleAsReplace ? 10 : 8)
                                    )) {
                                    String errorMessage = "Wrapper convertToSerialForm() or Wrapper encode() failed.";
                                    Logger.logOperationError(operation, xRecordUid, errorMessage);
                                    var41 = 411;
                                    var23 = false;
                                    break label406;
                                 }

                                 this._inBoundSyncObjectWrapper.rewindOtaFormOfSyncObject();
                                 if (xApplyMergeOperation) {
                                    this.makeInBoundTLEFieldsReady();
                                    this._inBoundNewTLEFields.readFrom(aSyncAgentRecord);
                                    this._inBoundOldTLEFields.readFrom(this._inBoundSyncObjectWrapper.getOtaFormOfSyncObject());
                                    if (aSyncAgentRecord.isForSlowSync()) {
                                       TLEFieldsList xRemaingingTLEFields = this._inBoundNewTLEFields.subtractFrom(this._inBoundOldTLEFields, false);
                                       this.filterUnMappedFields(xRemaingingTLEFields, xTableId);
                                       if (xRemaingingTLEFields.size() != 0) {
                                          SyncApplicationRecordChange xSyncApplicationRecordChange = new SyncApplicationOptimizedRecordChange();
                                          xSyncApplicationRecordChange.setOperation(4);
                                          xSyncApplicationRecordChange.setRecordUid(xRecordUid);
                                          xSyncApplicationRecordChange.setRecordFields(xRemaingingTLEFields.toByteArray());
                                          xSyncApplicationRecordChange.setTableId((byte)aSyncAgentRecord.getTableId());
                                          this._syncAgentConnection.submit(xSyncApplicationRecordChange, true, this.IsOptimizeChanges());
                                       }
                                    }

                                    TLEFieldsList xMergedTLEFields = this._inBoundNewTLEFields.mergeInto(this._inBoundOldTLEFields, true);
                                    aSyncAgentRecord.rewind();
                                    aSyncAgentRecord.setLength(0);
                                    TLEFieldsList.writeToWithOrder(xMergedTLEFields, aSyncAgentRecord);
                                 }

                                 aSyncAgentRecord.rewind();
                                 this._inBoundSyncObjectWrapper.setOtaFormOfSyncObject(aSyncAgentRecord);
                              }

                              synchronized (this._listOfIgnoredUidToOperation) {
                                 this._listOfIgnoredUidToOperation.addElement(xRecordUid);
                              }

                              if (this._inBoundSyncObjectWrapper.decode()
                                 && this._syncCollection.updateSyncObject(t, this._inBoundSyncObjectWrapper.getSyncObject())) {
                                 DirtyBits.setClean(this._inBoundSyncObjectWrapper.getSyncObject());
                                 var23 = false;
                                 break label408;
                              }

                              synchronized (this._listOfIgnoredUidToOperation) {
                                 this._listOfIgnoredUidToOperation.removeElement(xRecordUid);
                                 String errorMessage = "Wrapper decode() or SyncCollection.updateSyncObject() failed.";
                                 Logger.logOperationError(operation, xRecordUid, errorMessage);
                                 returnCode = 411;
                                 var23 = false;
                                 break label408;
                              }
                           }

                           returnCode = 410;
                           var23 = false;
                           break label408;
                        } catch (OTASyncCollectionAdapter$InvalidTableIdException var33) {
                        }
                     } catch (Throwable var34) {
                        synchronized (this._listOfIgnoredUidToOperation) {
                           this._listOfIgnoredUidToOperation.removeElement(xRecordUid);
                        }

                        Logger.logOperationError(operation, xRecordUid, t);
                        returnCode = 411;
                        var23 = false;
                        break label407;
                     }

                     Logger.logNotDefinedTableId(4, xTableId);
                     returnCode = 409;
                     var23 = false;
                  } finally {
                     if (var23) {
                        if (this._inBoundSyncObjectWrapper != null) {
                           this._inBoundSyncObjectWrapper.reset(true);
                        }

                        if (this._inBoundNewTLEFields != null) {
                           this._inBoundNewTLEFields.reset();
                        }

                        if (this._inBoundOldTLEFields != null) {
                           this._inBoundOldTLEFields.reset();
                        }
                     }
                  }

                  if (this._inBoundSyncObjectWrapper != null) {
                     this._inBoundSyncObjectWrapper.reset(true);
                  }

                  if (this._inBoundNewTLEFields != null) {
                     this._inBoundNewTLEFields.reset();
                  }

                  if (this._inBoundOldTLEFields != null) {
                     this._inBoundOldTLEFields.reset();
                  }

                  return returnCode;
               }

               if (this._inBoundSyncObjectWrapper != null) {
                  this._inBoundSyncObjectWrapper.reset(true);
               }

               if (this._inBoundNewTLEFields != null) {
                  this._inBoundNewTLEFields.reset();
               }

               if (this._inBoundOldTLEFields != null) {
                  this._inBoundOldTLEFields.reset();
               }

               return returnCode;
            }

            if (this._inBoundSyncObjectWrapper != null) {
               this._inBoundSyncObjectWrapper.reset(true);
            }

            if (this._inBoundNewTLEFields != null) {
               this._inBoundNewTLEFields.reset();
            }

            if (this._inBoundOldTLEFields != null) {
               this._inBoundOldTLEFields.reset();
            }

            return returnCode;
         }

         if (this._inBoundSyncObjectWrapper != null) {
            this._inBoundSyncObjectWrapper.reset(true);
         }

         if (this._inBoundNewTLEFields != null) {
            this._inBoundNewTLEFields.reset();
         }

         if (this._inBoundOldTLEFields != null) {
            this._inBoundOldTLEFields.reset();
         }

         return var41;
      } else {
         return returnCode;
      }
   }

   private final SyncApplicationChangeList createSyncApplicationGroupChangeList() {
      return this._dataSourceDatabase.containsTables() ? new SyncApplicationOptimizedGroupChangeList() : new SyncApplicationUnOptimizedGroupChangeList();
   }

   private final SyncApplicationRecordChange createSyncApplicationRecordChange() {
      return this._dataSourceDatabase.containsTables() ? new SyncApplicationOptimizedRecordChange() : new SyncApplicationRecordChange();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int onFillInRecordChange(SyncApplicationRecordChange aSyncApplicationRecordChange) {
      int xReturnCode = 200;
      int xRecordUid = aSyncApplicationRecordChange.getRecordUid();
      SyncObject xSyncObject = this._syncCollection.getSyncObject(xRecordUid);
      short var15;
      if (xSyncObject == null) {
         var15 = 410;
      } else {
         boolean var10 = false /* VF: Semaphore variable */;

         label137: {
            try {
               label123:
               try {
                  var10 = true;
                  this.makeSyncObjectWrapperReady(false, xRecordUid, aSyncApplicationRecordChange.getTableId());
                  this._outBoundSyncObjectWrapper.setSyncObject(xSyncObject);
                  if (this._outBoundSyncObjectWrapper.convertToSerialForm(true) && this._outBoundSyncObjectWrapper.encode(9)) {
                     byte[] xBuffer = this._dataSourceDatabase.containsTables()
                        ? this._outBoundSyncObjectWrapper.getCopyOfOtaBuffer()
                        : this._outBoundSyncObjectWrapper.getCopyOfSerialBuffer();
                     if (aSyncApplicationRecordChange.getOperation() == 2 || xBuffer != null && xBuffer.length != 0) {
                        aSyncApplicationRecordChange.setTableId((byte)this._outBoundSyncObjectWrapper.getTableId());
                        aSyncApplicationRecordChange.setRecordFields(xBuffer);
                        var15 = 200;
                        var10 = false;
                     } else {
                        String errorMessage = "onFillInRecordChange(): unexpected empty buffer encountered.";
                        Logger.logOperationError(61, xRecordUid, errorMessage);
                        var15 = 411;
                        var10 = false;
                     }
                  } else {
                     String errorMessage = "Wrapper convertToSerialForm() or Wrapper encode() failed.";
                     Logger.logOperationError(61, xRecordUid, errorMessage);
                     var15 = 411;
                     var10 = false;
                  }
                  break label137;
               } catch (Throwable var13) {
                  Logger.logOperationError(61, xRecordUid, t);
                  var15 = 411;
                  var10 = false;
                  break label123;
               }
            } finally {
               if (var10) {
                  if (this._outBoundSyncObjectWrapper != null) {
                     this._outBoundSyncObjectWrapper.reset(false);
                  }
               }
            }

            if (this._outBoundSyncObjectWrapper != null) {
               this._outBoundSyncObjectWrapper.reset(false);
               return var15;
            }

            return var15;
         }

         if (this._outBoundSyncObjectWrapper != null) {
            this._outBoundSyncObjectWrapper.reset(false);
            return var15;
         }
      }

      return var15;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int onFillInChangeList(SyncApplicationChangeList aSyncApplicationChangeList) {
      if (aSyncApplicationChangeList instanceof SyncApplicationGroupChangeList) {
         SyncApplicationGroupChangeList xSyncApplicationGroupChangeList = (SyncApplicationGroupChangeList)aSyncApplicationChangeList;
         int xOperation = xSyncApplicationGroupChangeList.getOperation();
         boolean xIsForSlowSync = xSyncApplicationGroupChangeList.isForSlowSync();
         int[] xListOfUids = xSyncApplicationGroupChangeList.getListOfUids();
         if (xListOfUids != null) {
            int xIndex = xListOfUids.length - 1;

            while (xIndex > -1 && this.isBound()) {
               int xUid = xListOfUids[xIndex];

               label44:
               try {
                  SyncApplicationRecordChange xSyncApplicationRecordChange = this.createSyncApplicationRecordChange();
                  xSyncApplicationRecordChange.shouldBeFilled(true);
                  xSyncApplicationRecordChange.setRecordUid(xUid);
                  xSyncApplicationRecordChange.setOperation(xOperation);
                  xSyncApplicationRecordChange.setForSlowSync(xIsForSlowSync);
                  aSyncApplicationChangeList.add(xSyncApplicationRecordChange);
               } catch (Throwable var10) {
                  Logger.logOperationError(62, xUid, t);
                  break label44;
               }

               xIndex--;
               Thread.yield();
            }
         }
      }

      return 200;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int onGetRecordFromServer(SyncAgentRecord aSyncAgentRecord) {
      int xReturnCode = 200;
      int xRecordUid = aSyncAgentRecord.getUid();
      SyncObject xSyncObject = this._syncCollection.getSyncObject(xRecordUid);
      if (xSyncObject != null) {
         int xUid = xSyncObject.getUID();
         if (this.isValidUid(xUid) && xUid == xRecordUid) {
            try {
               SyncApplicationRecordChange xSyncApplicationRecordChange = this.createSyncApplicationRecordChange();
               xSyncApplicationRecordChange.shouldBeFilled(true);
               xSyncApplicationRecordChange.setOperation(10);
               xSyncApplicationRecordChange.setRecordUid(xRecordUid);
               xSyncApplicationRecordChange.setForSlowSync(aSyncAgentRecord.isForSlowSync());
               this._syncAgentConnection.submit(xSyncApplicationRecordChange, true, this.IsOptimizeChanges());
               return xReturnCode;
            } catch (Throwable var8) {
               Logger.logOperationError(6, xRecordUid, t);
               return 411;
            }
         } else {
            Logger.logInvalidUid(6, this._dataSourceDatabase.getName(), xUid);
            return 411;
         }
      } else {
         return 410;
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int onGetAllRecordsFromServer(SyncAgentRecord aSyncAgentRecord) {
      SyncObject[] xSyncObjects = this._syncCollection.getSyncObjects();
      boolean xForSlowSync = aSyncAgentRecord.isForSlowSync();
      int xSyncObjectIndex = 0;
      int xNumberOfChangePerList = this._syncAgentConnection.getMaxNumberOfChangesPerChangeList();

      while (xSyncObjectIndex < xSyncObjects.length && this.isBound()) {
         int xUid = 0;

         label117:
         try {
            SyncApplicationChangeList xSyncApplicationChangeList = this.createSyncApplicationGroupChangeList();
            xSyncApplicationChangeList.shouldBeFilled(true);
            SyncApplicationGroupChangeList xSyncApplicationGroupChangeList = (SyncApplicationGroupChangeList)xSyncApplicationChangeList;
            xSyncApplicationGroupChangeList.setForSlowSync(xForSlowSync);
            xSyncApplicationGroupChangeList.setOperation(10);
            int xRemaingNumberOfSyncObjects = xSyncObjects.length - xSyncObjectIndex;
            int[] xListOfUids = new int[Math.min(xRemaingNumberOfSyncObjects, xNumberOfChangePerList)];
            int xListOfUidsLength = xListOfUids.length;
            int xIndex = 0;

            do {
               SyncObject xSyncObject = xSyncObjects[xSyncObjectIndex++];
               if (xSyncObject != null && this.isValidUid(xUid = xSyncObject.getUID())) {
                  xListOfUids[xIndex++] = xUid;
                  Thread.yield();
               } else {
                  xListOfUidsLength--;
               }
            } while (xIndex < xListOfUidsLength && this.isBound());

            Array.resize(xListOfUids, xListOfUidsLength);
            xSyncApplicationGroupChangeList.setListOfUids(xListOfUids);
            this._syncAgentConnection.submit(xSyncApplicationChangeList, false);
         } catch (Throwable var19) {
            Logger.logOperationError(7, xUid, t);
            break label117;
         }

         Thread.yield();
      }

      try {
         this._syncAgentConnection.flush();
      } finally {
         return 200;
      }

      return 200;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int onGetAllRecordsHashes(SyncAgentRecordHashesList aList) {
      SyncObject[] xSyncObjects = this._syncCollection.getSyncObjects();
      boolean xUseOrderedHash = this._syncAgentConnection.useOrderedHash();
      this.makeSyncObjectWrapperReady(true, 0, 0);
      int xIndex = xSyncObjects.length - 1;
      ReusableObjectPool xReusableObjectPool = null;

      while (this.isBound() && xIndex > -1) {
         SyncObject xSyncObject = xSyncObjects[xIndex--];
         int xUid = 0;
         if (xSyncObject != null && this.isValidUid(xUid = xSyncObject.getUID())) {
            try {
               this._inBoundSyncObjectWrapper.setSyncObject(xSyncObject);
               if (!this._inBoundSyncObjectWrapper.convertToSerialForm(true) || !this._inBoundSyncObjectWrapper.encode(5 | (xUseOrderedHash ? 16 : 0))) {
                  continue;
               }

               if (xReusableObjectPool == null) {
                  xReusableObjectPool = ReusableObjectPool.getSingletonInstance(-49889245922388290L);
               }

               SyncAgentRecordHashes xSyncAgentRecordHashes = (SyncAgentRecordHashes)xReusableObjectPool.checkOut();
               if (xSyncAgentRecordHashes == null) {
                  xSyncAgentRecordHashes = new SyncAgentRecordHashes();
               }

               xSyncAgentRecordHashes.setUid(xUid);
               xSyncAgentRecordHashes.setKeyFieldsHash(this._inBoundSyncObjectWrapper.getKeyFieldsHash());
               xSyncAgentRecordHashes.setAllFieldsHash(this._inBoundSyncObjectWrapper.getFieldsHash());
               aList.addElement(xSyncAgentRecordHashes);
               this._inBoundSyncObjectWrapper.reset(false);
            } catch (Throwable var10) {
               Logger.logOperationError(60, xUid, t);
               return 411;
            }
         }

         Thread.yield();
      }

      if (this._inBoundSyncObjectWrapper != null) {
         this._inBoundSyncObjectWrapper.reset(true);
      }

      return 200;
   }

   private final boolean IsOptimizeChanges() {
      return this.IsOptimizeChanges(this._syncCollection);
   }

   private final boolean IsOptimizeChanges(Collection collection) {
      if (collection instanceof Object) {
         OTASyncEventOptimizationProvider optimizationProvider = (OTASyncEventOptimizationProvider)collection;
         if (optimizationProvider.getEventOptimizationDisabled()) {
            return false;
         }
      }

      return true;
   }
}
