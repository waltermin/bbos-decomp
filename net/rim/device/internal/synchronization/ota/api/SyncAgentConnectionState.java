package net.rim.device.internal.synchronization.ota.api;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.CRC32;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.Persistable;
import net.rim.device.internal.synchronization.ota.util.Helper;
import net.rim.vm.Persistence;

final class SyncAgentConnectionState implements Persistable {
   private int _appModuleHandle;
   private int _appModualCRC32;
   private Vector _changesList;
   private byte _flags;
   private byte _contextualRetryCount;
   private static final long GUID = -5303286210729691565L;
   private static final byte INITIALIZED_STATE = 1;
   private static final byte SUSPENDED_STATE = 2;
   private static final byte DISABLED_STATE = 3;
   private static IntHashtable _states;

   private static final void loadStates() {
      ApplicationRegistry theApplicationRegistry = ApplicationRegistry.getApplicationRegistry();
      _states = (IntHashtable)theApplicationRegistry.getOrWaitFor(-5303286210729691565L);
      if (_states == null) {
         PersistentObject xPersistentObject = PersistentStore.getPersistentObject(-5303286210729691565L);
         _states = (IntHashtable)xPersistentObject.getContents();
         if (_states == null) {
            _states = new IntHashtable();
            xPersistentObject.setContents(_states, 51);
            xPersistentObject.commit();
         }

         theApplicationRegistry.put(-5303286210729691565L, _states);
         performIntegerityCheck(_states);
      }
   }

   private static final void performIntegerityCheck(IntHashtable states) {
      if (!states.isEmpty()) {
         int[] xKeys = new int[states.size()];
         states.keysToArray(xKeys);

         for (int xIndex = xKeys.length - 1; xIndex > -1; xIndex--) {
            int xKey = xKeys[xIndex];
            SyncAgentConnectionState xSyncAgentConnectionState = (SyncAgentConnectionState)states.get(xKey);
            if (!xSyncAgentConnectionState.isValid()) {
               states.remove(xKey);
               SyncAgentStatisticsCollector.purgeSyncAgentStatisticsFor(xKey);
            }
         }
      }
   }

   static final SyncAgentConnectionState create(SyncAgentUrl aSyncAgentUrl) {
      synchronized (_states) {
         int xKey = aSyncAgentUrl.hashCode();
         SyncAgentConnectionState xSyncAgentConnectionState = (SyncAgentConnectionState)_states.get(xKey);
         if (xSyncAgentConnectionState == null) {
            xSyncAgentConnectionState = new SyncAgentConnectionState();
            _states.put(xKey, xSyncAgentConnectionState);
         }

         return xSyncAgentConnectionState;
      }
   }

   static final void purge(SyncAgentUrl aSyncAgentUrl) {
      synchronized (_states) {
         _states.remove(aSyncAgentUrl.hashCode());
      }
   }

   final synchronized void setAppModualHandle(int appModuleHandle) {
      this._appModuleHandle = appModuleHandle;

      try {
         byte[] xAppModuleHash = CodeModuleManager.getModuleHash(appModuleHandle);
         this._appModualCRC32 = CRC32.update(-1, xAppModuleHash);
      } finally {
         return;
      }
   }

   final synchronized boolean isValid() {
      try {
         byte[] xAppModuleHash = CodeModuleManager.getModuleHash(this._appModuleHandle);
         int xAppModualCRC32 = CRC32.update(-1, xAppModuleHash);
         return xAppModualCRC32 == this._appModualCRC32;
      } finally {
         ;
      }
   }

   final synchronized void resetChangeLists() {
      if (this._changesList != null) {
         this._changesList.setSize(0);
      }
   }

   final synchronized void resetAll() {
      this.setInitialized(false);
      this.setSuspended(false);
      this.setDisabled(false);
      this.resetChangeLists();
      this.resetContextualRetryCount();
      Persistence.commit(this, true);
   }

   final synchronized void setInitialized(boolean value) {
      this._flags = (byte)Helper.setFlagValue(this._flags, value, 1);
      Persistence.commit(this, true);
   }

   final synchronized boolean isInitialized() {
      return Helper.getFlagValue(this._flags, 1);
   }

   final synchronized void setSuspended(boolean value) {
      this._flags = (byte)Helper.setFlagValue(this._flags, value, 2);
      Persistence.commit(this, true);
   }

   final synchronized boolean isSuspended() {
      return Helper.getFlagValue(this._flags, 2);
   }

   final synchronized void setDisabled(boolean value) {
      this._flags = (byte)Helper.setFlagValue(this._flags, value, 3);
      Persistence.commit(this, true);
   }

   final synchronized boolean isDisabled() {
      return Helper.getFlagValue(this._flags, 3);
   }

   final synchronized void encryptContent(boolean encrypt) {
      if (this._changesList != null && !this._changesList.isEmpty()) {
         for (int xIndex = this._changesList.size() - 1; xIndex > -1; xIndex--) {
            SyncApplicationChangeList xSyncApplicationChangeList = (SyncApplicationChangeList)this._changesList.elementAt(xIndex);
            xSyncApplicationChangeList.encrypt(encrypt);
         }
      }
   }

   final synchronized Vector getChangesList() {
      if (this._changesList == null) {
         this._changesList = new Vector(0);
      }

      return this._changesList;
   }

   final synchronized SyncApplicationChange getChangeBy(int aRefId) {
      SyncApplicationChange aSyncApplicationChange = ((SyncApplicationChangeList)this._changesList.firstElement()).getByRefId(aRefId);
      if (aSyncApplicationChange != null) {
         return aSyncApplicationChange;
      }

      Enumeration aChangesList = this._changesList.elements();

      while (aChangesList.hasMoreElements()) {
         SyncApplicationChangeList aChangeList = (SyncApplicationChangeList)aChangesList.nextElement();
         aSyncApplicationChange = aChangeList.getByRefId(aRefId);
         if (aSyncApplicationChange != null) {
            break;
         }
      }

      return aSyncApplicationChange;
   }

   final void resetContextualRetryCount() {
      this._contextualRetryCount = -128;
   }

   final void incrementContextualRetryCount() {
      if (this._contextualRetryCount + 1 <= 127) {
         this._contextualRetryCount++;
      }
   }

   final short getContextualRetryCount() {
      return (short)(this._contextualRetryCount - -128);
   }

   static {
      loadStates();
   }
}
