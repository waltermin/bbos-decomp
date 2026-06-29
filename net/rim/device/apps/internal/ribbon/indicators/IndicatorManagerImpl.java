package net.rim.device.apps.internal.ribbon.indicators;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.ribbon.RibbonComponent$RibbonComponentChangeListener;
import net.rim.device.apps.api.ribbon.indicators.CountIndicator;
import net.rim.device.apps.api.ribbon.indicators.Indicator;
import net.rim.device.apps.api.ribbon.indicators.IndicatorManager;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.vm.Array;
import net.rim.vm.Monitor;
import net.rim.vm.WeakReference;

public class IndicatorManagerImpl extends IndicatorManager implements RealtimeClockListener, TestPoint {
   private IndicatorManagerImpl$SuspendInfo _suspendInfo = new IndicatorManagerImpl$SuspendInfo();
   private WeakReference[] _wrIndicatorChangedListeners = new WeakReference[0];
   private IndicatorDisplay[] _sortedIndicators = new IndicatorDisplay[0];
   private IndicatorComparator _indicatorComparator = new IndicatorComparator();
   private static final long MAXIMUM_INDICATOR_SUSPEND_TIME = 240000L;

   public static void init() {
      IndicatorManagerImpl imi = new IndicatorManagerImpl();
      imi.instanceInit();
      ApplicationRegistry.getApplicationRegistry().put(IndicatorManager.GUID, imi);
   }

   private void instanceInit() {
      Application app = Application.getApplication();
      app.addRealtimeClockListener(this);
      new GridIndicatorFactory(this).init();
      new HorizontalIndicatorFactory(this).init();
      new UnreadCountComponentFactory(this).init();
   }

   @Override
   public void addIndicator(Indicator indicator) {
      IndicatorDisplay[] sortedIndicators = this._sortedIndicators;
      if (indicator == null) {
         throw new NullPointerException();
      }

      synchronized (sortedIndicators) {
         int length = sortedIndicators.length;
         String typeName = indicator.getTypeName();

         for (int i = 0; i < length; i++) {
            if (indicator == sortedIndicators[i]._indicator) {
               return;
            }

            if (typeName.equals(sortedIndicators[i]._indicator.getTypeName())) {
               return;
            }
         }

         Array.resize(sortedIndicators, length + 1);
         sortedIndicators[length] = new IndicatorDisplay(indicator);
         Arrays.sort(sortedIndicators, this._indicatorComparator);
      }

      this.doUpdateIndicators();
   }

   @Override
   public void removeIndicator(Indicator indicator) {
      IndicatorDisplay[] sortedIndicators = this._sortedIndicators;
      if (indicator == null) {
         throw new NullPointerException();
      }

      synchronized (sortedIndicators) {
         int length = sortedIndicators.length;
         String typeName = indicator.getTypeName();

         for (int i = 0; i < length; i++) {
            if (indicator == sortedIndicators[i]._indicator || typeName.equals(sortedIndicators[i]._indicator.getTypeName())) {
               System.arraycopy(sortedIndicators, i + 1, sortedIndicators, i, length - i - 1);
               Array.resize(sortedIndicators, length - 1);
               length--;
            }
         }
      }

      this.doUpdateIndicators();
   }

   @Override
   public void updateIndicators() {
      if (!this._suspendInfo._suspendFlag) {
         this.doUpdateIndicators();
      }
   }

   private void doUpdateIndicators() {
      this.cleanupIndicatorListeners();
      WeakReference[] wrIndicatorChangedListeners = this._wrIndicatorChangedListeners;
      synchronized (wrIndicatorChangedListeners) {
         int count = wrIndicatorChangedListeners.length;

         for (int i = 0; i < count; i++) {
            RibbonComponent$RibbonComponentChangeListener changeListener = (RibbonComponent$RibbonComponentChangeListener)wrIndicatorChangedListeners[i].get();
            if (changeListener != null) {
               changeListener.ribbonComponentChanged(null);
            }
         }
      }
   }

   @Override
   public void suspendIndicatorUpdates() {
      synchronized (this._suspendInfo) {
         this._suspendInfo._suspendFlag = true;
         this._suspendInfo._suspendTime = System.currentTimeMillis();
      }
   }

   @Override
   public boolean isIndicatorUpdatingSuspended() {
      return this._suspendInfo._suspendFlag;
   }

   @Override
   public void resumeIndicatorUpdates() {
      this._suspendInfo._suspendFlag = false;
      this.doUpdateIndicators();
   }

   private void cleanupIndicatorListeners() {
      if (Monitor.monitorOwned(this._wrIndicatorChangedListeners)) {
         throw new RuntimeException("cleanupIndicatorListeners() called while holding a lock on the listeners");
      }

      WeakReference[] wrIndicatorChangedListeners = this._wrIndicatorChangedListeners;
      synchronized (wrIndicatorChangedListeners) {
         int count = wrIndicatorChangedListeners.length;
         int i = 0;

         while (i < count) {
            Object changeListener = wrIndicatorChangedListeners[i].get();
            if (changeListener != null) {
               i++;
            } else {
               wrIndicatorChangedListeners[i] = wrIndicatorChangedListeners[--count];
            }
         }

         if (count != wrIndicatorChangedListeners.length) {
            Array.resize(wrIndicatorChangedListeners, count);
         }
      }
   }

