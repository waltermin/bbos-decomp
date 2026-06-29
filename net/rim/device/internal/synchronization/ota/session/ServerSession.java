package net.rim.device.internal.synchronization.ota.session;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.servicebook.ServiceIdentifier;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.cldc.io.sync.RecordBaseSyncCommand;
import net.rim.device.cldc.io.sync.SyncCommand;
import net.rim.device.cldc.io.sync.SyncDatagram;
import net.rim.device.cldc.io.sync.command.Delete;
import net.rim.device.cldc.io.sync.command.Get;
import net.rim.device.cldc.io.sync.command.InitiateSync;
import net.rim.device.cldc.io.sync.command.Status;
import net.rim.device.cldc.io.sync.command.UnKnowen;
import net.rim.device.cldc.io.sync.command.UpdateSyncConfiguration;
import net.rim.device.cldc.io.sync.command.Use;
import net.rim.device.internal.synchronization.ota.api.Logger;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnection;
import net.rim.device.internal.synchronization.ota.api.SyncAgentConnections;
import net.rim.device.internal.synchronization.ota.api.SyncAgentRecord;
import net.rim.device.internal.synchronization.ota.api.SyncAgentRecordCollector;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatistics;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.DataSource;
import net.rim.device.internal.synchronization.ota.service.DataSourceDatabase;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.vm.Process;

public final class ServerSession extends Session {
   private int _currentDataSourceId;
   private int _currentTableId;
   private int _currentDatabaseVersion;
   private String _currentDatabaseName;
   private SyncAgentConnection _syncAgentConnection;
   private int _sessionStatusErrorCode;
   private int _numberOfSyncDatagramsYetToBeReceived;
   private int[] _receivedSyncDatagramStatus;
   private IntIntHashtable _commandIdToErrorCodeMap;
   private String _serviceUid;
   private long _sid;
   private ServiceIdentifier _serviceIdentifier;
   private String _userId;
   private boolean _incrementExpectedChangeListId;
   private boolean _considerUpdatesAsObsoleteChanges;
   private boolean _checkForDuplicatedAdds;
   private CpTicketHolder _cpTicketHolder;
   private SyncAgentRecord _syncAgentRecord;
   private Configuration _configuration;
   private Vector _syncAgentConnectionList;
   private IntHashtable _listOfFragmentedRecords;
   private static final int DATAGRAM_RECEIVED = 1;
   private static final int INVALID_DATAGRAM = 2;

   public ServerSession(SessionManager aSessionManager, int aSessionId, int aChangeListId, int aSessionType, CpTicketHolder cpTicketHolder) {
      super(aSessionManager, aSessionId, aChangeListId);
      this.setType(aSessionType);
      this._incrementExpectedChangeListId = true;
      this._commandIdToErrorCodeMap = (IntIntHashtable)(new Object());
      this._sessionStatusErrorCode = 200;
      this._numberOfSyncDatagramsYetToBeReceived = -1;
      this._serviceUid = aSessionManager.getServiceUid();
      this._sid = aSessionManager.getSid();
      this._serviceIdentifier = aSessionManager.getServiceIdentifier();
      this._userId = aSessionManager.getUserId();
      this._configuration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(this._sid);
      this._cpTicketHolder = cpTicketHolder;
      this._syncAgentConnectionList = (Vector)(new Object(0));
      this._listOfFragmentedRecords = (IntHashtable)(new Object(0));
   }

