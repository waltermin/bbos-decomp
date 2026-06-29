package net.rim.device.internal.synchronization.ota.api;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.lowmemory.LowMemoryFailedListener;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.SimpleSortingIntVector;
import net.rim.device.internal.proxy.Proxy;

public final class SyncAgentConnections implements LowMemoryFailedListener, GlobalEventListener {
   private IntHashtable _syncAgentConnections = new IntHashtable();
   private SimpleSortingIntVector _syncAgentConnectionsPriority = new SimpleSortingIntVector();
   private byte _lowMemoryPollCounter;
   private boolean _lowInMemory;
   private static final long APPLICATION_REG_GUID = 2945150083663420434L;
   private static final byte ALL_CONNECTIONS = 1;
   private static final byte ALL_CONNECTIONS_WITH_PENDING_CHANGES = 2;
   private static final byte ANY_CONNECTION_WITH_PENDING_CHANGES = 4;
   private static final byte ALL_CONNECTIONS_UNINITALIZED = 8;
   private static final byte ANY_CONNECTION_UNINITALIZED = 16;
   private static final byte ALL_CONNECTIONS_INITALIZED = 50;
   private static final byte ANY_CONNECTIONS_OPERATIONS_UNCOMPLETE = 100;
   private static SyncAgentConnections _instance;

   private static final void loadSingletonInstance() {
      if (_instance == null) {
         ApplicationRegistry theApplicationRegistry = ApplicationRegistry.getApplicationRegistry();
         _instance = (SyncAgentConnections)theApplicationRegistry.getOrWaitFor(2945150083663420434L);
         if (_instance == null) {
            _instance = new SyncAgentConnections();
            _instance._syncAgentConnectionsPriority.setSortComparator(new SyncAgentConnections$SyncAgentPriorityComparator(null), false, false);
            _instance._syncAgentConnectionsPriority.setSortAsAdded((short)2);
            LowMemoryManager.addLowMemoryFailedListener(_instance);
            Proxy.getInstance().addGlobalEventListener(_instance);
            theApplicationRegistry.put(2945150083663420434L, _instance);
         }
      }
   }

   private SyncAgentConnections() {
   }