   void registerFieldForUpdates(RibbonComponent$RibbonComponentChangeListener changeListener) {
      WeakReference[] wrIndicatorChangedListeners = this._wrIndicatorChangedListeners;
      if (changeListener == null) {
         throw new NullPointerException();
      }

      synchronized (wrIndicatorChangedListeners) {
         int len = wrIndicatorChangedListeners.length;
         Array.resize(wrIndicatorChangedListeners, len + 1);
         wrIndicatorChangedListeners[len] = new WeakReference(changeListener);
      }

      this.cleanupIndicatorListeners();
   }

   int getIndicators(IndicatorDisplay[] mostImportantIndicators, Graphics graphics) {
      return this.getIndicators(mostImportantIndicators, graphics, 0, "", false);
   }

   CountIndicator getCountIndicator(String typename) {
      IndicatorDisplay[] sortedIndicators = this._sortedIndicators;
      synchronized (sortedIndicators) {
         for (int i = sortedIndicators.length - 1; i >= 0; i--) {
            Indicator indicator = sortedIndicators[i]._indicator;
            if (indicator instanceof CountIndicator && typename.equals(indicator.getTypeName())) {
               return (CountIndicator)indicator;
            }
         }

         return null;
      }
   }

   @Override
   public void resetIndicatorAreas() {
      IndicatorDisplay[] sortedIndicators = this._sortedIndicators;
      synchronized (sortedIndicators) {
         for (int i = sortedIndicators.length - 1; i >= 0; i--) {
            sortedIndicators[i]._area = 0;
         }
      }
   }

   int getIndicators(IndicatorDisplay[] mostImportantIndicators, Graphics graphics, int intendedArea, String typeName, boolean omit) {
      IndicatorDisplay[] sortedIndicators = this._sortedIndicators;
      synchronized (sortedIndicators) {
         int maxNeeded = mostImportantIndicators.length;
         int maxAvailable = sortedIndicators.length;
         int iNeeded = 0;

         for (int iAvailable = 0; iNeeded < maxNeeded && iAvailable < maxAvailable; iAvailable++) {
            try {
               Indicator indicator = sortedIndicators[iAvailable]._indicator;
               int area = sortedIndicators[iAvailable]._area;
               int width = indicator.getWidth(graphics);
               if (width != 0) {
                  boolean add = area == 0 || intendedArea == 0 || area == intendedArea;
                  boolean justThisOne = false;
                  if (typeName.length() != 0) {
                     String indicatorName = indicator.getTypeName();
                     char delimeter = typeName.charAt(0);
                     boolean found = false;

                     for (int index = typeName.indexOf(indicatorName, 0); index > -1; index = typeName.indexOf(indicatorName, index + 1)) {
                        if (typeName.charAt(index - 1) == delimeter && typeName.charAt(index + indicatorName.length()) == delimeter) {
                           found = true;
                           break;
                        }
                     }

                     if (omit && !found && add) {
                        add = true;
                     } else if (!omit && found) {
                        add = true;
                        justThisOne = true;
                     } else {
                        add = false;
                     }
                  }

                  if (add) {
                     mostImportantIndicators[iNeeded] = sortedIndicators[iAvailable];
                     iNeeded++;
                     sortedIndicators[iAvailable]._area = 0;
                     if (justThisOne) {
                        break;
                     }
                  }
               }
            } finally {
               continue;
            }
         }

         return iNeeded;
      }
   }

   private void checkForAutoResume() {
      synchronized (this._suspendInfo) {
         if (this._suspendInfo._suspendFlag) {
            long difference = System.currentTimeMillis() - this._suspendInfo._suspendTime;
            if (difference < 0 || difference > 240000) {
               this._suspendInfo._suspendFlag = false;
               this.doUpdateIndicators();
            }
         }
      }
   }

   @Override
   public void clockUpdated() {
      this.checkForAutoResume();
   }

   @Override
   public void test(Object id, Object value) {
      String typeName = null;
      if (id instanceof String) {
         typeName = (String)id;
         IndicatorDisplay[] sortedIndicators = this._sortedIndicators;
         synchronized (sortedIndicators) {
            for (int i = sortedIndicators.length - 1; i >= 0; i--) {
               try {
                  Indicator indicator = sortedIndicators[i]._indicator;
                  if (typeName.equals(indicator.getTypeName())) {
                     if (indicator instanceof TestPoint) {
                        TestPoint tp = (TestPoint)indicator;
                        tp.test(id, value);
                     }
                     break;
                  }
               } finally {
                  continue;
               }
            }
         }

         this.updateIndicators();
      }
   }
}
