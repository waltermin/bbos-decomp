package net.rim.device.internal.synchronization.ota.session;

import java.io.IOException;
import java.util.Vector;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.api.util.IntVector;
import net.rim.device.cldc.io.sync.RecordBaseSyncCommand;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.cldc.io.sync.SyncDatagram;
import net.rim.device.cldc.io.sync.command.GetRecordsHashes;
import net.rim.device.cldc.io.sync.command.GetSyncConfiguration;
import net.rim.device.cldc.io.sync.command.InitiateSync;
import net.rim.device.cldc.io.sync.command.Log;
import net.rim.device.cldc.io.sync.command.RecordsHashes;
import net.rim.device.cldc.io.sync.command.Status;
import net.rim.device.cldc.io.sync.command.SyncConfiguration;
import net.rim.device.cldc.io.sync.command.UpdateSyncConfiguration;
import net.rim.device.cldc.io.sync.command.Use;
import net.rim.device.internal.synchronization.ota.api.Logger;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnection;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnections;
import net.rim.device.internal.synchronization.ota.api.SyncAgentGroupOfRecords;
import net.rim.device.internal.synchronization.ota.api.SyncAgentRecordHashes;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatistics;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationChangeStatus;
import net.rim.device.internal.synchronization.ota.api.SyncApplicationRecordChange;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.DataSource;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabase;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.util.ReferenceGenerator;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;

final class DeviceSession extends Session {
   private ReferenceGenerator _referenceGenerator;
   private String _serviceUid;
   private long _sid;
   private ServiceIdentifier _serviceIdentifier;
   private String _userId;
   private Configuration _configuration;
   private boolean _incrementChangeListId;
   private IntHashtable _refIdToSyncAgentConnectionMap;
   private IntHashtable _contextRefIdToSyncAgentConnectionMap;
   private IntIntHashtable _commandIdToRefIdToMap;
   private Vector _syncAgentConnections;
   private SyncAgentConnection _currentSyncAgentConnection;
   private int _currentDataSourceId;
   private Vector _listOfDatagramsToSend;
   private ServicesConfigurationManager _servicesConfigurationManager;
   private Vector _syncAgentConnectionList;
   private int _expectedChangeListIdOnStartup;
   private int _logMessageId;
   private String _logMessage;

   DeviceSession(SessionManager aSessionManager, int aSessionId, int aChangeListId, int aSessionType, Vector syncAgentConnections) {
      super(aSessionManager, aSessionId, aChangeListId);
      this.setType(aSessionType);
      this._servicesConfigurationManager = ServicesConfigurationManager.getSingletonInstance();
      this._serviceUid = aSessionManager.getServiceUid();
      this._sid = aSessionManager.getSid();
      this._serviceIdentifier = aSessionManager.getServiceIdentifier();
      this._userId = aSessionManager.getUserId();
      this._configuration = this._servicesConfigurationManager.getConfiguration(this._sid);
      this._referenceGenerator = ReferenceGenerator.getSingletonInstance();
      this._refIdToSyncAgentConnectionMap = new IntHashtable();
      this._contextRefIdToSyncAgentConnectionMap = new IntHashtable();
      this._commandIdToRefIdToMap = new IntIntHashtable();
      this._incrementChangeListId = true;
      this._syncAgentConnections = syncAgentConnections;
      this._listOfDatagramsToSend = new Vector(0);
   }

   private final void cleanReferences() {
      this._refIdToSyncAgentConnectionMap = null;
      this._commandIdToRefIdToMap = null;
      this._referenceGenerator = null;
      this._syncAgentConnections = null;
      this._configuration = null;
      this._servicesConfigurationManager = null;
      super._sessionManager = null;
      this._currentSyncAgentConnection = null;
      this._listOfDatagramsToSend = null;
   }

   private final void sendDeviceConfigurationChanges() {
      SyncDatagram xSyncDatagram = (SyncDatagram)this.checkOutSyncDatagram();
      xSyncDatagram.setSessionId(this.getSessionId());
      xSyncDatagram.setCurrentChangeListId(this.getChangeListId());
      Vector xDatabaseList = super._sessionManager.getAllDisabledDatabaseList();
      boolean xNeedToSendDatagram = false;
      synchronized (xDatabaseList) {
         Use xUse = (Use)super._syncCommandsPool.checkOut(7);
         UpdateSyncConfiguration xUpdateSyncConfiguration = (UpdateSyncConfiguration)super._syncCommandsPool.checkOut(18);

         for (int xIndex = xDatabaseList.size() - 1; xIndex > -1; xIndex--) {
            label79:
            try {
               SyncAgentUrl xSyncAgentUrl = (SyncAgentUrl)xDatabaseList.elementAt(xIndex);
               DataSource xDataSource = this._configuration.getDataSourceBy(xSyncAgentUrl.getDataSourceName());
               DataSourceDatabase xDataSourceDatabase = xDataSource.getDataSourceDatabaseBy(xSyncAgentUrl.getDatabaseName(), xSyncAgentUrl.getVersion());
               if (xDataSourceDatabase != null) {
                  xDataSource.getDataSourceDatabases();
                  xUse.setDataSourceId(xDataSource.getId());
                  if (!xDataSourceDatabase.isGeneric() && !xDataSourceDatabase.useExplicitDatabaseName()) {
                     xUse.setDatabaseId(xDataSourceDatabase.getId());
                  } else {
                     xUse.setDatabaseName(xSyncAgentUrl.getDatabaseName());
                  }

                  byte[] xBuffer = new byte[]{11, 1, 0};
                  xUpdateSyncConfiguration.setConfiguration(xBuffer);
                  xSyncDatagram.addCommand(xUse);
                  xSyncDatagram.addCommand(xUpdateSyncConfiguration);
                  xNeedToSendDatagram = true;
               }
            } finally {
               break label79;
            }

            xUse.reset();
            xUpdateSyncConfiguration.reset();
         }

         super._syncCommandsPool.checkIn(xUse);
         super._syncCommandsPool.checkIn(xUpdateSyncConfiguration);
         xDatabaseList.removeAllElements();
      }

      int _currentExpectedChangeListId = super._sessionManager.getExpectedChangeListId();
      if (!xNeedToSendDatagram || _currentExpectedChangeListId != this._expectedChangeListIdOnStartup) {
         this.abort();
      }

      if (this.getState() != 9) {
         xSyncDatagram.setExpectedChangeListId(_currentExpectedChangeListId);
         this.sendDatagram(xSyncDatagram, true, true);
      } else {
         this.checkInSyncDatagram(xSyncDatagram);
      }

      Logger.logStartEndSession(this._serviceUid, this._userId, super._sessionId, super._type, super._changeListId, super._state, super._timeout, true, null);
   }