   public final void checkForDuplicatedAdds(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean checkForDuplicatedAdds() {
      return this._checkForDuplicatedAdds;
   }

   public final boolean considerUpdatesAsObsoleteChanges() {
      return this._considerUpdatesAsObsoleteChanges;
   }

   public final void considerUpdatesAsObsoleteChanges(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final void incrementExpectedChangeListId(boolean value) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final boolean incrementExpectedChangeListNumber() {
      return this._incrementExpectedChangeListId;
   }

   private final boolean allSyncDatagramsReceived() {
      return this._numberOfSyncDatagramsYetToBeReceived == 0;
   }

   private final boolean isAlreadyReceived(int sequenceNumber) {
      return this._receivedSyncDatagramStatus != null && (this._receivedSyncDatagramStatus[sequenceNumber] & 1) == 1;
   }

   private final void setSyncDatagramStatus(int sequenceNumber, int lastSequenceNumber, int status) {
      if (this._receivedSyncDatagramStatus == null) {
         int xNewSize = lastSequenceNumber + 1;
         this._receivedSyncDatagramStatus = new int[xNewSize];
         this._numberOfSyncDatagramsYetToBeReceived = this._receivedSyncDatagramStatus.length;
      }

      this._receivedSyncDatagramStatus[sequenceNumber] = this._receivedSyncDatagramStatus[sequenceNumber] | status;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void onDatagramReceived(SyncDatagram aSyncDatagram) {
      this._currentDataSourceId = 0;
      this._currentTableId = 0;
      this._currentDatabaseVersion = 0;
      this._currentDatabaseName = null;
      int xCommandErrorCode = 403;
      int xSequenceNumber = aSyncDatagram.getCurrentSequenceNumber();
      int xLastSequenceNumber = aSyncDatagram.getLastSequenceNumber();
      if (!this.isAlreadyReceived(xSequenceNumber)) {
         this.setSyncDatagramStatus(xSequenceNumber, xLastSequenceNumber, 1);
         if (!aSyncDatagram.isValid()) {
            this.setSyncDatagramStatus(xSequenceNumber, xLastSequenceNumber, 2);
         } else {
            this._numberOfSyncDatagramsYetToBeReceived--;
            this.goIntoProcessingState();
            if (this._cpTicketHolder != null) {
               this._cpTicketHolder.setTimer(this.getTimeout());
            }

            int xSyncCommandId = 0;
            int xUseCommandId = -1;
            int xUseCommandErrorCode = 200;

            while (this.getState() != 9) {
               if (super._sessionManager.isDone()) {
                  this.abort();
               } else if (Process.ensureMinimumIdleTime(5) <= 0) {
                  try {
                     Thread.sleep(5000);
                  } finally {
                     continue;
                  }
               } else {
                  SyncCommand xSyncCommand = aSyncDatagram.checkOutSyncCommand();
                  if (xSyncCommand == null) {
                     break;
                  }

                  xSyncCommandId = xSyncCommand.getId();
                  if (!xSyncCommand.isValid()) {
                     xCommandErrorCode = 409;
                  } else {
                     label290: {
                        boolean var15 = false /* VF: Semaphore variable */;

                        label271: {
                           try {
                              label269:
                              try {
                                 var15 = true;
                                 if (xSyncCommand instanceof RecordBaseSyncCommand) {
                                    if (xUseCommandErrorCode != 200) {
                                       var15 = false;
                                    } else {
                                       xCommandErrorCode = this.execute((RecordBaseSyncCommand)xSyncCommand);
                                       var15 = false;
                                    }
                                 } else if (xSyncCommand instanceof Use) {
                                    xCommandErrorCode = this.execute((Use)xSyncCommand);
                                    xUseCommandErrorCode = xCommandErrorCode;
                                    xUseCommandId = xSyncCommand.getId();
                                    var15 = false;
                                 } else if (xSyncCommand instanceof InitiateSync) {
                                    xCommandErrorCode = this.execute((InitiateSync)xSyncCommand);
                                    if (xCommandErrorCode == 200) {
                                       this._commandIdToErrorCodeMap.remove(xUseCommandId);
                                       xUseCommandId = -1;
                                       var15 = false;
                                    } else {
                                       var15 = false;
                                    }
                                 } else if (xSyncCommand instanceof UpdateSyncConfiguration) {
                                    xCommandErrorCode = this.execute((UpdateSyncConfiguration)xSyncCommand);
                                    if (xCommandErrorCode == 200) {
                                       this._commandIdToErrorCodeMap.remove(xUseCommandId);
                                       xUseCommandId = -1;
                                       var15 = false;
                                    } else {
                                       var15 = false;
                                    }
                                 } else if (xSyncCommand instanceof UnKnowen) {
                                    xCommandErrorCode = this.execute((UnKnowen)xSyncCommand);
                                    var15 = false;
                                 } else {
                                    xCommandErrorCode = 407;
                                    var15 = false;
                                 }
                                 break label271;
                              } catch (Throwable var23) {
                                 if (t instanceof Object) {
                                    xCommandErrorCode = 413;
                                    var15 = false;
                                 } else {
                                    String errorMessage = ((StringBuffer)(new Object("ServerSession.onDatagramReceived() threw an unexpected exception: ")))
                                       .append(t.getMessage())
                                       .toString();
                                    Logger.logErrorMessage(errorMessage);
                                    xCommandErrorCode = 411;
                                    var15 = false;
                                 }
                                 break label269;
                              }
                           } finally {
                              if (var15) {
                                 aSyncDatagram.checkInSyncCommand(xSyncCommand);
                              }
                           }

                           aSyncDatagram.checkInSyncCommand(xSyncCommand);
                           break label290;
                        }

                        aSyncDatagram.checkInSyncCommand(xSyncCommand);
                     }
                  }

                  if (xCommandErrorCode != 200) {
                     if (this._commandIdToErrorCodeMap == null) {
                        this._commandIdToErrorCodeMap = (IntIntHashtable)(new Object());
                     }

                     if (xCommandErrorCode == 417) {
                        super._operationSuspensionHappened = true;
                     }

                     this._commandIdToErrorCodeMap.put(xSyncCommandId, xCommandErrorCode);
                     xCommandErrorCode = 200;
                  }

                  Thread.yield();
               }
            }
         }

         if (this.allSyncDatagramsReceived()) {
            this.succeed();
            return;
         }

         this.goIntoRunningState();
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void sendStatus() {
      SyncDatagram xSyncDatagram = (SyncDatagram)this.checkOutSyncDatagram();
      xSyncDatagram.setSessionId(this.getSessionId());
      Status xStatus = (Status)super._syncCommandsPool.checkOut(8);
      boolean xAddStatusCommand = false;
      if (this._commandIdToErrorCodeMap != null) {
         IntEnumeration xCmdIdsList = this._commandIdToErrorCodeMap.keys();
         if (!this._commandIdToErrorCodeMap.isEmpty()) {
            while (xCmdIdsList.hasMoreElements()) {
               int xCmdId = xCmdIdsList.nextElement();
               int xCmdResultCode = this._commandIdToErrorCodeMap.get(xCmdId);
               xStatus.addCmdErrorCode(xCmdId, xCmdResultCode);
            }

            xAddStatusCommand = true;
         }
      }

      if (this._receivedSyncDatagramStatus != null) {
         for (int xIndex = this._receivedSyncDatagramStatus.length - 1; xIndex > -1; xIndex--) {
            if ((this._receivedSyncDatagramStatus[xIndex] & 1) != 1) {
               xStatus.addDatagramErrorCode(xIndex, 415);
               xAddStatusCommand = true;
            } else if ((this._receivedSyncDatagramStatus[xIndex] & 2) == 2) {
               xStatus.addDatagramErrorCode(xIndex, 402);
               xAddStatusCommand = true;
            }
         }
      }

      if (this._sessionStatusErrorCode != 200) {
         xStatus.setSessionErrorCode(this._sessionStatusErrorCode);
         xAddStatusCommand = true;
      }

      if (xAddStatusCommand) {
         xSyncDatagram.addCommand(xStatus);
      }

      super._syncCommandsPool.checkIn(xStatus);

      try {
         this.sendDatagram(xSyncDatagram, true, false);
      } catch (Throwable var8) {
         Logger.logErrorMessage(this._serviceUid, this._userId, super._sessionId, super._type, super._changeListId, super._state, e);
         return;
      }
   }

   private final int execute(UnKnowen anUnKnowen) {
      return 408;
   }

   private final int execute(Use aUse) {
      int returnCode = 200;
      SyncAgentConnection xPreviousSyncAgentConnection = this._syncAgentConnection;
      this._syncAgentConnection = null;
      if (aUse.isForDataSource()) {
         this._currentDataSourceId = aUse.getDataSourceId();
      }

      DataSource xDataSource = this._configuration.getDataSourceBy(this._currentDataSourceId);
      if (xDataSource == null) {
         returnCode = 406;
      } else {
         if (aUse.hasExplictDatabaseName()) {
            this._currentDatabaseName = aUse.getDatabaseName();
         } else {
            this._currentDatabaseName = xDataSource.getDataSourceDatabaseNameFor(aUse.getDatabaseId());
         }

         this._currentTableId = aUse.getTableId();
         if ("*".equals(this._currentDatabaseName)) {
            this._currentDatabaseVersion = 0;
            return returnCode;
         }

         Vector xSyncAgentConnectionList = SyncAgentConnections.getAllConnectionsBy(
            this._syncAgentConnectionList, this._sid, xDataSource.getName(), this._currentDatabaseName
         );
         if (xSyncAgentConnectionList.isEmpty()) {
            this._currentDatabaseVersion = aUse.getDatabaseVersion();
            return 405;
         }

         this._syncAgentConnection = (SyncAgentConnection)xSyncAgentConnectionList.firstElement();
         this._currentDatabaseVersion = this._syncAgentConnection.getDatabaseVersion();
         if (this._syncAgentConnection.isInitialized()) {
            int xHashCodeForPC = 0;
            int xHashCodeForCC = this._syncAgentConnection.hashCode();
            if (xPreviousSyncAgentConnection != null) {
               xHashCodeForPC = xPreviousSyncAgentConnection.hashCode();
               if (xHashCodeForPC != xHashCodeForCC) {
                  xPreviousSyncAgentConnection.onSessionEvent(50, null);
               }
            }

            this._syncAgentConnection.getSyncAgentStatistics().setRemainingNumberOfOperations(aUse.getTotalNumberOfOperations(), this.getSessionId());
            if (xHashCodeForPC != xHashCodeForCC) {
               return this._syncAgentConnection.onSessionEvent(49, null);
            }
         }
      }

      return returnCode;
   }

   private final boolean shouldReturnSuspendedChange() {
      return PersistentContent.isContentProtectionSupported() && (this._cpTicketHolder == null || this._cpTicketHolder.getTicket() == null);
   }

   private final int execute(RecordBaseSyncCommand aRecordBaseSyncCommand) {
      if (this._syncAgentConnection == null) {
         return 405;
      }

      SyncAgentStatistics xSyncAgentStatistics = this._syncAgentConnection.getSyncAgentStatistics();
      if (this.shouldReturnSuspendedChange()) {
         return 417;
      }

      int xSyncCommandTag = aRecordBaseSyncCommand.getTag();
      byte var5;
      switch (xSyncCommandTag) {
         case 0:
         default:
            xSyncAgentStatistics.incrementNumberOfExecutedOperations();
            return 407;
         case 1:
            var5 = 1;
            break;
         case 2:
            Delete xDeleteCommand = (Delete)aRecordBaseSyncCommand;
            var5 = (byte)(xDeleteCommand.deleteAll() ? 3 : 2);
            break;
         case 3:
         case 4:
            if (this.considerUpdatesAsObsoleteChanges()) {
               xSyncAgentStatistics.incrementNumberOfExecutedOperations();
               return 414;
            }

            var5 = (byte)(xSyncCommandTag == 3 ? 4 : 5);
            break;
         case 5:
            Get xGetCommand = (Get)aRecordBaseSyncCommand;
            var5 = (byte)(xGetCommand.all() ? 7 : 6);
      }

      if (this._syncAgentRecord != null) {
         this._syncAgentRecord.reset();
      } else {
         this._syncAgentRecord = new SyncAgentRecord();
      }

      if (var5 != 3) {
         this._syncAgentRecord.setUid(aRecordBaseSyncCommand.getRecordUID());
         this._syncAgentRecord.setFields(aRecordBaseSyncCommand.getRecordFields());
         this._syncAgentRecord.setForSlowSync(aRecordBaseSyncCommand.isForSlowSync());
         this._syncAgentRecord.setIndex(aRecordBaseSyncCommand.getIndex());
         this._syncAgentRecord.setLastIndex(aRecordBaseSyncCommand.getLastIndex());
         this._syncAgentRecord.setTableId(this._currentTableId);
         this._syncAgentRecord.setDatabaseVersion(this._currentDatabaseVersion);
         this._syncAgentRecord.checkForDuplicateAdds(this._checkForDuplicatedAdds);
         this._syncAgentRecord.setFieldsAttributes(aRecordBaseSyncCommand.getFieldsAttributes());
      }

      this._syncAgentRecord = this.handleFragmented(this._syncAgentRecord);
      if (this._syncAgentRecord == null) {
         xSyncAgentStatistics.incrementNumberOfExecutedOperations();
         xSyncAgentStatistics.incrementNumberOfFailedOperations();
         String errorMessage = "ServerSession.handleFragment() returned a NULL SyncAgentRecord.";
         Logger.logErrorMessage(errorMessage);
         return 411;
      }

      if (this._syncAgentRecord.isFragmented()) {
         return 200;
      }

      int xReturnErrorCode = this._syncAgentConnection.onSessionEvent(var5, this._syncAgentRecord);
      xSyncAgentStatistics.incrementNumberOfExecutedOperations();
      if (xReturnErrorCode != 200 && xReturnErrorCode != 410) {
         xSyncAgentStatistics.setStatus(2);
         xSyncAgentStatistics.incrementNumberOfFailedOperations();
      }

      return xReturnErrorCode;
   }

   private final int execute(UpdateSyncConfiguration aUpdateSyncConfiguration) {
      int returnCode = 403;

      label140:
      try {
         SyncAgent xSyncAgent = SyncAgent.getSingletonInstance();
         byte[] xConfigChanges = aUpdateSyncConfiguration.getConfiguration();
         DataBuffer xConfigChangesBuffer = (DataBuffer)(new Object(xConfigChanges, 0, xConfigChanges.length, true));
         if (this._currentDataSourceId == 10827) {
            boolean xUserWasEnabled = this._configuration.isUserEnabled();
            this._configuration.parse(xConfigChangesBuffer);
            if (this._configuration.isUserPreferenceToSyncSet()) {
               if (xUserWasEnabled) {
                  if (!this._configuration.isUserEnabled()) {
                     xSyncAgent.notifyListenersWith(24, this._serviceIdentifier);
                     xSyncAgent.notifyListenersWith(2, this._serviceIdentifier);
                     SyncAgentConnections.closeConnections(this._sid, null, null);
                  }
               } else if (this._configuration.isUserEnabled()) {
                  xSyncAgent.notifyListenersWith(23, this._serviceIdentifier);
                  xSyncAgent.notifyListenersWith(1, this._serviceIdentifier);
               }
            }
         } else {
            DataSource xDataSource = this._configuration.getDataSourceBy(this._currentDataSourceId);
            DataSourceDatabase xDataSourceDatabase = xDataSource.getDataSourceDatabaseBy(this._currentDatabaseName, this._currentDatabaseVersion);
            if (this._syncAgentConnection != null) {
               SyncAgentStatistics xSyncAgentStatistics = this._syncAgentConnection.getSyncAgentStatistics();
               xSyncAgentStatistics.incrementNumberOfExecutedOperations();
               xDataSourceDatabase.readFrom(xConfigChangesBuffer);
               if (!xDataSourceDatabase.isServerEnabled()) {
                  this._syncAgentConnection.onSessionEvent(50, null);
                  this._syncAgentConnection.close(true);
               }
            } else if (this._currentDatabaseName == null) {
               xDataSource.readFrom(xConfigChangesBuffer);
            } else if (!this._currentDatabaseName.equals("*")) {
               xConfigChangesBuffer.rewind();
               if (xDataSourceDatabase == null) {
                  IntHashtable xVersionToDataSourceDatabaseMap = xDataSource.getDataSourceDatabaseBy(this._currentDatabaseName);
                  if (xVersionToDataSourceDatabaseMap != null) {
                     Enumeration xEnumeration = xVersionToDataSourceDatabaseMap.elements();

                     while (xEnumeration.hasMoreElements()) {
                        xDataSourceDatabase = (DataSourceDatabase)xEnumeration.nextElement();
                        xDataSourceDatabase.readFrom(xConfigChangesBuffer);
                        xConfigChangesBuffer.rewind();
                     }
                  }
               } else {
                  xDataSourceDatabase.readFrom(xConfigChangesBuffer);
               }

               SyncAgentUrl xSyncAgentUrl = new SyncAgentUrl(this._sid, xDataSource.getName(), this._currentDatabaseName);
               xSyncAgent.notifyListenersWith(4, xSyncAgentUrl);
            } else {
               boolean xWasEnabledByServer = xDataSourceDatabase.isServerEnabled();
               xDataSourceDatabase.readFrom(xConfigChangesBuffer);
               if (xWasEnabledByServer) {
                  if (!xDataSourceDatabase.isServerEnabled()) {
                     synchronized (SyncAgentConnections.getLock()) {
                        Vector xSyncAgentConnectionsList = SyncAgentConnections.getAllConnectionsBy(
                           this._syncAgentConnectionList, this._sid, xDataSource.getName(), null
                        );

                        for (int xIndex = xSyncAgentConnectionsList.size() - 1; xIndex > -1; xIndex--) {
                           SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)xSyncAgentConnectionsList.elementAt(xIndex);
                           if (!xDataSourceDatabase.isServerEnabled()) {
                              xSyncAgentConnection.close(true);
                           }
                        }
                     }
                  }
               } else if (xDataSourceDatabase.isServerEnabled()) {
                  xSyncAgent.notifyListenersWith(1, this._serviceIdentifier);
               }
            }
         }

         returnCode = 200;
      } finally {
         break label140;
      }

      this.setType(17);
      return returnCode;
   }

   private final int execute(InitiateSync aInitiateSync) {
      DataSource xDataSource = this._configuration.getDataSourceBy(this._currentDataSourceId);
      String xDataSourceName = xDataSource != null ? xDataSource.getName() : null;
      this._syncAgentConnectionList = SyncAgentConnections.getAllConnectionsBy(
         this._syncAgentConnectionList, this._sid, xDataSourceName, this._currentDatabaseName
      );
      SyncAgentConnections.resetConnections(this._syncAgentConnectionList);
      this.setType(18);
      return 200;
   }

   private final SyncAgentRecord handleFragmented(SyncAgentRecord aSyncAgentRecord) {
      if (aSyncAgentRecord.isFragmented()) {
         int xRecordUid = aSyncAgentRecord.getUid();
         SyncAgentRecordCollector xSyncAgentRecordCollector = (SyncAgentRecordCollector)this._listOfFragmentedRecords.get(xRecordUid);
         if (xSyncAgentRecordCollector == null) {
            xSyncAgentRecordCollector = new SyncAgentRecordCollector(aSyncAgentRecord);
            this._listOfFragmentedRecords.put(xRecordUid, xSyncAgentRecordCollector);
         }

         aSyncAgentRecord = xSyncAgentRecordCollector.add(aSyncAgentRecord);
         if (aSyncAgentRecord != null && !aSyncAgentRecord.isFragmented()) {
            this._listOfFragmentedRecords.remove(xRecordUid);
         }
      }

      return aSyncAgentRecord;
   }

   @Override
   public final void run() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 0
      // 01: bipush 1
      // 02: invokevirtual java/lang/Thread.setPriority (I)V
      // 05: aload 0
      // 06: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.goIntoRunningState ()V
      // 09: aload 0
      // 0a: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._serviceUid Ljava/lang/String;
      // 0d: aload 0
      // 0e: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._userId Ljava/lang/String;
      // 11: aload 0
      // 12: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionId I
      // 15: aload 0
      // 16: getfield net/rim/device/internal/synchronization/ota/session/Session._type I
      // 19: aload 0
      // 1a: getfield net/rim/device/internal/synchronization/ota/session/Session._changeListId I
      // 1d: aload 0
      // 1e: getfield net/rim/device/internal/synchronization/ota/session/Session._state I
      // 21: aload 0
      // 22: getfield net/rim/device/internal/synchronization/ota/session/Session._timeout J
      // 25: bipush 1
      // 26: aconst_null
      // 27: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logStartEndSession (Ljava/lang/String;Ljava/lang/String;IIIIJZLjava/lang/Object;)V
      // 2a: aload 0
      // 2b: invokespecial net/rim/device/internal/synchronization/ota/session/Session.run ()V
      // 2e: aload 0
      // 2f: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._syncAgentConnection Lnet/rim/device/internal/synchronization/ota/api/SyncAgentConnection;
      // 32: ifnull 59
      // 35: aload 0
      // 36: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._syncAgentConnection Lnet/rim/device/internal/synchronization/ota/api/SyncAgentConnection;
      // 39: bipush 50
      // 3b: aconst_null
      // 3c: invokevirtual net/rim/device/internal/synchronization/ota/api/SyncAgentConnection.onSessionEvent (ILjava/lang/Object;)I
      // 3f: pop
      // 40: aload 0
      // 41: aconst_null
      // 42: putfield net/rim/device/internal/synchronization/ota/session/ServerSession._syncAgentConnection Lnet/rim/device/internal/synchronization/ota/api/SyncAgentConnection;
      // 45: goto 59
      // 48: astore 1
      // 49: aload 0
      // 4a: aconst_null
      // 4b: putfield net/rim/device/internal/synchronization/ota/session/ServerSession._syncAgentConnection Lnet/rim/device/internal/synchronization/ota/api/SyncAgentConnection;
      // 4e: goto 59
      // 51: astore 2
      // 52: aload 0
      // 53: aconst_null
      // 54: putfield net/rim/device/internal/synchronization/ota/session/ServerSession._syncAgentConnection Lnet/rim/device/internal/synchronization/ota/api/SyncAgentConnection;
      // 57: aload 2
      // 58: athrow
      // 59: aload 0
      // 5a: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.getState ()I
      // 5d: tableswitch 27 4 6 39 27 27
      // 78: aload 0
      // 79: invokespecial net/rim/device/internal/synchronization/ota/session/ServerSession.sendStatus ()V
      // 7c: goto 84
      // 7f: astore 1
      // 80: aload 0
      // 81: invokevirtual net/rim/device/internal/synchronization/ota/session/Session.abort ()V
      // 84: aload 0
      // 85: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._serviceUid Ljava/lang/String;
      // 88: aload 0
      // 89: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._userId Ljava/lang/String;
      // 8c: aload 0
      // 8d: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionId I
      // 90: aload 0
      // 91: getfield net/rim/device/internal/synchronization/ota/session/Session._type I
      // 94: aload 0
      // 95: getfield net/rim/device/internal/synchronization/ota/session/Session._changeListId I
      // 98: aload 0
      // 99: getfield net/rim/device/internal/synchronization/ota/session/Session._state I
      // 9c: aload 0
      // 9d: getfield net/rim/device/internal/synchronization/ota/session/Session._timeout J
      // a0: bipush 0
      // a1: aconst_null
      // a2: invokestatic net/rim/device/internal/synchronization/ota/api/Logger.logStartEndSession (Ljava/lang/String;Ljava/lang/String;IIIIJZLjava/lang/Object;)V
      // a5: aload 0
      // a6: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._cpTicketHolder Lnet/rim/device/internal/synchronization/ota/session/CpTicketHolder;
      // a9: ifnull b7
      // ac: aload 0
      // ad: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._cpTicketHolder Lnet/rim/device/internal/synchronization/ota/session/CpTicketHolder;
      // b0: ldc_w 120000
      // b3: i2l
      // b4: invokevirtual net/rim/device/internal/synchronization/ota/session/CpTicketHolder.setTimer (J)V
      // b7: aload 0
      // b8: getfield net/rim/device/internal/synchronization/ota/session/Session._sessionManager Lnet/rim/device/internal/synchronization/ota/session/SessionManager;
      // bb: aload 0
      // bc: invokevirtual net/rim/device/internal/synchronization/ota/session/SessionManager.onSessionStateChange (Lnet/rim/device/internal/synchronization/ota/session/Session;)V
      // bf: goto c3
      // c2: astore 1
      // c3: aload 0
      // c4: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._syncAgentRecord Lnet/rim/device/internal/synchronization/ota/api/SyncAgentRecord;
      // c7: ifnull d1
      // ca: aload 0
      // cb: getfield net/rim/device/internal/synchronization/ota/session/ServerSession._syncAgentRecord Lnet/rim/device/internal/synchronization/ota/api/SyncAgentRecord;
      // ce: invokevirtual net/rim/device/internal/synchronization/ota/api/SyncAgentRecord.reset ()V
      // d1: return
      // try (27 -> 33): 37 null
      // try (27 -> 33): 42 null
      // try (37 -> 38): 42 null
      // try (42 -> 43): 42 null
      // try (22 -> 53): 54 null
      // try (82 -> 86): 87 null
   }
}
