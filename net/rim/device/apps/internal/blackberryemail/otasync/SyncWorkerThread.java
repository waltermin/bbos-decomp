package net.rim.device.apps.internal.blackberryemail.otasync;

import java.util.Vector;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.apps.api.transmission.rim.otasync.OTAFMConfiguration;

final class SyncWorkerThread extends Thread {
   private OTAMessageSync _otaMessageSync;
   private OTAFMConfigurationManagerImpl _configManager;
   private ReliableTransmissionHelper _reliableService;
   private Vector _workQueue;
   private Vector _workUnitPool;
   private boolean _nonConfiguredSBsChecked;
   private long _lastNonConfiguredSBsCheckedTime;
   private boolean _dontCheckForNonConfiguredSBs;
   private long _lastConfigurationScanTime;
   private long _lastFlushTransmitBuffersTime;
   private static final int MAX_FREE_WORKUNITS;
   private static final long FLUSH_BUFFER_TIMEOUT;
   private static final int IDLE_TIMEOUT;
   private static final long SCAN_CONFIGURATION_TIMEOUT;
   private static final long SEND_CONFIGURATION_TIMEOUT;
   private static final int SEND_CONFIGURATION_REQUEST;
   private static final int SEND_CONFIGURATION;
   private static final int SEND_MESSAGE_LIST;
   private static final int FLUSH_TRANSMIT_BUFFERS;
   private static final int RELIABLE_TRANSMISSION_SCAN;
   private static final int FLUSH_TRANSMIT_BUFFER;
   private static final int SEND_FOLDER_LIST_REQUEST;
   private static final int MESSAGE_LIST_RESTORED;
   private static final int FOLDER_LIST_SYNCED;
   private static final int SCAN_CONFIGURATIONS;
   private static final int SLEEP;
   private static final int TASK_FORCE_CONFIGURATION_QUERY;
   private static final int TASK_ENABLE_SERVICE_RECORD;
   private static final int TASK_SEND_MESSAGE_LIST;
   private static final int TASK_SEND_FOLDER_LIST_REQUEST;
   private static final int TASK_SEND_CONFIGURATION_QUERY_IF_MISSING;
   private static final int TASK_SET_RESTORE_MESSAGE_LIST;
   private static final int TASK_SCAN_CONFIGURATIONS;
   private static final int TASK_SEND_FOLDER_LIST_REQUEST_OR_MESSAGE_LIST;
   private static final int TASK_SEND_CONFIGURATION_QUERY;
   private static final long NON_CONFIGURED_SB_CHECK_TIMEOUT;

   SyncWorkerThread(OTAMessageSync messageSync) {
      this._otaMessageSync = messageSync;
      this._workQueue = (Vector)(new Object());
      this._workUnitPool = (Vector)(new Object());
      int newPriority = this.getPriority() - 2;
      if (newPriority < 1) {
         newPriority = 1;
      }

      this.setPriority(newPriority);
   }