   private final void sendSession() {
      SyncDatagram xSyncDatagram = (SyncDatagram)this.checkOutSyncDatagram();
      xSyncDatagram.setSessionId(this.getSessionId());
      xSyncDatagram.setCurrentChangeListId(this.getChangeListId());
      SyncCommand xSyncCommand = null;
      switch (super._type) {
         case 1:
            GetSyncConfiguration xGetSyncConfiguration = (GetSyncConfiguration)super._syncCommandsPool.checkOut(9);
            xGetSyncConfiguration.setDeviceCapabilities(this._configuration.getDeviceCapabilities());
            xSyncCommand = xGetSyncConfiguration;
            break;
         case 4:
            xSyncCommand = super._syncCommandsPool.checkOut(13);
            break;
         case 5:
            xSyncCommand = super._syncCommandsPool.checkOut(12);
            break;
         case 7:
            Log xLogSyncCommand = (Log)super._syncCommandsPool.checkOut(19);
            xLogSyncCommand.setLogMessageId(this._logMessageId);
            xLogSyncCommand.setLogMessage(this._logMessage);
            xSyncCommand = xLogSyncCommand;
      }

      if (xSyncCommand != null) {
         xSyncDatagram.addCommand(xSyncCommand);
         super._syncCommandsPool.checkIn(xSyncCommand);
         int _currentExpectedChangeListId = super._sessionManager.getExpectedChangeListId();
         if (_currentExpectedChangeListId != this._expectedChangeListIdOnStartup) {
            this.abort();
         }

         if (this.getState() != 9) {
            xSyncDatagram.setExpectedChangeListId(_currentExpectedChangeListId);
            this.sendDatagram(xSyncDatagram, true, true);
            if (super._type == 1) {
               SyncAgent xSyncAgent = SyncAgent.getSingletonInstance();
               xSyncAgent.notifyListenersWith(25, this._serviceIdentifier);
            }
         }
      } else {
         this.abort();
      }

      Logger.logStartEndSession(this._serviceUid, this._userId, super._sessionId, super._type, super._changeListId, super._state, super._timeout, true, null);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendInitializationRequest() throws Throwable {
      SyncDatagram xSyncDatagram = (SyncDatagram)this.checkOutSyncDatagram();
      xSyncDatagram.setSessionId(this.getSessionId());
      xSyncDatagram.setCurrentChangeListId(this.getChangeListId());
      xSyncDatagram.setCurrentSequenceNumber(super._startOfSendingWindow);
      int xCurrentDataSourceId = 0;
      int xSyncAgentConnectionsSize = this._syncAgentConnections.size();
      Use xUse = (Use)super._syncCommandsPool.checkOut(7);
      InitiateSync xInitiateSync = (InitiateSync)super._syncCommandsPool.checkOut(11);

      for (int xConnectionIndex = 0; xConnectionIndex < xSyncAgentConnectionsSize && this.getState() != 9; xConnectionIndex++) {
         SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)this._syncAgentConnections.elementAt(xConnectionIndex);
         boolean var23 = false /* VF: Semaphore variable */;

         try {
            var23 = true;
            xSyncAgentConnection.onSessionEvent(51, null);
            xSyncAgentConnection.buildGroups();
            var23 = false;
         } finally {
            if (var23) {
               xSyncAgentConnection.onSessionEvent(52, null);
            }
         }

         xSyncAgentConnection.onSessionEvent(52, null);
         if (this.getState() == 9) {
            return;
         }

         if (!xSyncAgentConnection.isClosed()) {
            SyncAgentUrl xSyncAgentUrl = xSyncAgentConnection.getUrl();
            String xDataSourceName = xSyncAgentUrl.getDataSourceName();
            String xDatabaseName = xSyncAgentUrl.getDatabaseName();
            DataSource xDataSource = this._configuration.getDataSourceBy(xDataSourceName);
            DataSourceDatabase xDataSourceDatabase = xSyncAgentConnection.getDataSourceDatabase();
            int xDataSourceId = xDataSource.getId();
            if (xCurrentDataSourceId != xDataSourceId) {
               xUse.setDataSourceId(xDataSourceId);
               xCurrentDataSourceId = xDataSourceId;
            }

            if (!xDataSourceDatabase.isGeneric() && !xDataSourceDatabase.useExplicitDatabaseName()) {
               xUse.setDatabaseId(xDataSourceDatabase.getId());
            } else {
               xUse.setDatabaseName(xDatabaseName);
            }

            xUse.setDatabaseVersion(xSyncAgentConnection.getDatabaseVersion());
            int xRefId = this._referenceGenerator.getNegativeRefID();
            this._refIdToSyncAgentConnectionMap.put(xRefId, xSyncAgentConnection);
            xSyncDatagram.addCommand(xUse);
            this._commandIdToRefIdToMap.put(xUse.getId(), xRefId);
            xInitiateSync.setPriority(xSyncAgentConnection.getPacketLevelPriority());
            if (xDataSourceDatabase.sendWithInitialize()
               || SyncAgent.getSingletonInstance().isUsedByOtherSyncSources(xSyncAgentUrl.getSid(), xSyncAgentUrl.getDatabaseName())) {
               if (!this._configuration.isDeleteOnSlowSyncEnabled()) {
                  xDataSourceDatabase.setDeleteOnSlowSync(false);
               }

               xInitiateSync.setDataSourceDatabase(xDataSourceDatabase);
            }

            SyncAgentGroupOfRecords[] xGroups = xSyncAgentConnection.getGroups();
            if (xGroups != null) {
               for (int xIndex = xGroups.length - 1; xIndex > -1; xIndex--) {
                  SyncAgentGroupOfRecords xSyncAgentGroupOfRecords = xGroups[xIndex];
                  if (xSyncAgentGroupOfRecords != null) {
                     xInitiateSync.addGroupHashValue(xSyncAgentGroupOfRecords);
                  }

                  if (this.getState() == 9) {
                     return;
                  }
               }
            }

            xRefId = this._referenceGenerator.getNegativeRefID();
            this._refIdToSyncAgentConnectionMap.put(xRefId, xSyncAgentConnection);
            xSyncDatagram.addCommand(xInitiateSync);
            this._commandIdToRefIdToMap.put(xInitiateSync.getId(), xRefId);
            xUse.reset();
            xInitiateSync.reset();
            SyncAgent.getSingletonInstance().notifyListenersWith(19, xSyncAgentUrl);
         }
      }

      super._syncCommandsPool.checkIn(xUse);
      super._syncCommandsPool.checkIn(xInitiateSync);
      int _currentExpectedChangeListId = super._sessionManager.getExpectedChangeListId();
      if (_currentExpectedChangeListId != this._expectedChangeListIdOnStartup) {
         this.abort();
      }

      if (this.getState() != 9) {
         boolean var20 = false /* VF: Semaphore variable */;

         try {
            var20 = true;
            xSyncDatagram.setExpectedChangeListId(_currentExpectedChangeListId);
            this.sendDatagram(xSyncDatagram, true, true);
            var20 = false;
         } finally {
            if (var20) {
               for (int xConnectionIndex = 0; xConnectionIndex < xSyncAgentConnectionsSize; xConnectionIndex++) {
                  SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)this._syncAgentConnections.elementAt(xConnectionIndex);
                  xSyncAgentConnection.destroyGroups();
               }
            }
         }

         Logger.logStartEndSession(this._serviceUid, this._userId, super._sessionId, super._type, super._changeListId, super._state, super._timeout, true, null);
      }
   }

   private final void addSyncCommandToSyncDatagram(
      SyncCommand aSyncCommand, SyncAgentConnection aSyncAgentConnection, SyncDatagram aSyncDatagram, boolean generateRefId
   ) {
      int xRefId;
      if (generateRefId) {
         xRefId = this._referenceGenerator.getNegativeRefID();
      } else {
         xRefId = aSyncCommand.getId();
      }

      if (aSyncCommand instanceof Use) {
         this._contextRefIdToSyncAgentConnectionMap.put(xRefId, aSyncAgentConnection);
      }

      this._refIdToSyncAgentConnectionMap.put(xRefId, aSyncAgentConnection);
      aSyncDatagram.addCommand(aSyncCommand);
      int xAbsoluteCommandId = aSyncDatagram.getCurrentSequenceNumber() << 16 | aSyncCommand.getId();
      this._commandIdToRefIdToMap.put(xAbsoluteCommandId, xRefId);
   }

   private final void sendPendingChanges() throws IOException {
      Vector xDataSourceNames = this._configuration.getDataSourceNames();
      if (xDataSourceNames.isEmpty()) {
         throw new IOException();
      }

      int xDataSourceNamesListSize = xDataSourceNames.size();
      SyncDatagram xSyncDatagram = null;
      Use xUse = (Use)super._syncCommandsPool.checkOut(7);

      for (int xDataSourceNameIndex = 0; xDataSourceNameIndex < xDataSourceNamesListSize; xDataSourceNameIndex++) {
         String xDataSourceName = (String)xDataSourceNames.elementAt(xDataSourceNameIndex);
         this._syncAgentConnectionList = SyncAgentConnections.getAllConnectionsWithPendingChangesFor(
            this._syncAgentConnectionList, this._sid, xDataSourceName, null
         );
         int xNumberOfConnections = this._syncAgentConnectionList.size();
         DataSource xDataSource = this._configuration.getDataSourceBy(xDataSourceName);
         int xDataSourceId = xDataSource.getId();
         boolean xSetDataSource = true;

         for (int xSyncAgentConnectionIndex = 0; xSyncAgentConnectionIndex < xNumberOfConnections; xSyncAgentConnectionIndex++) {
            SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)this._syncAgentConnectionList.elementAt(xSyncAgentConnectionIndex);
            IntHashtable xTablesToChangesMap = xSyncAgentConnection.getPendingChanges();
            if (xTablesToChangesMap != null) {
               boolean xSetDatabase = true;
               IntEnumeration xTablesList = xTablesToChangesMap.keys();
               SyncAgentUrl xSyncAgentUrl = xSyncAgentConnection.getUrl();
               DataSourceDatabase xDataSourceDatabase = xSyncAgentConnection.getDataSourceDatabase();

               while (xTablesList.hasMoreElements()) {
                  boolean xSetDatabaseTable = true;
                  int xTableId = xTablesList.nextElement();
                  Vector xListOfChanges = (Vector)xTablesToChangesMap.get(xTableId);
                  int xNumberOfChanges = xListOfChanges != null ? xListOfChanges.size() : 0;
                  if (xNumberOfChanges != 0) {
                     Logger.logNumberOfCommandsScaned(xSyncAgentUrl.getDatabaseName(), xNumberOfChanges);

                     for (int xIndex = 0; xIndex < xNumberOfChanges; xUse.reset()) {
                        if (xSyncDatagram == null) {
                           xSyncDatagram = (SyncDatagram)this.checkOutSyncDatagram();
                           xSyncDatagram.setCurrentSequenceNumber(super._startOfSendingWindow + this._listOfDatagramsToSend.size());
                           this._listOfDatagramsToSend.addElement(xSyncDatagram);
                           xSetDataSource = true;
                           xSetDatabase = true;
                           xSetDatabaseTable = true;
                        }

                        if (xSetDataSource) {
                           xUse.setDataSourceId(xDataSourceId);
                           xSetDataSource = false;
                        }

                        if (xSetDatabase) {
                           String xDatabaseName = xSyncAgentUrl.getDatabaseName();
                           if (!xDataSourceDatabase.isGeneric() && !xDataSourceDatabase.useExplicitDatabaseName()) {
                              xUse.setDatabaseId(xDataSourceDatabase.getId());
                           } else {
                              xUse.setDatabaseName(xDatabaseName);
                           }

                           xUse.setDatabaseVersion(xSyncAgentConnection.getDatabaseVersion());
                           xSetDatabase = false;
                        }

                        if (xSetDatabaseTable) {
                           if (xTableId != 0) {
                              xUse.setTableId(xTableId);
                           } else {
                              xSetDatabaseTable = false;
                           }
                        }

                        boolean xAddUseCommand = true;

                        do {
                           SyncCommand xSyncCommand = this.convertSyncApplicationRecordChangeToSyncCommand(
                              (SyncApplicationRecordChange)xListOfChanges.elementAt(xIndex)
                           );
                           if (!xAddUseCommand) {
                              if (!xSyncDatagram.couldAddSyncCommand(xSyncCommand)) {
                                 xSyncDatagram = null;
                                 break;
                              }
                           } else {
                              if (!xSyncDatagram.couldAddSyncCommands(xUse, xSyncCommand)) {
                                 if (xSyncDatagram.getNumberOfSyncCommandsProccessed() == 0) {
                                    SyncCommand[] xFragmentedSyncCommand = xSyncCommand.fragment(super._syncCommandsPool, 16384);

                                    for (int xFragmentIndex = 0; xFragmentIndex < xFragmentedSyncCommand.length; xFragmentIndex++) {
                                       if (xSyncDatagram == null) {
                                          xSyncDatagram = (SyncDatagram)this.checkOutSyncDatagram();
                                          xSyncDatagram.setCurrentSequenceNumber(super._startOfSendingWindow + this._listOfDatagramsToSend.size());
                                          this._listOfDatagramsToSend.addElement(xSyncDatagram);
                                       }

                                       this.addSyncCommandToSyncDatagram(xUse, xSyncAgentConnection, xSyncDatagram, true);
                                       this.addSyncCommandToSyncDatagram(xFragmentedSyncCommand[xFragmentIndex], xSyncAgentConnection, xSyncDatagram, false);
                                       xSyncDatagram = null;
                                    }

                                    xIndex++;
                                 }

                                 xSyncDatagram = null;
                                 break;
                              }

                              this.addSyncCommandToSyncDatagram(xUse, xSyncAgentConnection, xSyncDatagram, true);
                              xAddUseCommand = false;
                           }

                           this.addSyncCommandToSyncDatagram(xSyncCommand, xSyncAgentConnection, xSyncDatagram, false);
                           super._syncCommandsPool.checkIn(xSyncCommand);
                        } while (++xIndex < xNumberOfChanges);
                     }
                  }
               }
            }
         }
      }

      super._syncCommandsPool.checkIn(xUse);
      int xNumberOdDatagramsToSent = this._listOfDatagramsToSend.size();
      if (xNumberOdDatagramsToSent == 0) {
         this.abort();
      } else {
         int xLastDatagramIndex = xNumberOdDatagramsToSent - 1;

         for (int xDatagramIndex = 0; xDatagramIndex <= xLastDatagramIndex && this.getState() != 9; xDatagramIndex++) {
            xSyncDatagram = (SyncDatagram)this._listOfDatagramsToSend.elementAt(xDatagramIndex);
            xSyncDatagram.setSessionId(this.getSessionId());
            xSyncDatagram.setCurrentChangeListId(this.getChangeListId());
            int xExpectedChangeListId = super._sessionManager.getExpectedChangeListId();
            if (xExpectedChangeListId != this._expectedChangeListIdOnStartup) {
               this.abort();
               break;
            }

            xSyncDatagram.setExpectedChangeListId(xExpectedChangeListId);
         }

         if (this.getState() != 9) {
            this.sendDatagrams(this._listOfDatagramsToSend, true);
         }

         Logger.logStartEndSession(this._serviceUid, this._userId, super._sessionId, super._type, super._changeListId, super._state, super._timeout, true, null);
      }
   }

   public final void setLogMessageId(int logMessageId) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void setLogMessage(String logMessage) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   // $VF: Could not inline inconsistent finally blocks
   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void onDatagramReceived(SyncDatagram aSyncDatagram) {
      SyncCommand xSyncCommand = null;
      this.goIntoProcessingState();
      boolean xConsiderStatus = true;
      boolean xShouldExecuteCommndsBasedOnUseCommand = false;

      label210: {
         while (true) {
            label207: {
               label218: {
                  if (this.getState() != 9) {
                     boolean var13 = false /* VF: Semaphore variable */;
                     boolean var17 = false /* VF: Semaphore variable */;

                     label203: {
                        try {
                           label201:
                           try {
                              var17 = true;
                              var13 = true;
                              xSyncCommand = aSyncDatagram.checkOutSyncCommand();
                              if (xSyncCommand == null) {
                                 if (!xConsiderStatus) {
                                    var13 = false;
                                    var17 = false;
                                    break label203;
                                 }

                                 xSyncCommand = super._syncCommandsPool.checkOut(8);
                              }

                              if (xSyncCommand instanceof Status) {
                                 this.execute((Status)xSyncCommand);
                                 var13 = false;
                                 var17 = false;
                                 break label210;
                              }

                              if (xSyncCommand instanceof SyncConfiguration) {
                                 this.execute((SyncConfiguration)xSyncCommand);
                                 var13 = false;
                                 var17 = false;
                                 break;
                              }

                              if (xSyncCommand instanceof Use) {
                                 xShouldExecuteCommndsBasedOnUseCommand = this.execute((Use)xSyncCommand) == 200;
                                 var13 = false;
                                 var17 = false;
                              } else if (xSyncCommand instanceof GetRecordsHashes) {
                                 xConsiderStatus = false;
                                 if (xShouldExecuteCommndsBasedOnUseCommand) {
                                    this.execute((GetRecordsHashes)xSyncCommand);
                                    var13 = false;
                                    var17 = false;
                                 } else {
                                    var13 = false;
                                    var17 = false;
                                 }
                              } else {
                                 var13 = false;
                                 var17 = false;
                              }
                              break label218;
                           } finally {
                              if (var17) {
                                 this.abort();
                                 var13 = false;
                                 break label201;
                              }
                           }
                        } finally {
                           if (var13) {
                              aSyncDatagram.checkInSyncCommand(xSyncCommand);
                           }
                        }

                        aSyncDatagram.checkInSyncCommand(xSyncCommand);
                        break label207;
                     }

                     aSyncDatagram.checkInSyncCommand(xSyncCommand);
                  }

                  try {
                     this.sendDatagrams(this._listOfDatagramsToSend, false);
                     this.goIntoRunningState();
                     return;
                  } catch (Throwable var18) {
                     Logger.logErrorMessage(this._serviceUid, this._userId, super._sessionId, super._type, super._changeListId, super._state, t);
                     this.abort();
                     return;
                  }
               }

               aSyncDatagram.checkInSyncCommand(xSyncCommand);
            }

            Thread.yield();
         }

         aSyncDatagram.checkInSyncCommand(xSyncCommand);
         return;
      }

      aSyncDatagram.checkInSyncCommand(xSyncCommand);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void execute(SyncConfiguration aSyncConfiguration) {
      boolean var5 = false /* VF: Semaphore variable */;

      Configuration xConfiguration;
      label31:
      try {
         var5 = true;
         xConfiguration = Configuration.create(aSyncConfiguration.getConfiguration(), this._sid);
         this._servicesConfigurationManager.setConfiguration(xConfiguration);
         var5 = false;
      } finally {
         if (var5) {
            xConfiguration = this._servicesConfigurationManager.getConfiguration(this._sid);
            xConfiguration.setUserEnabled(false);
            break label31;
         }
      }

      SyncAgent xSyncAgent = SyncAgent.getSingletonInstance();
      if (xConfiguration.isUserPreferenceToSyncSet()) {
         xSyncAgent.notifyListenersWith(2, this._serviceIdentifier);
         xSyncAgent.notifyListenersWith(1, this._serviceIdentifier);
         xSyncAgent.notifyListenersWith(xConfiguration.isUserEnabled() ? 23 : 24, this._serviceIdentifier);
      }

      xSyncAgent.notifyListenersWith(26, this._serviceIdentifier);
      this.succeed();
   }

   private final void disableUser() {
      this._configuration.setUserEnabled(false);
      SyncAgent xSyncAgent = SyncAgent.getSingletonInstance();
      xSyncAgent.notifyListenersWith(2, this._serviceIdentifier);
   }

   private final void execute(Status aStatus) {
      int xSessionErrorCode = aStatus.getSessionErrorCode();
      if (xSessionErrorCode != 200) {
         switch (xSessionErrorCode) {
            case 404:
               Logger.logInvalidSyncState(this._serviceUid, this._userId, super._sessionId, super._changeListId);
               this.invalidSyncState();
               return;
            case 418:
               this.abort();
               return;
            case 421:
               this.resetSyncState();
               return;
            default:
               aStatus.reset();
               aStatus.setSessionErrorCode(200);
         }
      }

      if (aStatus.containsInvalidDagaramError()) {
         this.disableUser();
         this.abort();
      } else {
         switch (this.getType()) {
            case 0:
               break;
            case 1:
            default:
               this.handleConfigurationSessionStatus(aStatus);
               break;
            case 2:
               this.handleInitializationSessionStatus(aStatus);
               break;
            case 3:
               this.handleDeviceChangesSessionStatus(aStatus);
               break;
            case 4:
            case 5:
               this.handleSuspendResumeSessionStatus(aStatus);
         }

         this.succeed();
      }
   }

   private final int execute(Use aUse) {
      int xReturnCode = 200;
      if (aUse.isForDataSource()) {
         this._currentDataSourceId = aUse.getDataSourceId();
      }

      if (!aUse.isForDatabase()) {
         return 411;
      }

      DataSource xDataSource = this._configuration.getDataSourceBy(this._currentDataSourceId);
      if (xDataSource == null) {
         return 411;
      }

      String xDataSourceName = xDataSource.getName();
      String xDatabaseName;
      if (aUse.hasExplictDatabaseName()) {
         xDatabaseName = aUse.getDatabaseName();
      } else {
         xDatabaseName = xDataSource.getDataSourceDatabaseNameFor(aUse.getDatabaseId());
      }

      this._syncAgentConnectionList = SyncAgentConnections.getAllConnectionsBy(this._syncAgentConnectionList, this._sid, xDataSourceName, xDatabaseName);
      if (this._syncAgentConnectionList.isEmpty()) {
         int var6 = 411;
         this._currentSyncAgentConnection = null;
         return var6;
      } else {
         this._currentSyncAgentConnection = (SyncAgentConnection)this._syncAgentConnectionList.firstElement();
         return xReturnCode;
      }
   }

   private final int execute(GetRecordsHashes aGetRecordHashes) throws IOException {
      int xReturnCode = 200;
      IntVector xListOfGroupIds = aGetRecordHashes.getGroupsIds();
      if (!xListOfGroupIds.isEmpty()) {
         int xGroupIndex = xListOfGroupIds.size() - 1;
         SyncDatagram xSyncDatagram = null;
         if (!this._listOfDatagramsToSend.isEmpty()) {
            xSyncDatagram = (SyncDatagram)this._listOfDatagramsToSend.lastElement();
         }

         Use xUse = (Use)super._syncCommandsPool.checkOut(7);
         RecordsHashes xRecordsHashes = null;
         boolean xPrepareUseCommand = true;
         int xUseCommandSize = 0;

         while (xGroupIndex > -1) {
            int xGroupId = xListOfGroupIds.elementAt(xGroupIndex);
            SyncAgentGroupOfRecords xSyncAgentGroupOfRecords = this._currentSyncAgentConnection.getGroup(xGroupId);
            if (xSyncAgentGroupOfRecords != null) {
               int xRecordHashesIndex = xSyncAgentGroupOfRecords.size() - 1;

               while (xRecordHashesIndex > -1) {
                  if (xSyncDatagram == null) {
                     xSyncDatagram = (SyncDatagram)this.checkOutSyncDatagram();
                     xSyncDatagram.setSessionId(this.getSessionId());
                     xSyncDatagram.setCurrentChangeListId(this.getChangeListId());
                     xSyncDatagram.setExpectedChangeListId(super._sessionManager.getExpectedChangeListId());
                     xSyncDatagram.setCurrentSequenceNumber(super._startOfSendingWindow + this._listOfDatagramsToSend.size());
                     this._listOfDatagramsToSend.addElement(xSyncDatagram);
                  }

                  if (xPrepareUseCommand) {
                     xUse.setDataSourceId(this._currentDataSourceId);
                     DataSourceDatabase xDataSourceDatabase = this._currentSyncAgentConnection.getDataSourceDatabase();
                     if (!xDataSourceDatabase.isGeneric() && !xDataSourceDatabase.useExplicitDatabaseName()) {
                        xUse.setDatabaseId(xDataSourceDatabase.getId());
                     } else {
                        xUse.setDatabaseName(this._currentSyncAgentConnection.getUrl().getDatabaseName());
                     }

                     xPrepareUseCommand = false;
                     xUseCommandSize = xUse.size();
                  }

                  if (xRecordsHashes == null) {
                     xRecordsHashes = (RecordsHashes)super._syncCommandsPool.checkOut(17);
                  }

                  xRecordsHashes.addRecordHashes((SyncAgentRecordHashes)xSyncAgentGroupOfRecords.elementAt(xRecordHashesIndex));
                  if (xSyncDatagram.getAvailableBytes() - (xUseCommandSize + xRecordsHashes.size()) < 0) {
                     xSyncDatagram.addCommand(xUse);
                     xUse.reset();
                     xPrepareUseCommand = true;
                     xRecordsHashes.removeLastRecordHashes();
                     xSyncDatagram.addCommand(xRecordsHashes);
                     xRecordsHashes.reset();
                     xSyncDatagram = null;
                  } else {
                     xRecordHashesIndex--;
                  }
               }

               xGroupIndex--;
               Thread.yield();
            }
         }

         if (xRecordsHashes == null) {
            throw new IOException();
         }

         if (xSyncDatagram != null) {
            xSyncDatagram.addCommand(xUse);
            xSyncDatagram.addCommand(xRecordsHashes);
         }

         super._syncCommandsPool.checkIn(xUse);
         super._syncCommandsPool.checkIn(xRecordsHashes);
      }

      return xReturnCode;
   }

   private final void handleConfigurationSessionStatus(Status aStatus) {
      SyncAgent.getSingletonInstance().notifyListenersWith(26, this._serviceIdentifier);
   }

   private final void handleSuspendResumeSessionStatus(Status aStatus) {
   }

   private final void handleDeviceChangesSessionStatus(Status aStatus) {
      IntHashtable xIgnoredRefIds = new IntHashtable(0);
      int xEventId = -1;
      int xErrorCode = -1;
      String nullObj = "";
      IntIntHashtable xCommandIdToErrorCodeMap = aStatus.getCommandsErrorCodes();
      IntEnumeration xCommandIdsList = this._commandIdToRefIdToMap.keys();
      ReusableObjectPool xSyncApplicationChangeStatusPool = ReusableObjectPool.getSingletonInstance(5044400588884437613L);
      SyncApplicationChangeStatus xSyncApplicationChangeStatus = (SyncApplicationChangeStatus)xSyncApplicationChangeStatusPool.checkOut();
      if (xSyncApplicationChangeStatus == null) {
         xSyncApplicationChangeStatus = new SyncApplicationChangeStatus();
      }

      while (true) {
         SyncAgentConnection xSyncAgentConnection;
         int xRefId;
         label81:
         while (true) {
            if (!xCommandIdsList.hasMoreElements()) {
               xSyncApplicationChangeStatusPool.checkIn(xSyncApplicationChangeStatus);
               return;
            }

            int xCommandId = xCommandIdsList.nextElement();
            xRefId = this._commandIdToRefIdToMap.get(xCommandId);
            xSyncAgentConnection = (SyncAgentConnection)this._refIdToSyncAgentConnectionMap.get(xRefId);
            if (!xSyncAgentConnection.isClosed() && !xIgnoredRefIds.containsKey(xRefId)) {
               if (xCommandIdToErrorCodeMap == null || !xCommandIdToErrorCodeMap.containsKey(xCommandId)) {
                  xErrorCode = 200;
                  var16 = 8;
                  break;
               }

               xErrorCode = xCommandIdToErrorCodeMap.get(xCommandId);
               switch (xErrorCode) {
                  case 405:
                  case 406:
                  case 412:
                     xSyncAgentConnection.getDataSourceDatabase().setServerEnabled(false);
                     xSyncAgentConnection.close(true);
                     break;
                  case 420:
                     super._operationReTryRequested = true;
                     if (this._contextRefIdToSyncAgentConnectionMap.containsKey(xRefId)) {
                        IntEnumeration xPossibleCommandIdsTobeIgnored = this._commandIdToRefIdToMap.keys();

                        while (xPossibleCommandIdsTobeIgnored.hasMoreElements()) {
                           int xTempCommandId = xPossibleCommandIdsTobeIgnored.nextElement();
                           if (xSyncAgentConnection.equals(this._refIdToSyncAgentConnectionMap.get(this._commandIdToRefIdToMap.get(xTempCommandId)))) {
                              int xTempRefId = this._commandIdToRefIdToMap.get(xTempCommandId);
                              if (this._commandIdToRefIdToMap.containsKey(xTempCommandId)) {
                                 xIgnoredRefIds.put(xTempRefId, nullObj);
                                 xSyncApplicationChangeStatus.setContextual(true);
                              }
                           }
                        }

                        if (!xSyncApplicationChangeStatus.isContextual()) {
                           break;
                        }
                     }

                     var16 = 8;
                     if ((
                           xSyncApplicationChangeStatus.isContextual()
                              || xSyncAgentConnection.getRetryCountFor(xRefId) + 1 < this._configuration.getNumberOfOperationRetries()
                        )
                        && (
                           !xSyncApplicationChangeStatus.isContextual()
                              || xSyncAgentConnection.getContextualRetryCount() + 1 < this._configuration.getNumberOfOperationRetries()
                        )) {
                        break label81;
                     }

                     if (!xSyncApplicationChangeStatus.isContextual()
                        && xSyncAgentConnection.getRetrySuspensionCount(xRefId) + 1 >= this._configuration.getNumberOfOperationRetrySuspensions()) {
                        var16 = 67;
                        xErrorCode = 67;
                     } else {
                        var16 = 65;
                        xErrorCode = 65;
                     }

                     super._operationReTryRequested = false;
                     break label81;
                  default:
                     var16 = 8;
                     break label81;
               }
            }
         }

         if (var16 < 0 || xErrorCode < 0) {
            throw new IllegalArgumentException();
         }

         xSyncApplicationChangeStatus.setRefId(xRefId);
         xSyncApplicationChangeStatus.setStatus(xErrorCode);
         xSyncAgentConnection.onSessionEvent(var16, xSyncApplicationChangeStatus);
         Thread.yield();
      }
   }

   private final void handleInitializationSessionStatus(Status aStatus) {
      IntIntHashtable xCommandIdToErrorCodeMap = aStatus.getCommandsErrorCodes();
      IntIntHashtable xCommandIdToNofPOpsMap = aStatus.getPendingNumberOfOperationsMap();
      SyncAgent xSyncAgent = SyncAgent.getSingletonInstance();

      for (IntEnumeration xCommandIdsList = this._commandIdToRefIdToMap.keys(); xCommandIdsList.hasMoreElements(); Thread.yield()) {
         int xCommandId = xCommandIdsList.nextElement();
         int xRefId = this._commandIdToRefIdToMap.get(xCommandId);
         SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)this._refIdToSyncAgentConnectionMap.get(xRefId);
         if (!xSyncAgentConnection.isClosed()) {
            SyncAgentUrl xSyncAgentUrl = xSyncAgentConnection.getUrl();
            if (xCommandIdToErrorCodeMap != null && xCommandIdToErrorCodeMap.containsKey(xCommandId)) {
               int xErrorCode = xCommandIdToErrorCodeMap.get(xCommandId);
               if (xErrorCode != 420) {
                  xSyncAgentConnection.getDataSourceDatabase().setServerEnabled(false);
                  xSyncAgentConnection.close(true);
               } else {
                  xSyncAgentConnection.onSessionEvent(57, null);
                  super._operationReTryRequested = true;
               }
            } else if (!xSyncAgentConnection.isInitialized()) {
               xSyncAgentConnection.destroyGroups();
               xSyncAgentConnection.onSessionEvent(55, null);
               xSyncAgent.notifyListenersWith(20, xSyncAgentUrl);
            } else {
               int xRemaingNumberOfOperations = 0;
               SyncAgentStatistics xSyncAgentStatistics = xSyncAgentConnection.getSyncAgentStatistics();
               synchronized (xSyncAgentStatistics) {
                  xSyncAgentStatistics.reset();
                  if (xCommandIdToNofPOpsMap != null && xCommandIdToNofPOpsMap.containsKey(xCommandId)) {
                     xRemaingNumberOfOperations = xCommandIdToNofPOpsMap.get(xCommandId);
                  }

                  xSyncAgentStatistics.setRemainingNumberOfOperations(xRemaingNumberOfOperations, this.getSessionId());
               }
            }
         }
      }
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush 1
      // 02: invokevirtual java/lang/Thread.setPriority (I)V
      // 05: aload 0
      // 06: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.goIntoRunningState ()V
      // 09: bipush 0
      // 0a: istore 1
      // 0b: aload 0
      // 0c: aload 0
      // 0d: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionManager Lnet/rim/device/internal/synchronization/ota/session/SessionManager;
      // 10: invokevirtual net/rim/device/internal/synchronization/ota/session/SessionManager.getExpectedChangeListId ()I
      // 13: putfield net/rim/device/internal/synchronization/ota/session/DeviceSession._expectedChangeListIdOnStartup I
      // 16: aload 0
      // 17: getfield net/rim/device/internal/synchronization/ota/session/Session._type I
      // 1a: tableswitch 46 0 7 71 46 53 60 46 46 67 46
      // 48: aload 0
      // 49: invokespecial net/rim/device/internal/synchronization/ota/session/DeviceSession.sendSession ()V
      // 4c: goto 61
      // 4f: aload 0
      // 50: invokespecial net/rim/device/internal/synchronization/ota/session/DeviceSession.sendInitializationRequest ()V
      // 53: goto 61
      // 56: aload 0
      // 57: invokespecial net/rim/device/internal/synchronization/ota/session/DeviceSession.sendPendingChanges ()V
      // 5a: goto 61
      // 5d: aload 0
      // 5e: invokespecial net/rim/device/internal/synchronization/ota/session/DeviceSession.sendDeviceConfigurationChanges ()V
      // 61: aload 0
      // 62: invokespecial net/rim/device/internal/synchronization/ota/session/Session.run ()V
      // 65: goto a9
      // 68: astore 2
      // 69: aload 0
      // 6a: getfield net/rim/device/internal/synchronization/ota/session/DeviceSession._serviceUid Ljava/lang/String;
      // 6d: aload 0
      // 6e: getfield net/rim/device/internal/synchronization/ota/session/DeviceSession._userId Ljava/lang/String;
      // 71: aload 0
      // 72: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionId I
      // 75: aload 0
      // 76: getfield net/rim/device/internal/synchronization/ota/session/Session._type I
      // 79: aload 0
      // 7a: getfield net/rim/device/internal/synchronization/ota/session/Session._changeListId I
      // 7d: aload 0
      // 7e: getfield net/rim/device/internal/synchronization/ota/session/Session._state I
      // 81: aload 2
      // 82: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logErrorMessage (Ljava/lang/String;Ljava/lang/String;IIIILjava/lang/Object;)V
      // 85: bipush 1
      // 86: istore 1
      // 87: goto a9
      // 8a: astore 2
      // 8b: aload 0
      // 8c: getfield net/rim/device/internal/synchronization/ota/session/DeviceSession._serviceUid Ljava/lang/String;
      // 8f: aload 0
      // 90: getfield net/rim/device/internal/synchronization/ota/session/DeviceSession._userId Ljava/lang/String;
      // 93: aload 0
      // 94: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionId I
      // 97: aload 0
      // 98: getfield net/rim/device/internal/synchronization/ota/session/Session._type I
      // 9b: aload 0
      // 9c: getfield net/rim/device/internal/synchronization/ota/session/Session._changeListId I
      // 9f: aload 0
      // a0: getfield net/rim/device/internal/synchronization/ota/session/Session._state I
      // a3: aload 2
      // a4: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logErrorMessage (Ljava/lang/String;Ljava/lang/String;IIIILjava/lang/Object;)V
      // a7: bipush 1
      // a8: istore 1
      // a9: aload 0
      // aa: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionManager Lnet/rim/device/internal/synchronization/ota/session/SessionManager;
      // ad: invokevirtual java/lang/Thread.isAlive ()Z
      // b0: ifeq c7
      // b3: iload 1
      // b4: ifeq bb
      // b7: aload 0
      // b8: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.abort ()V
      // bb: aload 0
      // bc: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionManager Lnet/rim/device/internal/synchronization/ota/session/SessionManager;
      // bf: aload 0
      // c0: invokevirtual net/rim/device/internal/synchronization/ota/session/SessionManager.onSessionStateChange (Lnet/rim/device/internal/synchronization/ota/session/Session;)V
      // c3: goto c7
      // c6: astore 2
      // c7: aload 0
      // c8: getfield net/rim/device/internal/synchronization/ota/session/DeviceSession._serviceUid Ljava/lang/String;
      // cb: aload 0
      // cc: getfield net/rim/device/internal/synchronization/ota/session/DeviceSession._userId Ljava/lang/String;
      // cf: aload 0
      // d0: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionId I
      // d3: aload 0
      // d4: getfield net/rim/device/internal/synchronization/ota/session/Session._type I
      // d7: aload 0
      // d8: getfield net/rim/device/internal/synchronization/ota/session/Session._changeListId I
      // db: aload 0
      // dc: getfield net/rim/device/internal/synchronization/ota/session/Session._state I
      // df: aload 0
      // e0: getfield net/rim/device/internal/synchronization/ota/session/Session._timeout J
      // e3: bipush 0
      // e4: aconst_null
      // e5: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logStartEndSession (Ljava/lang/String;Ljava/lang/String;IIIIJZLjava/lang/Object;)V
      // e8: aload 0
      // e9: invokespecial net/rim/device/internal/synchronization/ota/session/DeviceSession.cleanReferences ()V
      // ec: return
      // try (12 -> 28): 29 null
      // try (12 -> 28): 47 null
      // try (72 -> 76): 77 null
   }

   public final boolean incrementChangeListId() {
      return this._incrementChangeListId;
   }

   public final void incrementChangeListId(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final SyncCommand convertSyncApplicationRecordChangeToSyncCommand(SyncApplicationRecordChange aSyncApplicationRecordChange) {
      SyncCommand xSyncCommand = null;
      int xSyncCommandTag = 0;
      switch (aSyncApplicationRecordChange.getOperation()) {
         case 0:
         case 3:
         case 7:
         case 8:
         case 9:
            break;
         case 1:
         default:
            xSyncCommandTag = 1;
            break;
         case 2:
            xSyncCommandTag = 2;
            break;
         case 4:
            xSyncCommandTag = 3;
            break;
         case 5:
            xSyncCommandTag = 4;
            break;
         case 6:
            xSyncCommandTag = 5;
            break;
         case 10:
            xSyncCommandTag = 6;
      }

      xSyncCommand = super._syncCommandsPool.checkOut(xSyncCommandTag);
      xSyncCommand.setId(aSyncApplicationRecordChange.getRefId());
      if (xSyncCommand instanceof RecordBaseSyncCommand) {
         RecordBaseSyncCommand xRecordBaseSyncCommand = (RecordBaseSyncCommand)xSyncCommand;
         xRecordBaseSyncCommand.setRecordUID(aSyncApplicationRecordChange.getRecordUid());
         xRecordBaseSyncCommand.setRecordFields(aSyncApplicationRecordChange.getRecordFields(true));
         xRecordBaseSyncCommand.setForSlowSync(aSyncApplicationRecordChange.isForSlowSync());
      }

      return xSyncCommand;
   }
}
