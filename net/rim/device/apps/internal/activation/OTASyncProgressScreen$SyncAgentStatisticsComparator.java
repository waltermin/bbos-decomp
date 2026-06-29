package net.rim.device.apps.internal.activation;

import net.rim.device.api.util.Comparator;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatistics;
import net.rim.device.internal.synchronization.ota.api.SyncAgentUrl;

final class OTASyncProgressScreen$SyncAgentStatisticsComparator implements Comparator {
   @Override
   public final int compare(Object o1, Object o2) {
      SyncAgentUrl saUrl1 = ((SyncAgentStatistics)o1).getSyncAgentUrl();
      SyncAgentUrl saUrl2 = ((SyncAgentStatistics)o2).getSyncAgentUrl();
      return StringUtilities.compareToIgnoreCase(saUrl1.getDatabaseName(), saUrl2.getDatabaseName(), 1701707776);
   }
}
