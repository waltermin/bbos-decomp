package net.rim.device.internal.synchronization.ota.session;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Hash;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.vm.Persistence;

public final class SessionManagerState implements Persistable {
   private int _expectedChangeListId = 1;
   private int _lastChangeListId;
   private int _flags = 1;
   private int _logMessageId;
   private String _logMessage;
   private static final int FLAGS_CONFIG_REQUEST = 1;
   private static final int FLAGS_SUSPEND_COMMANDS_REQUEST = 2;
   private static final int FLAGS_RESUME_COMMANDS_REQUEST = 4;
   private static final int FLAGS_SERVER_IS_SUSPENDED = 8;
   private static final int FLAGS_DEVICE_LOG_REQUEST = 16;

   private static final long getKeyFor(long sid) {
      String xCompleteKey = ((StringBuffer)(new Object("net.rim.device.internal.synchronization.ota.session.SessionManagerState:"))).append(sid).toString();
      return Hash.bytesToLong(xCompleteKey.getBytes());
   }

   private static final PersistentObject getPersistentObjectFor(long sid) {
      return RIMPersistentStore.getPersistentObject(getKeyFor(sid));
   }

   public static final SessionManagerState get(long sid) {
      PersistentObject xPersistentObject = getPersistentObjectFor(sid);
      Object xObject = xPersistentObject.getContents();
      SessionManagerState xSessionManagerState = xObject == null ? new SessionManagerState() : (SessionManagerState)xObject;
      xPersistentObject.setContents(xSessionManagerState, 51);
      if (xObject == null) {
         xPersistentObject.commit();
      }

      return xSessionManagerState;
   }

   public static final void purge(long sid) {
      PersistentStore.destroyPersistentObject(getKeyFor(sid));
   }

   private SessionManagerState() {
      this._flags = 1;
   }

   public final void shouldSendSuspendCommandsRequest(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 2);
   }

   public final void shouldSendResumeCommandsRequest(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 4);
   }

   public final void shouldSendRequestForConfiguration(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 1);
   }

   public final boolean shouldSendSuspendCommandsRequest() {
      return Helper.getFlagValue(this._flags, 2);
   }

   public final boolean shouldSendResumeCommandsRequest() {
      return Helper.getFlagValue(this._flags, 4);
   }

   public final boolean shouldSendRequestForConfiguration() {
      return Helper.getFlagValue(this._flags, 1);
   }

   public final void serverSuspended(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 8);
   }

   public final boolean serverSuspended() {
      return Helper.getFlagValue(this._flags, 8);
   }

   public final boolean shouldSendDeviceLogMessage() {
      return Helper.getFlagValue(this._flags, 16);
   }

   public final void shouldSendDeviceLogMessage(boolean value) {
      this._flags = Helper.setFlagValue(this._flags, value, 16);
   }

   public final void setLogMessageId(int logMessageId) {
      this._logMessageId = logMessageId;
   }

   public final void setLogMessage(String logMessage) {
      this._logMessage = logMessage;
   }

   public final int getLogMessageId() {
      return this._logMessageId;
   }

   public final String getLogMessage() {
      return this._logMessage;
   }

   public final int getNextSessionId() {
      int returnValue = 0;

      do {
         returnValue = (int)System.currentTimeMillis() & 2147483647;
      } while (returnValue == 0);

      return returnValue;
   }

   public final int getExpectedChangeListId() {
      return this._expectedChangeListId;
   }

   public final int getLastChangeListId() {
      return this._lastChangeListId;
   }

   public final void incrementExpectedChangeListId() {
      this._expectedChangeListId++;
      if (this._expectedChangeListId <= 0) {
         this._expectedChangeListId = 1;
      }

      Persistence.commit(this, true);
   }

   public final void setLastChangeListId(int value) {
      this._lastChangeListId = value;
      Persistence.commit(this, true);
   }

   public final void decrementLastChangeListId() {
      this._lastChangeListId--;
      if (this._lastChangeListId < 0) {
         this._lastChangeListId = 0;
      }

      Persistence.commit(this, true);
   }

   public final void incrementLastChangeListId() {
      this._lastChangeListId++;
      if (this._lastChangeListId <= 0) {
         this._lastChangeListId = 1;
      }

      Persistence.commit(this, true);
   }

   public final void resetChangeListIds() {
      this._expectedChangeListId = 1;
      this._lastChangeListId = 0;
      Persistence.commit(this, true);
   }

   public final void resetAll() {
      this.resetChangeListIds();
      this._flags = 1;
      Persistence.commit(this, true);
   }
}