   final void setReliableTransmissionService(ReliableTransmissionHelper service) {
      throw new RuntimeException("cod2jar: stack: underflow");
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
      // 000: aload 0
      // 001: invokestatic net/rim/device/apps/api/transmission/rim/otasync/OTAFMConfigurationManager$Instance.getInstance ()Lnet/rim/device/apps/api/transmission/rim/otasync/OTAFMConfigurationManager;
      // 004: checkcast net/rim/device/apps/internal/blackberryemail/otasync/OTAFMConfigurationManagerImpl
      // 007: putfield net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread._configManager Lnet/rim/device/apps/internal/blackberryemail/otasync/OTAFMConfigurationManagerImpl;
      // 00a: aconst_null
      // 00b: astore 1
      // 00c: aload 0
      // 00d: getfield net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread._workQueue Ljava/util/Vector;
      // 010: dup
      // 011: astore 2
      // 012: monitorenter
      // 013: aload 0
      // 014: getfield net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread._workQueue Ljava/util/Vector;
      // 017: invokevirtual java/util/Vector.size ()I
      // 01a: ifne 02b
      // 01d: aload 0
      // 01e: getfield net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread._workQueue Ljava/util/Vector;
      // 021: invokevirtual java/lang/Object.wait ()V
      // 024: goto 013
      // 027: astore 3
      // 028: goto 013
      // 02b: aload 2
      // 02c: monitorexit
      // 02d: goto 037
      // 030: astore 4
      // 032: aload 2
      // 033: monitorexit
      // 034: aload 4
      // 036: athrow
      // 037: aload 0
      // 038: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.getNextWorkUnit ()Lnet/rim/device/apps/internal/blackberryemail/otasync/WorkUnit;
      // 03b: astore 1
      // 03c: aload 1
      // 03d: ifnonnull 043
      // 040: goto 108
      // 043: aload 1
      // 044: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._command I
      // 047: tableswitch 65 0 12 193 65 79 100 114 125 132 150 193 164 171 178 185
      // 088: aload 0
      // 089: aload 1
      // 08a: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmO1 Ljava/lang/Object;
      // 08d: checkcast java/lang/Object
      // 090: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doSendConfigurationQuery (Lnet/rim/device/api/servicebook/ServiceRecord;)V
      // 093: goto 108
      // 096: aload 0
      // 097: aload 1
      // 098: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmO1 Ljava/lang/Object;
      // 09b: checkcast java/lang/Object
      // 09e: aload 1
      // 09f: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmO2 Ljava/lang/Object;
      // 0a2: checkcast java/lang/Object
      // 0a5: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doSendConfiguration (Lnet/rim/device/api/servicebook/ServiceRecord;Lnet/rim/device/apps/api/transmission/rim/otasync/OTAFMConfiguration;)V
      // 0a8: goto 108
      // 0ab: aload 0
      // 0ac: aload 1
      // 0ad: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmO1 Ljava/lang/Object;
      // 0b0: checkcast java/lang/Object
      // 0b3: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doSendMessageList (Lnet/rim/device/api/servicebook/ServiceRecord;)V
      // 0b6: goto 108
      // 0b9: aload 0
      // 0ba: aload 1
      // 0bb: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmB1 Z
      // 0be: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doFlushTransmitBuffers (Z)V
      // 0c1: goto 108
      // 0c4: aload 0
      // 0c5: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doReliableTransmissionScan ()V
      // 0c8: goto 108
      // 0cb: aload 0
      // 0cc: aload 1
      // 0cd: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmO1 Ljava/lang/Object;
      // 0d0: checkcast java/lang/Object
      // 0d3: aload 1
      // 0d4: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmB1 Z
      // 0d7: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doFlushTransmitBuffer (Lnet/rim/device/api/servicebook/ServiceRecord;Z)V
      // 0da: goto 108
      // 0dd: aload 0
      // 0de: aload 1
      // 0df: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmO1 Ljava/lang/Object;
      // 0e2: checkcast java/lang/Object
      // 0e5: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doSendFolderListRequest (Lnet/rim/device/api/servicebook/ServiceRecord;)V
      // 0e8: goto 108
      // 0eb: aload 0
      // 0ec: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doMessageListRestored ()V
      // 0ef: goto 108
      // 0f2: aload 0
      // 0f3: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doFolderListSynced ()V
      // 0f6: goto 108
      // 0f9: aload 0
      // 0fa: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doScanConfigurations ()V
      // 0fd: goto 108
      // 100: aload 0
      // 101: aload 1
      // 102: getfield net/rim/device/apps/internal/blackberryemail/otasync/WorkUnit._parmI1 I
      // 105: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.doSleepFor (I)V
      // 108: aload 1
      // 109: ifnonnull 10f
      // 10c: goto 00a
      // 10f: aload 0
      // 110: aload 1
      // 111: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.freeWorkUnit (Lnet/rim/device/apps/internal/blackberryemail/otasync/WorkUnit;)V
      // 114: goto 00a
      // 117: astore 2
      // 118: aload 1
      // 119: ifnonnull 11f
      // 11c: goto 00a
      // 11f: aload 0
      // 120: aload 1
      // 121: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.freeWorkUnit (Lnet/rim/device/apps/internal/blackberryemail/otasync/WorkUnit;)V
      // 124: goto 00a
      // 127: astore 5
      // 129: aload 1
      // 12a: ifnull 132
      // 12d: aload 0
      // 12e: aload 1
      // 12f: invokespecial net/rim/device/apps/internal/blackberryemail/otasync/SyncWorkerThread.freeWorkUnit (Lnet/rim/device/apps/internal/blackberryemail/otasync/WorkUnit;)V
      // 132: aload 5
      // 134: athrow
      // try (15 -> 18): 19 null
      // try (11 -> 23): 24 null
      // try (24 -> 27): 24 null
      // try (29 -> 94): 101 null
      // try (29 -> 94): 109 null
      // try (101 -> 102): 109 null
      // try (109 -> 110): 109 null
   }

