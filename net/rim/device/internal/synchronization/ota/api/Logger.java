package net.rim.device.internal.synchronization.ota.api;

import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.cldc.io.sync.SyncDatagram;
import net.rim.device.internal.synchronization.ota.util.ReusableObjectPool;
import net.rim.device.internal.synchronization.ota.util.ReusableStringBuffer;

public final class Logger {
   public static final long SYNC_EVENTLOGGER_GUID = 2424575107343457299L;
   public static final String SYNC_EVENTLOGGER_DISPLAYNAME = "net.rim.otasync";

   private static final void log(Object message, int logLevel) {
      if (message != null) {
         String xMessageString = message.toString();

         try {
            if (DeviceInfo.isSimulator()) {
               System.out.println(xMessageString);
            }

            EventLogger.logEvent(2424575107343457299L, xMessageString.getBytes(), logLevel);
            if (EventLogger.getMinimumLevel() >= 5 && message instanceof Throwable) {
               Throwable t = (Throwable)message;
               t.printStackTrace();
               return;
            }
         } finally {
            return;
         }
      }
   }

   private static final ReusableStringBuffer checkOutReusableStringBuffer() {
      ReusableObjectPool xReusableObjectPool = ReusableObjectPool.getSingletonInstance(-7834795833597249705L);
      ReusableStringBuffer xReusableStringBuffer = (ReusableStringBuffer)xReusableObjectPool.checkOut();
      if (xReusableStringBuffer == null) {
         xReusableStringBuffer = new ReusableStringBuffer();
      }

      return xReusableStringBuffer;
   }

   private static final void checkInReusableStringBuffer(ReusableStringBuffer aReusableStringBuffer) {
      ReusableObjectPool xReusableObjectPool = ReusableObjectPool.getSingletonInstance(-7834795833597249705L);
      xReusableObjectPool.checkIn(aReusableStringBuffer);
   }

   public static final void logSyncDaemonStarted() {
      log("DN+", 0);
   }

