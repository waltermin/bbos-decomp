package net.rim.device.internal.synchronization.ota.api;

import java.util.Enumeration;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.IntHashtable;
import net.rim.vm.Array;

public final class SyncAgentStatisticsCollector {
   private IntHashtable _statistics;
   private static final long GUID;

   private static final SyncAgentStatisticsCollector getSingletonInstance() {
      ApplicationRegistry theApplicationRegistry = ApplicationRegistry.getApplicationRegistry();
      SyncAgentStatisticsCollector xInstance = (SyncAgentStatisticsCollector)theApplicationRegistry.getOrWaitFor(8620019904824715973L);
      if (xInstance == null) {
         xInstance = new SyncAgentStatisticsCollector();
         theApplicationRegistry.put(8620019904824715973L, xInstance);
      }

      return xInstance;
   }

   private SyncAgentStatisticsCollector() {
      PersistentObject xPersistentObject = PersistentStore.getPersistentObject(8620019904824715973L);
      this._statistics = this.performIntegerityCheck((IntHashtable)xPersistentObject.getContents());
      xPersistentObject.setContents(this._statistics, 51);
      xPersistentObject.commit();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final IntHashtable performIntegerityCheck(IntHashtable statistics) {
      if (statistics == null) {
         return (IntHashtable)(new Object(50));
      }

      int[] xKeys = new int[statistics.size()];
      statistics.keysToArray(xKeys);

      for (int xIndex = xKeys.length - 1; xIndex > -1; xIndex--) {
         int xKey = xKeys[xIndex];
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            SyncAgentStatistics t = (SyncAgentStatistics)statistics.get(xKey);
            SyncAgentUrl xSyncAgentUrl = t.getSyncAgentUrl();
            if (t.isIntegrityChecksAllowed()) {
               if (ServiceBook.getSB().getRecordByCidAndSid("sync", xSyncAgentUrl.getSid()) == null) {
                  statistics.remove(xKey);
                  var8 = false;
               } else {
                  var8 = false;
               }
            } else {
               var8 = false;
            }
         } finally {
            if (var8) {
               statistics.remove(xKey);
               continue;
            }
         }
      }

      return statistics;
   }

   public static final SyncAgentStatistics getSyncAgentStatisticsFor(SyncAgentUrl aUrl) {
      return getSyncAgentStatisticsFor(aUrl, true);
   }

   public static final SyncAgentStatistics getSyncAgentStatisticsFor(SyncAgentUrl aUrl, boolean allowIntegrityChecks) {
      SyncAgentStatisticsCollector xInstance = getSingletonInstance();
      boolean xFireEvent = false;
      SyncAgentStatistics xSyncAgentStatistics;
      synchronized (xInstance) {
         xSyncAgentStatistics = (SyncAgentStatistics)xInstance._statistics.get(aUrl.hashCode());
         if (xSyncAgentStatistics == null) {
            xSyncAgentStatistics = new SyncAgentStatistics(aUrl, allowIntegrityChecks);
            xInstance._statistics.put(aUrl.hashCode(), xSyncAgentStatistics);
            xFireEvent = true;
         }
      }

      if (xFireEvent) {
         SyncAgent.getSingletonInstance().notifyListenersWith(16, xSyncAgentStatistics);
      }

      return xSyncAgentStatistics;
   }

   public static final void purgeSyncAgentStatisticsFor(int aSyncAgentUrlHashCode) {
      SyncAgentStatisticsCollector xInstance = getSingletonInstance();
      boolean xFireEvent = false;
      SyncAgentStatistics xSyncAgentStatistics;
      synchronized (xInstance) {
         xSyncAgentStatistics = (SyncAgentStatistics)xInstance._statistics.remove(aSyncAgentUrlHashCode);
         if (xSyncAgentStatistics != null) {
            xFireEvent = true;
         }
      }

      if (xFireEvent) {
         SyncAgent.getSingletonInstance().notifyListenersWith(18, xSyncAgentStatistics);
      }
   }

   public static final void purgeSyncAgentStatisticsFor(SyncAgentUrl aUrl) {
      purgeSyncAgentStatisticsFor(aUrl.hashCode());
   }

   public static final void fillInAllSyncAgentStatistics(SyncAgentStatistics[] aSyncAgentStatistics) {
      SyncAgentStatisticsCollector xInstance = getSingletonInstance();
      synchronized (xInstance) {
         int xStatisticsSize = xInstance._statistics.size();
         if (aSyncAgentStatistics.length != xStatisticsSize) {
            Array.resize(aSyncAgentStatistics, xStatisticsSize);
         }

         if (xStatisticsSize != 0) {
            Enumeration xList = xInstance._statistics.elements();
            int xIndex = 0;

            while (xList.hasMoreElements()) {
               SyncAgentStatistics xSyncAgentStatistics = (SyncAgentStatistics)xList.nextElement();
               SyncAgentUrl syncAgentUrl = xSyncAgentStatistics.getSyncAgentUrl();
               if (!xSyncAgentStatistics.isIntegrityChecksAllowed()
                  || syncAgentUrl != null && syncAgentUrl.getExternalToSync()
                  || SyncAgentConnections.getConnectionBy(syncAgentUrl) != null) {
                  aSyncAgentStatistics[xIndex] = xSyncAgentStatistics;
                  xIndex++;
               }
            }

            Array.resize(aSyncAgentStatistics, xIndex);
         }
      }
   }

   public static final SyncAgentStatistics[] getAllSyncAgentStatistics() {
      SyncAgentStatisticsCollector xInstance = getSingletonInstance();
      synchronized (xInstance) {
         SyncAgentStatistics[] xSyncAgentStatistics = new SyncAgentStatistics[xInstance._statistics.size()];
         fillInAllSyncAgentStatistics(xSyncAgentStatistics);
         return xSyncAgentStatistics;
      }
   }
}
