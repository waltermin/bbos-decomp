package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.IntIntHashtable;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.api.SyncAgentListener;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatistics;
import net.rim.device.internal.synchronization.ota.api.SyncAgentStatisticsCollector;
import net.rim.device.internal.system.InternalServices;

final class OTASyncProgressScreen extends ActivationScreen implements SyncAgentListener, ListFieldCallback, ActivationEventQueueCallback {
   private IntIntHashtable _currentCollections = (IntIntHashtable)(new Object());
   private SyncAgentStatistics[] _saStats = new Object[0];
   private OTASyncProgressScreen$SyncAgentStatisticsComparator _saStatsComparator = new OTASyncProgressScreen$SyncAgentStatisticsComparator();
   private ListField _listField;
   private ActivationApp _app;
   private long _lastUpdated = 0;
   private static final long UPDATE_STATISTICS;
   private static StringBuffer _sb = (StringBuffer)(new Object());

   OTASyncProgressScreen(ActivationApp app, String statusInfo, long serviceId) {
      super(app, statusInfo, null, null);
      super._currentSid = serviceId;
      this._app = app;
      SyncAgentStatisticsCollector.fillInAllSyncAgentStatistics(this._saStats);
      Arrays.sort(this._saStats, this._saStatsComparator);
      this._listField = (ListField)(new Object(this._saStats.length));
      this._listField.setCallback(this);
      if (statusInfo != null && statusInfo.length() > 0) {
         this.add((Field)(new Object()));
      }

      this.add(this._listField);
      SyncAgent.getSingletonInstance().registerListener(this);
   }

   @Override
   public final void onEventFromActivationEventQueue(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 1) {
         synchronized (this._app.getAppEventLock()) {
            this.update((SyncAgentStatistics)object0, data0 == 18);
         }
      }
   }

   private final void update(SyncAgentStatistics saStats, boolean remove) {
      int index = Arrays.binarySearch(this._saStats, saStats, this._saStatsComparator, 0, this._saStats.length);
      if (index >= 0) {
         if (remove) {
            Arrays.removeAt(this._saStats, index);
            this._listField.delete(index);
         } else {
            this._saStats[index] = saStats;
            this._listField.invalidate(index);
         }
      } else {
         if (!remove) {
            index = -index - 1;
            Arrays.insertAt(this._saStats, saStats, index);
            this._listField.insert(index);
         }
      }
   }

   @Override
   public final void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      SyncAgentStatistics saStats = this._saStats[index];
      String name = saStats.getSyncAgentUrl().getDatabaseName();
      int current = saStats.getTotalNumberOfExecutedOperations();
      int total = saStats.getTotalNumberOfOperations();
      _sb.setLength(0);
      if (current != total && saStats.getTotalNumberOf100PercentHits() < 1) {
         if (total >= 0 && current != 0) {
            _sb.append(current).append('/').append(total).append(" (");
            _sb.append(total > 0 ? current * 100 / total : 100);
            _sb.append("%)");
         } else {
            _sb.append(ActivationApp._resources.getString(105));
         }
      } else if (saStats.getTotalNumberOfFailedOperations() == 0) {
         _sb.append(ActivationApp._resources.getString(104));
      } else {
         _sb.append(ActivationApp._resources.getString(155));
      }

      int widthDrawn = graphics.drawText(_sb, 0, _sb.length(), 0, y, 5, Display.getWidth());
      graphics.drawText(name, 0, y, 70, Display.getWidth() - widthDrawn - 2);
   }

   @Override
   public final int getPreferredWidth(ListField listField) {
      return Display.getWidth();
   }

   @Override
   public final Object get(ListField listField, int index) {
      return this._saStats[index];
   }

   @Override
   public final int indexOfList(ListField listField, String prefix, int start) {
      return this._listField.getSelectedIndex();
   }

   @Override
   public final void onSyncAgentEvent(int eventID, Object object) {
      switch (eventID) {
         case 16:
         case 17:
         case 18:
         default:
            SyncAgentStatistics saStats = null;
            if (object instanceof Object) {
               saStats = (SyncAgentStatistics)object;
            }

            if (InternalServices.getUptime() - this._lastUpdated > 5000
               || saStats != null && !this._currentCollections.containsKey(saStats.getSyncAgentUrl().hashCode())) {
               this._currentCollections.put(saStats.getSyncAgentUrl().hashCode(), Integer.MIN_VALUE);
               this._lastUpdated = InternalServices.getUptime();
               if (this._app != null) {
                  this._app.activationEventOccurred(this, 1, eventID, 0, object, null);
               }
            }
         case 15:
      }
   }
}