   public static final void logNotDefinedTableId(int anOp, int aTableId) {
      if (3 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AP").append(',').append("OP=").append(anOp);
         xLogMessage.append(',').append("UNRTBLID=").append(aTableId);
         log(xLogMessage, 3);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logInvalidUid(int anOp, String datasource, int aRecordUid) {
      if (3 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AP").append(',').append("OP=");
         xLogMessage.append(anOp).append(',').append("INVUID=").append(aRecordUid);
         xLogMessage.append(',').append("DS=").append(datasource);
         log(xLogMessage, 3);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logOperationError(int anOp, int aRecordUid, Object aMsg) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AP").append(',').append("OP=").append(anOp);
      xLogMessage.append(',').append("UID=").append(aRecordUid);
      xLogMessage.append(',').append("MSG=").append(aMsg);
      log(xLogMessage, 2);
      checkInReusableStringBuffer(xReusableStringBuffer);
      if (EventLogger.getMinimumLevel() >= 5 && aMsg instanceof Throwable) {
         Throwable t = (Throwable)aMsg;
         t.printStackTrace();
      }
   }

   public static final void logTransaction(boolean start, long time, Object aMsg) {
      if (5 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AP").append(',');
         xLogMessage.append((char)(start ? '+' : '-'));
         xLogMessage.append("TRN").append(',').append("SAGC=");
         xLogMessage.append(aMsg);
         if (!start) {
            xLogMessage.append(',').append("TIM=").append(time);
         }

         log(xLogMessage, 5);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logCPTicket(boolean enter) {
      log("AG," + (enter ? 43 : 45) + "CPT", 0);
   }

   public static final void logStartEndSession(
      String serviceUid, String aUserSystemId, int sessionId, int type, int changeListId, int state, long timeout, boolean start, Object aMessage
   ) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AG").append(',');
      xLogMessage.append("SUID=").append(serviceUid).append(',');
      xLogMessage.append("USID=").append(aUserSystemId);
      xLogMessage.append(',').append("STY=").append(type);
      xLogMessage.append(',');
      xLogMessage.append((char)(start ? '+' : '-'));
      xLogMessage.append("SESS").append(',').append("SID=").append(sessionId);
      xLogMessage.append(',').append("CLID=").append(changeListId);
      if (!start) {
         xLogMessage.append(',').append("SST=").append(state);
      } else {
         xLogMessage.append(',').append("TIM=").append(timeout / 60000);
      }

      if (aMessage != null) {
         xLogMessage.append(',').append("MSG=").append(aMessage);
      }

      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logErrorMessage(Object aMsg) {
      log(aMsg, 2);
   }

   public static final void logErrorMessage(String aUserSystemId, Object aMsg) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AG").append(',').append("USID=").append(aUserSystemId);
      xLogMessage.append(',').append("MSG=").append(aMsg);
      log(xLogMessage, 2);
      checkInReusableStringBuffer(xReusableStringBuffer);
      if (EventLogger.getMinimumLevel() >= 5 && aMsg instanceof Throwable) {
         Throwable t = (Throwable)aMsg;
         t.printStackTrace();
      }
   }

   public static final void logUnmappedTagError(int tag, int count, String datasource) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AG").append(',');
      xLogMessage.append("UNMAP");
      xLogMessage.append(',').append("TAG=").append(tag);
      xLogMessage.append(',').append("CNT=").append(count);
      xLogMessage.append(',').append("DS=").append(datasource);
      log(xLogMessage, 2);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logErrorMessage(String serviceUid, String aUserSystemId, int sessionId, int type, int changeListId, int state, Object aMessage) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AG").append(',');
      xLogMessage.append("SUID=").append(serviceUid);
      xLogMessage.append(',').append("USID=").append(aUserSystemId);
      xLogMessage.append(',').append("STY=").append(type);
      xLogMessage.append(',').append("SID=").append(sessionId);
      xLogMessage.append(',').append("CLID=").append(changeListId);
      xLogMessage.append(',').append("MSG=").append(aMessage);
      log(xLogMessage, 2);
      checkInReusableStringBuffer(xReusableStringBuffer);
      if (EventLogger.getMinimumLevel() >= 5 && aMessage instanceof Throwable) {
         Throwable t = (Throwable)aMessage;
         t.printStackTrace();
      }
   }

   public static final void logBuildingGroupHashesTime(Object aMsg, long time) {
      if (5 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',').append("SAGC=");
         xLogMessage.append(aMsg);
         xLogMessage.append(',').append("BGH").append(',').append("TIM=").append(time);
         log(xLogMessage, 5);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logDropDatagram(String serviceUid, String aUserSystemId, SyncDatagram aSyncDatagram, int token) {
      if (3 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',').append("SUID=").append(serviceUid);
         xLogMessage.append(',').append("USID=").append(aUserSystemId);
         xLogMessage.append(',').append("DGDRP").append(',').append("TKN=");
         xLogMessage.append(token).append(',');
         appendSyncDatagram(xLogMessage, aSyncDatagram);
         log(xLogMessage, 3);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logIgnoreLateVerificationResponse(int aSessionId) {
      if (5 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',').append("IGVIF").append(',').append("SID=").append(aSessionId);
         log(xLogMessage, 5);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logStartEndSessionManager(String serviceUid, String aUserSystemId, boolean start) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AG").append(',');
      xLogMessage.append((char)(start ? '+' : '-'));
      xLogMessage.append("SM").append(',').append("SUID=").append(serviceUid).append(',').append("USID=").append(aUserSystemId);
      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logIgnoredSession(String serviceUid, String aUserSystemId, int aSessionId, int reason) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AG").append(',').append("SUID=").append(serviceUid).append(',').append("USID=").append(aUserSystemId);
      xLogMessage.append(',').append("IGSESS").append(',').append("SID=").append(aSessionId);
      xLogMessage.append(',').append("TKN=").append(reason);
      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logUserDisabled(String aUserSystemId) {
      if (3 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',').append("USID=").append(aUserSystemId);
         xLogMessage.append(',').append("UDSLBD");
         log(xLogMessage, 3);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logFastSlowConnection(String serviceUid, String aUserSystemId, boolean fast) {
      if (4 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',').append("SUID=").append(serviceUid).append(',').append("USID=").append(aUserSystemId);
         xLogMessage.append(',').append("FC=").append(fast);
         log(xLogMessage, 4);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logSessionManagerBlockedFor(String serviceUid, String aUserSystemId, long timeInMillis) {
      if (5 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',').append("SUID=").append(serviceUid).append(',').append("USID=").append(aUserSystemId);
         xLogMessage.append(',').append("BLCKD").append(',').append("TIM=");
         xLogMessage.append(timeInMillis / 1000);
         log(xLogMessage, 5);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logAddingRemovingConnection(boolean addingConnection, int priority, int ver, Object aMsg) {
      logAddingRemovingConnection(addingConnection, priority, ver, aMsg, false);
   }

   public static final void logAddingRemovingConnection(boolean addingConnection, int priority, int ver, Object aMsg, boolean forceLogging) {
      if (forceLogging || 5 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',');
         xLogMessage.append((char)(addingConnection ? '+' : '-'));
         xLogMessage.append("SAGC=").append(aMsg);
         xLogMessage.append(',').append("PT=").append(priority);
         xLogMessage.append(',').append("VER=").append(ver);
         log(xLogMessage, forceLogging ? 2 : 5);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logSuspendResumeConnection(boolean suspend, Object aMsg, boolean forceLogging) {
      if (forceLogging || 5 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',');
         xLogMessage.append(suspend ? "SUSP" : "RSUM");
         xLogMessage.append(',').append("SAGC=").append(aMsg);
         log(xLogMessage, forceLogging ? 2 : 5);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   public static final void logCouldNotRunSessionManager(int token, int reason) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("DN").append(',').append("SM").append(',');
      xLogMessage.append("TKN=").append(token).append(',');
      xLogMessage.append(reason);
      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logInvalidSyncState(String serviceUid, String aUserSystemId, int aSessionId, int aChangeListId) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AG").append(',').append("SUID=").append(serviceUid).append(',').append("USID=").append(aUserSystemId);
      xLogMessage.append(',').append("INVSST").append(',').append('@').append("SESS").append(',').append("SID=").append(aSessionId);
      xLogMessage.append(',').append("CLID=").append(aChangeListId);
      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logNumberOfCommandsScaned(Object aMsg, int aNumberOfCommands) {
      if (5 <= EventLogger.getMinimumLevel()) {
         ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
         StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
         xLogMessage.append("AG").append(',').append("SCAN").append(',').append(aMsg);
         xLogMessage.append(',').append("NCMDS=").append(aNumberOfCommands);
         log(xLogMessage, 5);
         checkInReusableStringBuffer(xReusableStringBuffer);
      }
   }

   private static final StringBuffer appendSyncDatagram(StringBuffer aStringBuffer, SyncDatagram aSyncDatagram) {
      aStringBuffer.append("USID=").append(aSyncDatagram.getUserId());
      aStringBuffer.append(',').append("TID=").append(aSyncDatagram.getTransactionId());
      aStringBuffer.append(',').append("SID=").append(aSyncDatagram.getSessionId());
      if (aSyncDatagram.isForVerification()) {
         aStringBuffer.append(',').append("VIFR=").append(aSyncDatagram.IsForVerificationRequest());
      }

      if (!aSyncDatagram.isResponse()) {
         aStringBuffer.append(',').append("CLID=").append(aSyncDatagram.getCurrentChangeListId());
         aStringBuffer.append(',').append("ECLID=").append(aSyncDatagram.getExpectedChangeListId());
      }

      aStringBuffer.append(',').append("SEQ=").append(aSyncDatagram.getCurrentSequenceNumber() + 1);
      aStringBuffer.append(',').append("LSEQ=").append(aSyncDatagram.getLastSequenceNumber() + 1);
      if (aSyncDatagram.isSessionTimeoutProvided()) {
         aStringBuffer.append(',').append("TIM=").append(aSyncDatagram.getSessionTimeout());
      }

      int xNumberOfCommands = aSyncDatagram.getNumberOfCommands();
      if (xNumberOfCommands != 0) {
         aStringBuffer.append(',').append("NCMDS=").append(xNumberOfCommands);
      }

      return aStringBuffer;
   }

   public static final void logTransportationRxTx(String direction, int size, SyncDatagram aSyncDatagram) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("TP").append(',').append(direction);
      xLogMessage.append(',').append("SZ=").append(size).append(',');
      appendSyncDatagram(xLogMessage, aSyncDatagram);
      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logTransportationDropDatagram(String direction, SyncDatagram aSyncDatagram) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("TP").append(',').append(direction);
      xLogMessage.append(',').append("DGDRP").append(',');
      appendSyncDatagram(xLogMessage, aSyncDatagram);
      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }

   public static final void logUidsNotMatching(int anOp, int oldUid, int newUid, Object msg) {
      ReusableStringBuffer xReusableStringBuffer = checkOutReusableStringBuffer();
      StringBuffer xLogMessage = xReusableStringBuffer.getStringBuffer();
      xLogMessage.append("AP").append(',').append("NMUIDS").append(',').append("OP=").append(anOp);
      xLogMessage.append(',').append("UID=").append(oldUid);
      xLogMessage.append(',').append("UID=").append(newUid);
      xLogMessage.append(',').append("MSG=").append(msg);
      log(xLogMessage, 0);
      checkInReusableStringBuffer(xReusableStringBuffer);
   }
}