   private final WorkUnit getNextWorkUnit() {
      WorkUnit workUnit = null;
      synchronized (this._workQueue) {
         if (this._workQueue.size() > 0) {
            workUnit = (WorkUnit)this._workQueue.elementAt(0);
            this._workQueue.removeElementAt(0);
         }

         return workUnit;
      }
   }

   private final WorkUnit allocateWorkUnit() {
      synchronized (this._workUnitPool) {
         int size = this._workUnitPool.size();
         WorkUnit workUnit;
         if (size > 0) {
            workUnit = (WorkUnit)this._workUnitPool.elementAt(size - 1);
            this._workUnitPool.removeElementAt(size - 1);
         } else {
            workUnit = new WorkUnit();
         }

         return workUnit;
      }
   }

   private final WorkUnit allocateWorkUnit(int command) {
      WorkUnit workUnit = this.allocateWorkUnit();
      workUnit._command = command;
      return workUnit;
   }

   private final WorkUnit allocateWorkUnit(int command, boolean parm) {
      WorkUnit workUnit = this.allocateWorkUnit(command);
      workUnit._parmB1 = parm;
      return workUnit;
   }

   private final WorkUnit allocateWorkUnit(int command, Object parm) {
      WorkUnit workUnit = this.allocateWorkUnit(command);
      workUnit._parmO1 = parm;
      return workUnit;
   }

   private final WorkUnit allocateWorkUnit(int command, Object parm1, boolean parm2) {
      WorkUnit workUnit = this.allocateWorkUnit(command, parm1);
      workUnit._parmB1 = parm2;
      return workUnit;
   }

   private final WorkUnit allocateWorkUnit(int command, Object parmA, Object parmB) {
      WorkUnit workUnit = this.allocateWorkUnit(command, parmA);
      workUnit._parmO2 = parmB;
      return workUnit;
   }

   private final WorkUnit allocateWorkUnit(int command, int parm1) {
      WorkUnit workUnit = this.allocateWorkUnit(command);
      workUnit._parmI1 = parm1;
      return workUnit;
   }

   private final void freeWorkUnit(WorkUnit workUnit) {
      if (this._workUnitPool.size() < 5) {
         workUnit.reset();
         this._workUnitPool.addElement(workUnit);
      }
   }

   private final void queueWorkUnit(WorkUnit workUnit) {
      synchronized (this._workQueue) {
         this._workQueue.addElement(workUnit);
         this._workQueue.notify();
      }
   }

   private final boolean workUnitPresent(int command, Object parmA, Object parmB) {
      synchronized (this._workQueue) {
         for (int i = this._workQueue.size() - 1; i >= 0; i--) {
            WorkUnit workUnit = (WorkUnit)this._workQueue.elementAt(i);
            if (workUnit._command == command && workUnit._parmO1 == parmA && workUnit._parmO2 == parmB) {
               return true;
            }
         }

         return false;
      }
   }