   private static final SyncAgentConnections getInstance() {
      return _instance;
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 945659952435832745L) {
         getInstance()._lowMemoryPollCounter = -1;
      }
   }

   @Override
   public final void lowMemoryManagerFailed() {
      getInstance()._lowInMemory = true;
   }

   static final synchronized void checkForLowMemory() {
      if ((++getInstance()._lowMemoryPollCounter & 63) == 0) {
         getInstance()._lowInMemory = false;
         LowMemoryManager.poll();
      }
   }

   public static final boolean isMemoryLow() {
      return getInstance()._lowInMemory;
   }

   public static final Object getLock() {
      return getInstance();
   }

   public static final boolean isConnectionRegistered(SyncAgentUrl aSyncAgentUrl) {
      return isConnectionRegistered(aSyncAgentUrl.hashCode());
   }

   private static final boolean isConnectionRegistered(int hashCode) {
      synchronized (getInstance()) {
         return getInstance()._syncAgentConnections.get(hashCode) != null;
      }
   }

   public static final void registerConnection(SyncAgentConnection aSyncAgentConnection) {
      synchronized (getInstance()) {
         if (isConnectionRegistered(aSyncAgentConnection.getUrl())) {
            throw new IllegalStateException();
         }

         getInstance()._syncAgentConnections.put(aSyncAgentConnection.hashCode(), aSyncAgentConnection);
         getInstance()._syncAgentConnectionsPriority.addElement(aSyncAgentConnection.hashCode());
         Logger.logAddingRemovingConnection(
            true, aSyncAgentConnection.getPacketLevelPriority(), aSyncAgentConnection.getDatabaseVersion(), aSyncAgentConnection.getUrl().getDatabaseName()
         );
      }
   }

   public static final void deregisterConnection(SyncAgentConnection aSyncAgentConnection) {
      synchronized (getInstance()) {
         if (isConnectionRegistered(aSyncAgentConnection.hashCode())) {
            if (aSyncAgentConnection.getPacketLevelPriority() != 1275) {
               getInstance()
                  ._syncAgentConnectionsPriority
                  .removeElementAt(getInstance()._syncAgentConnectionsPriority.bestGuessBinarySearch(aSyncAgentConnection.hashCode()));
            } else {
               getInstance()._syncAgentConnectionsPriority.removeElement(aSyncAgentConnection.hashCode());
            }

            getInstance()._syncAgentConnections.remove(aSyncAgentConnection.hashCode());
            Logger.logAddingRemovingConnection(
               false, aSyncAgentConnection.getPacketLevelPriority(), aSyncAgentConnection.getDatabaseVersion(), aSyncAgentConnection.getUrl().getDatabaseName()
            );
         }
      }
   }

   static final void deregisterAllConnections() {
      if (!getInstance()._syncAgentConnections.isEmpty() && !getInstance()._syncAgentConnectionsPriority.isEmpty()) {
         synchronized (getInstance()) {
            getInstance()._syncAgentConnections.clear();
            getInstance()._syncAgentConnectionsPriority.removeAllElements();
            Logger.logAddingRemovingConnection(false, 0, 0, "deregister all");
         }
      }
   }

   public static final void closeConnections(long sid, String aDataSourceName, String aDatabaseName) {
      Vector xList = null;
      xList = getAllConnectionsBy(null, sid, aDataSourceName, aDatabaseName);
      boolean deregisterAll = xList.size() == getInstance()._syncAgentConnections.size();
      if (deregisterAll) {
         deregisterAllConnections();
      }

      for (int xIndex = xList.size() - 1; xIndex > -1; xIndex--) {
         SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)xList.elementAt(xIndex);
         xSyncAgentConnection.close(true, deregisterAll);
      }
   }

   public static final Vector getAllConnectionsWithPendingChangesFor(Vector aList, long sid, String aDataSourceName, String aDatabaseName) {
      return getConnectionsBy(aList, sid, aDataSourceName, aDatabaseName, 2);
   }

   public static final Vector getAllUnInitializedConnectionsFor(Vector aList, long sid, String aDataSourceName, String aDatabaseName) {
      return getConnectionsBy(aList, sid, aDataSourceName, aDatabaseName, 8);
   }

   public static final Vector getAllConnectionsBy(Vector aList, long sid, String aDataSourceName, String aDatabaseName) {
      return getConnectionsBy(aList, sid, aDataSourceName, aDatabaseName, 1);
   }

   public static final boolean isTherePendingChangesForConnectionsWith(Vector aList, long sid, String aDataSourceName, String aDatabaseName) {
      return getConnectionsBy(aList, sid, aDataSourceName, aDatabaseName, 4).size() != 0;
   }

   public static final boolean isAllConnectionsInitializedFor(Vector aList, long sid, String aDataSourceName, String aDatabaseName) {
      return getConnectionsBy(aList, sid, aDataSourceName, aDatabaseName, 16).size() == 0;
   }

   private static final void notifySyncConnectionsWith(Vector selectedConnections, int anEventId, Object aMessage) {
      for (int xIndex = selectedConnections.size() - 1; xIndex > -1; xIndex--) {
         SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)selectedConnections.elementAt(xIndex);
         xSyncAgentConnection.onSessionEvent(anEventId, aMessage);
      }
   }

   public static final void encryptChangeListsFor(Vector selectedConnections) {
      notifySyncConnectionsWith(selectedConnections, 64, null);
   }

   public static final void resetConnections(Vector selectedConnections) {
      notifySyncConnectionsWith(selectedConnections, 56, null);
   }

   public static final void suspendConnections(Vector selectedConnections) {
      notifySyncConnectionsWith(selectedConnections, 58, null);
   }

   public static final boolean allSyncOperationsComplete(Vector aList, long sid, String aDataSourceName, String aDatabaseName) {
      return getConnectionsBy(aList, sid, aDataSourceName, aDatabaseName, 100).size() == 0;
   }

   public static final SyncAgentConnection getConnectionBy(SyncAgentUrl aSyncAgentUrl) {
      return aSyncAgentUrl == null ? null : (SyncAgentConnection)getInstance()._syncAgentConnections.get(aSyncAgentUrl.hashCode());
   }

   private static final void rebuildPriorities() {
      synchronized (getInstance()) {
         Enumeration syncAgentConnections = getInstance()._syncAgentConnections.elements();
         getInstance()._syncAgentConnectionsPriority.removeAllElements();
         SyncAgentConnection aSyncAgentConnection = null;

         while (syncAgentConnections.hasMoreElements()) {
            aSyncAgentConnection = (SyncAgentConnection)syncAgentConnections.nextElement();
            getInstance()._syncAgentConnectionsPriority.addElement(aSyncAgentConnection.hashCode());
         }
      }
   }

   private static final Vector getConnectionsBy(Vector aList, long sid, String aDataSourceName, String aDatabaseName, int flags) {
      synchronized (getInstance()) {
         if ("*".equals(aDatabaseName)) {
            aDatabaseName = null;
         }

         if ("*".equals(aDataSourceName)) {
            aDataSourceName = null;
         }

         if (aList == null) {
            aList = new Vector(0);
         } else {
            aList.setSize(0);
         }

         int xRequiredFields = 0;
         int xResultFields = 0;
         xRequiredFields |= sid != -1 ? 4 : 0;
         xRequiredFields |= aDataSourceName != null ? 2 : 0;
         xRequiredFields |= aDatabaseName != null ? 1 : 0;
         if (xRequiredFields != 0) {
            if (getInstance()._syncAgentConnectionsPriority.size() != getInstance()._syncAgentConnections.size()) {
               Logger.logErrorMessage("AG,OOS collection rebuild");
               rebuildPriorities();
            }

            for (int xIndex = 0; xIndex <= getInstance()._syncAgentConnectionsPriority.size() - 1; xIndex++) {
               int xHashCode = getInstance()._syncAgentConnectionsPriority.elementAt(xIndex);
               SyncAgentConnection xSyncAgentConnection = (SyncAgentConnection)getInstance()._syncAgentConnections.get(xHashCode);
               SyncAgentUrl xSyncAgentUrl = xSyncAgentConnection.getUrl();
               if ((xRequiredFields & 4) == 4 && xSyncAgentUrl.getSid() == sid) {
                  xResultFields |= 4;
               }

               if ((xRequiredFields & 2) == 2 && xSyncAgentUrl.getDataSourceName().equals(aDataSourceName)) {
                  xResultFields |= 2;
               }

               if ((xRequiredFields & 1) == 1 && xSyncAgentUrl.getDatabaseName().equals(aDatabaseName)) {
                  xResultFields |= 1;
               }

               if (xRequiredFields == xResultFields) {
                  boolean xAddItToTheList = false;
                  boolean xReturnWithTheList = false;
                  switch (flags) {
                     case 1:
                        xAddItToTheList = true;
                        break;
                     case 2:
                        xAddItToTheList = xSyncAgentConnection.isTherePendingChanges();
                        break;
                     case 4:
                        xAddItToTheList = xSyncAgentConnection.isTherePendingChanges();
                        xReturnWithTheList = true;
                        break;
                     case 8:
                        xAddItToTheList = !xSyncAgentConnection.isInitialized();
                        break;
                     case 16:
                        xAddItToTheList = !xSyncAgentConnection.isInitialized();
                        xReturnWithTheList = true;
                        break;
                     case 50:
                        xAddItToTheList = !xSyncAgentConnection.isInitialized();
                        break;
                     case 100:
                        xAddItToTheList = !xSyncAgentConnection.isSyncOperationsComplete();
                        xReturnWithTheList = true;
                  }

                  if (xAddItToTheList) {
                     aList.addElement(xSyncAgentConnection);
                     if (xReturnWithTheList) {
                        return aList;
                     }
                  }
               }

               xResultFields = 0;
            }
         }

         return aList;
      }
   }

   static {
      loadSingletonInstance();
   }
}
