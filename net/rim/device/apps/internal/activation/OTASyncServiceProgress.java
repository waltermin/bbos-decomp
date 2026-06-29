package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.LongEnumeration;
import net.rim.device.api.util.LongHashtable;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatistics;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatisticsCollector;
import net.rim.vm.Array;

final class OTASyncServiceProgress {
   private SyncAgentStatistics[] _saStats = new SyncAgentStatistics[0];
   private String[] _otherFailedDatabases = new String[0];
   private OTASyncServiceProgress$OTASyncServiceState _state;
   private boolean _pimConfigRequestPending = true;
   private int _percentComplete = 100;
   private long _serviceId;
   private static long GUID_PERSISTED_STATES = 6390965641451050308L;
   static final int SYNC_STATE_ENABLED = 0;
   static final int SYNC_STATE_CANCELED = 1;
   static final int SYNC_STATE_DISABLED = 2;
   static final int SYNC_STATE_CONFIG_PENDING = 3;
   static final int SYNC_STATE_NOT_AVAILABLE = 4;
   static final int SYNC_STATE_PENDING_ENABLED = 5;
   static final int SYNC_STATE_PENDING_DISABLE = 6;
   static final int SYNC_STATE_PENDING_CANCEL = 7;

   static final LongHashtable loadServicesProgressFromPersistence() {
      ApplicationRegistry appRegistry = ApplicationRegistry.getApplicationRegistry();
      LongHashtable states = (LongHashtable)appRegistry.get(GUID_PERSISTED_STATES);
      if (states == null) {
         PersistentObject persistentObject = PersistentStore.getPersistentObject(GUID_PERSISTED_STATES);
         states = (LongHashtable)persistentObject.getContents();
         if (states == null) {
            states = new LongHashtable();
            persistentObject.setContents(states, 51);
            persistentObject.commit();
         }

         appRegistry.put(GUID_PERSISTED_STATES, states);
      }

      LongHashtable serviceProgressObjects = new LongHashtable();
      LongEnumeration stateEnum = states.keys();

      while (stateEnum.hasMoreElements()) {
         long serviceId = stateEnum.nextElement();
         serviceProgressObjects.put(serviceId, new OTASyncServiceProgress(serviceId, (OTASyncServiceProgress$OTASyncServiceState)states.get(serviceId)));
      }

      return serviceProgressObjects;
   }

   OTASyncServiceProgress(long serviceId) {
      LongHashtable states = (LongHashtable)ApplicationRegistry.getApplicationRegistry().get(GUID_PERSISTED_STATES);
      if (!states.containsKey(serviceId)) {
         states.put(serviceId, new OTASyncServiceProgress$OTASyncServiceState(null));
         PersistentObject.commit(states);
      }

      this._state = (OTASyncServiceProgress$OTASyncServiceState)states.get(serviceId);
      this._serviceId = serviceId;
   }

   private OTASyncServiceProgress(long serviceId, OTASyncServiceProgress$OTASyncServiceState state) {
      this._serviceId = serviceId;
      this._state = state;
   }

   final SyncAgentStatistics[] getSyncAgentStatistics() {
      return this._saStats;
   }

   final synchronized void collectSyncAgentStatistics() {
      SyncAgentStatisticsCollector.fillInAllSyncAgentStatistics(this._saStats);
   }

   final boolean hasOtherFailedDatabases() {
      return this._otherFailedDatabases.length != 0;
   }

   final synchronized void addOtherFailedDatabase(String database) {
      synchronized (this._otherFailedDatabases) {
         Arrays.add(this._otherFailedDatabases, database);
      }
   }

   final String[] getOtherFailedDatabases() {
      return this._otherFailedDatabases;
   }

   final synchronized void resetOtherFailedDatabases() {
      synchronized (this._otherFailedDatabases) {
         Array.resize(this._otherFailedDatabases, 0);
      }
   }

   final boolean getPimConfigRequestPending() {
      return this._pimConfigRequestPending;
   }

   final void setPimConfigRequestPending(boolean pending) {
      this._pimConfigRequestPending = pending;
   }

   final synchronized int getPercentComplete() {
      return this._percentComplete;
   }

   final synchronized void setPercentComplete(int percent) {
      this._percentComplete = percent;
   }

   final int getSyncState() {
      return this._state._syncState;
   }

   final synchronized void setSyncState(int state) {
      this._state._syncState = state;
      this._state.commitState();
   }

   final boolean getPimConfigRequestRecieved() {
      return this._state._pimConfigRequestRecieved;
   }

   final synchronized void setPimConfigRequestRecieved(boolean recieved) {
      this._state._pimConfigRequestRecieved = recieved;
      this._state.commitState();
   }

   final synchronized void purgeState() {
      LongHashtable states = (LongHashtable)ApplicationRegistry.getApplicationRegistry().get(GUID_PERSISTED_STATES);
      if (states != null && states.containsKey(this._serviceId)) {
         states.remove(this._serviceId);
         PersistentObject.commit(states);
      }
   }
}