   private final void forAllCMIMEServiceRecords(int task) {
      long currentTime = System.currentTimeMillis();
      ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("CMIME");

      for (int i = serviceRecords.length - 1; i >= 0; i--) {
         ServiceRecord serviceRecord = serviceRecords[i];
         OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
         ServiceSyncInfo syncInfo = this._configManager.getServiceSyncInfo(serviceRecord);
         switch (task) {
            case 0:
            case 7:
               break;
            case 1:
            default:
               this.sendConfigurationQuery(serviceRecord);
               break;
            case 2:
               configuration = (OTAFMConfiguration)(new Object());
               configuration.setServerSupport(true, true, true, true, true);
               this._configManager.updateConfiguration(serviceRecord, configuration, true);
               break;
            case 3:
               if (configuration != null && configuration.getWirelessReconcileEnabled()) {
                  this.sendMessageList(serviceRecord);
               }
               break;
            case 4:
               if (configuration != null && configuration.getWirelessFilingEnabled()) {
                  this.sendFolderListRequest(serviceRecord);
               }
               break;
            case 5:
               if (configuration == OTAFMConfiguration.getDisabledConfiguration() && this._configManager.containsOTAFMInfo(serviceRecord)) {
                  this.sendConfigurationQuery(serviceRecord);
               }
               break;
            case 6:
               if (syncInfo != null) {
                  syncInfo.setRestoreMessageList(true);
               }
               break;
            case 8:
               if (syncInfo != null && !configuration.hasAcknowledgementBeenReceived()) {
                  long delta = currentTime - syncInfo.getConfigurationSentTimestamp();
                  if (delta > 960000) {
                     this.sendConfiguration(serviceRecord, configuration);
                  } else if (delta < 0) {
                     syncInfo.setConfigurationSentTimestamp(currentTime);
                  }

                  PersistentObject.commit(syncInfo);
               }
               break;
            case 9:
               if (configuration != null) {
                  if (configuration.getWirelessFilingEnabled()) {
                     this.sendFolderListRequest(serviceRecord);
                  } else if (configuration.getWirelessReconcileEnabled()) {
                     this.sendMessageList(serviceRecord);
                  }
               }
               break;
            case 10:
               if (this._configManager.containsOTAFMInfo(serviceRecord)) {
                  this.sendConfigurationQuery(serviceRecord);
               }
         }
      }
   }

   final void serviceRecordsChanged() {
      this._nonConfiguredSBsChecked = false;
      this._dontCheckForNonConfiguredSBs = false;
   }

   final void forceConfigurationQuery() {
      this.forAllCMIMEServiceRecords(1);
   }

   final void sendConfigurationQuery() {
      this.forAllCMIMEServiceRecords(10);
   }

   final void sendConfigurationQuery(ServiceRecord serviceRecord) {
      this.queueWorkUnit(this.allocateWorkUnit(1, serviceRecord));
   }

   private final void doSendConfigurationQuery(ServiceRecord serviceRecord) {
      this._otaMessageSync.flushTransmitBuffer(serviceRecord, false);
      this._otaMessageSync.sendConfigurationQuery(serviceRecord);
   }

   final void sendConfiguration(ServiceRecord serviceRecord, OTAFMConfiguration configuration) {
      this.queueWorkUnit(this.allocateWorkUnit(2, serviceRecord, configuration));
   }

   private final void doSendConfiguration(ServiceRecord serviceRecord, OTAFMConfiguration configuration) {
      this._otaMessageSync.flushTransmitBuffer(serviceRecord, false);
      this._otaMessageSync.sendConfiguration(serviceRecord, configuration);
   }

   final void enableAllServiceRecords() {
      this.forAllCMIMEServiceRecords(2);
   }

   final void sendAllMessageLists() {
      this.forAllCMIMEServiceRecords(3);
   }

   final void sendAllFolderListRequests() {
      this.forAllCMIMEServiceRecords(4);
   }

   final void sendMessageList(ServiceRecord serviceRecord) {
      this.queueWorkUnit(this.allocateWorkUnit(3, serviceRecord));
   }

   private final void doSendMessageList(ServiceRecord serviceRecord) {
      OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
      if (configuration.getWirelessReconcileEnabled()) {
         this._otaMessageSync.sendMessageList(serviceRecord);
      }
   }

   private final boolean nonConfiguredSBsExist() {
      ServiceRecord[] serviceRecords = ServiceBook.getSB().findRecordsByCid("CMIME");

      for (int i = serviceRecords.length - 1; i >= 0; i--) {
         ServiceRecord serviceRecord = serviceRecords[i];
         OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecord);
         if (configuration == OTAFMConfiguration.getDisabledConfiguration() && this._configManager.containsOTAFMInfo(serviceRecord)) {
            return true;
         }
      }

      this._dontCheckForNonConfiguredSBs = true;
      return false;
   }

   private final void scanForNonConfiguredServiceRecords() {
      this.forAllCMIMEServiceRecords(5);
   }

   final void performPeriodicTasks() {
      long currentTime = System.currentTimeMillis();
      if (this._nonConfiguredSBsChecked
         && !this._dontCheckForNonConfiguredSBs
         && this.nonConfiguredSBsExist()
         && currentTime - this._lastNonConfiguredSBsCheckedTime > 900000) {
         this._nonConfiguredSBsChecked = false;
      }

      if (!this._nonConfiguredSBsChecked && currentTime != 0 && ReliableTransmissionHelper.canTransmit()) {
         this.scanForNonConfiguredServiceRecords();
         this._lastNonConfiguredSBsCheckedTime = currentTime;
         this._nonConfiguredSBsChecked = true;
      }

      if (!this._configManager.hasUnflushedBuffers() || this._lastFlushTransmitBuffersTime == 0 || currentTime < this._lastFlushTransmitBuffersTime) {
         this._lastFlushTransmitBuffersTime = currentTime;
      }

      if (DeviceInfo.getIdleTime() > 120) {
         if (currentTime - this._lastFlushTransmitBuffersTime > 180000) {
            this.flushTransmitBuffers(false);
            this._lastFlushTransmitBuffersTime = currentTime;
         }

         if (currentTime < this._lastConfigurationScanTime) {
            this._lastConfigurationScanTime = currentTime;
         } else if (currentTime - this._lastConfigurationScanTime > 600000) {
            this._lastConfigurationScanTime = currentTime;
            this.queueWorkUnit(this.allocateWorkUnit(11));
         }
      }

      if (this._reliableService.hasWork() && !this.workUnitPresent(5, null, null)) {
         this.queueWorkUnit(this.allocateWorkUnit(5));
      }
   }

   final void flushTransmitBuffers(boolean userAction) {
      if (this._configManager.isOTAFMAvailable() && ReliableTransmissionHelper.canTransmit() && !this.workUnitPresent(4, null, null)) {
         this.queueWorkUnit(this.allocateWorkUnit(4, userAction));
      }
   }

   private final void doFlushTransmitBuffers(boolean userAction) {
      ServiceRecord[] serviceRecords = this._configManager.getServiceRecords();
      synchronized (serviceRecords) {
         for (int i = serviceRecords.length - 1; i >= 0; i--) {
            OTAFMConfiguration configuration = this._configManager.getConfiguration(serviceRecords[i]);
            if (configuration != null && configuration.getWirelessReconcileEnabled()) {
               this._otaMessageSync.flushTransmitBuffer(serviceRecords[i], userAction);
            }
         }
      }
   }

   private final void doReliableTransmissionScan() {
      this._reliableService.periodicScan();
   }

   final void flushTransmitBuffer(ServiceRecord serviceRecord, boolean userAction) {
      this.queueWorkUnit(this.allocateWorkUnit(6, serviceRecord, userAction));
   }

   private final void doFlushTransmitBuffer(ServiceRecord serviceRecord, boolean userAction) {
      this._otaMessageSync.flushTransmitBuffer(serviceRecord, userAction);
   }

   final void sendFolderListRequest(ServiceRecord serviceRecord) {
      this.queueWorkUnit(this.allocateWorkUnit(7, serviceRecord));
   }

   private final void doSendFolderListRequest(ServiceRecord serviceRecord) {
      if (this._configManager.wirelessFilingAllowed(serviceRecord)) {
         this._otaMessageSync.flushTransmitBuffer(serviceRecord, false);
         this._otaMessageSync.sendFolderListRequest(serviceRecord);
      }
   }

   private final void doScanConfigurations() {
      this.forAllCMIMEServiceRecords(8);
   }

   final void messageListRestored() {
      if (!this.workUnitPresent(9, null, null)) {
         this.queueWorkUnit(this.allocateWorkUnit(9));
      }
   }

   private final void doMessageListRestored() {
      this.setRestoreMessageListFlag();
      this.sendAllMessageLists();
   }

   final void setRestoreMessageListFlag() {
      this.forAllCMIMEServiceRecords(6);
   }

   final void folderListSynced() {
      if (!this.workUnitPresent(10, null, null)) {
         this.queueWorkUnit(this.allocateWorkUnit(10));
      }
   }

   private final void doFolderListSynced() {
      this.forAllCMIMEServiceRecords(9);
   }

   final void sleepFor(int seconds) {
      this.queueWorkUnit(this.allocateWorkUnit(12, seconds));
   }

   private final void doSleepFor(int seconds) {
      try {
         Thread.sleep(seconds * 1000);
      } finally {
         return;
      }
   }
}
